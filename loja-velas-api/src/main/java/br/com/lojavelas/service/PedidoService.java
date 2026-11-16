package br.com.lojavelas.service;

import br.com.lojavelas.dto.CheckoutRequest;
import br.com.lojavelas.dto.EntregaRequest;
import br.com.lojavelas.dto.PagamentoRequest;
import br.com.lojavelas.dto.StatusPagamentoRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.*;
import br.com.lojavelas.model.enums.FormaPagamento;
import br.com.lojavelas.model.enums.StatusPagamento;
import br.com.lojavelas.model.enums.StatusPedido;
import br.com.lojavelas.repository.CarrinhoRepository;
import br.com.lojavelas.repository.PagamentoRepository;
import br.com.lojavelas.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ClienteService clienteService;
    private final CupomService cupomService;

    public List<Pedido> listarDoCliente(Integer idCliente) {
        clienteService.buscar(idCliente);
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(idCliente);
    }

    public Pedido buscar(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pedido", id));
    }

    /**
     * Fecha o pedido a partir do carrinho do cliente: confere e baixa o estoque,
     * congela os preços, aplica o cupom e esvazia o carrinho. Tudo numa transação só.
     */
    @Transactional
    public Pedido checkout(Integer idCliente, CheckoutRequest req) {
        Cliente cliente = clienteService.buscar(idCliente);
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo");
        }
        Endereco endereco = clienteService.buscarEndereco(idCliente, req.idEndereco());
        Carrinho carrinho = carrinhoRepository.findByClienteId(idCliente)
                .filter(c -> !c.getItens().isEmpty())
                .orElseThrow(() -> new BusinessException("O carrinho está vazio"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEndereco(endereco);
        pedido.setEmbalagemPresente(Boolean.TRUE.equals(req.embalagemPresente()));
        pedido.setMensagemPresente(req.mensagemPresente());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrinho item : carrinho.getItens()) {
            VariacaoProduto v = item.getVariacao();
            if (!v.getAtivo() || !v.getProduto().getAtivo()) {
                throw new BusinessException("O item " + v.getSku() + " não está mais disponível");
            }
            if (v.getQtdEstoque() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o SKU " + v.getSku()
                        + " (disponível: " + v.getQtdEstoque() + ")");
            }
            v.setQtdEstoque(v.getQtdEstoque() - item.getQuantidade());
            ItemPedido ip = new ItemPedido(pedido, v, item.getQuantidade(), v.getPrecoFinal());
            pedido.getItens().add(ip);
            subtotal = subtotal.add(ip.getSubtotal());
        }

        BigDecimal desconto = BigDecimal.ZERO;
        if (req.codigoCupom() != null && !req.codigoCupom().isBlank()) {
            Cupom cupom = cupomService.validar(req.codigoCupom(), subtotal);
            desconto = cupomService.calcularDesconto(cupom, subtotal);
            cupom.setQtdUsada(cupom.getQtdUsada() + 1);
            pedido.setCupom(cupom);
        }
        BigDecimal frete = req.valorFrete() == null ? BigDecimal.ZERO : req.valorFrete();

        pedido.setValorSubtotal(subtotal);
        pedido.setValorDesconto(desconto);
        pedido.setValorFrete(frete);
        pedido.setValorTotal(subtotal.subtract(desconto).add(frete));
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        Pedido salvo = pedidoRepository.save(pedido);
        carrinho.getItens().clear();
        carrinho.setDataAtualizacao(LocalDateTime.now());
        return salvo;
    }

    @Transactional
    public Pedido alterarStatus(Integer idPedido, StatusPedido novo) {
        Pedido pedido = buscar(idPedido);
        mudarStatus(pedido, novo);
        return pedido;
    }

    // ---------------- Pagamentos ----------------

    @Transactional
    public Pagamento registrarPagamento(Integer idPedido, PagamentoRequest req) {
        Pedido pedido = buscar(idPedido);
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new BusinessException("Este pedido não está aguardando pagamento");
        }
        boolean temPendente = pedido.getPagamentos().stream()
                .anyMatch(p -> p.getStatus() == StatusPagamento.PENDENTE);
        if (temPendente) {
            throw new BusinessException("Já existe um pagamento pendente para este pedido");
        }
        int parcelas = req.parcelas() == null ? 1 : req.parcelas();
        if (parcelas > 1 && req.formaPagamento() != FormaPagamento.CARTAO_CREDITO) {
            throw new BusinessException("Parcelamento só é permitido no cartão de crédito");
        }
        Pagamento pg = new Pagamento();
        pg.setPedido(pedido);
        pg.setFormaPagamento(req.formaPagamento());
        pg.setParcelas(parcelas);
        pg.setValor(pedido.getValorTotal());
        pg.setStatus(StatusPagamento.PENDENTE);
        pedido.getPagamentos().add(pg);
        return pagamentoRepository.save(pg);
    }

    /**
     * Atualiza o status do pagamento (normalmente chamado pelo retorno do gateway).
     * APROVADO marca o pedido como PAGO; ESTORNADO cancela o pedido e devolve o estoque.
     */
    @Transactional
    public Pagamento atualizarPagamento(Integer idPagamento, StatusPagamentoRequest req) {
        Pagamento pg = pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> new NotFoundException("Pagamento", idPagamento));
        StatusPagamento atual = pg.getStatus();
        StatusPagamento novo = req.status();

        boolean permitido = (atual == StatusPagamento.PENDENTE
                && (novo == StatusPagamento.APROVADO || novo == StatusPagamento.RECUSADO))
                || (atual == StatusPagamento.APROVADO && novo == StatusPagamento.ESTORNADO);
        if (!permitido) {
            throw new BusinessException("Não é possível mudar o pagamento de " + atual + " para " + novo);
        }

        pg.setStatus(novo);
        if (req.idTransacaoGateway() != null) {
            pg.setIdTransacaoGateway(req.idTransacaoGateway());
        }
        Pedido pedido = pg.getPedido();
        if (novo == StatusPagamento.APROVADO) {
            pg.setDataPagamento(LocalDateTime.now());
            mudarStatus(pedido, StatusPedido.PAGO);
        } else if (novo == StatusPagamento.ESTORNADO && pedido.getStatus() != StatusPedido.CANCELADO) {
            mudarStatus(pedido, StatusPedido.CANCELADO);
        }
        return pg;
    }

    // ---------------- Entrega ----------------

    @Transactional
    public Pedido registrarEnvio(Integer idPedido, EntregaRequest req) {
        Pedido pedido = buscar(idPedido);
        Entrega entrega = pedido.getEntrega();
        if (entrega == null) {
            entrega = new Entrega();
            entrega.setPedido(pedido);
            pedido.setEntrega(entrega);
        }
        entrega.setTransportadora(req.transportadora());
        entrega.setModalidade(req.modalidade());
        entrega.setCodigoRastreio(req.codigoRastreio());
        entrega.setPrazoDias(req.prazoDias());
        if (pedido.getStatus() != StatusPedido.ENVIADO) {
            mudarStatus(pedido, StatusPedido.ENVIADO); // valida se o pedido já foi pago
            entrega.setDataEnvio(LocalDateTime.now());
        }
        return pedido;
    }

    @Transactional
    public Pedido confirmarEntrega(Integer idPedido) {
        Pedido pedido = buscar(idPedido);
        if (pedido.getEntrega() == null) {
            throw new BusinessException("O pedido ainda não foi enviado");
        }
        mudarStatus(pedido, StatusPedido.ENTREGUE);
        pedido.getEntrega().setDataEntrega(LocalDateTime.now());
        return pedido;
    }

    // ---------------- Regras de status ----------------

    private void mudarStatus(Pedido pedido, StatusPedido novo) {
        StatusPedido atual = pedido.getStatus();
        if (!atual.podeIrPara(novo)) {
            throw new BusinessException("Não é possível mudar o pedido de " + atual + " para " + novo);
        }
        if (novo == StatusPedido.CANCELADO) {
            devolverEstoque(pedido);
        }
        pedido.setStatus(novo);
    }

    private void devolverEstoque(Pedido pedido) {
        for (ItemPedido ip : pedido.getItens()) {
            VariacaoProduto v = ip.getVariacao();
            v.setQtdEstoque(v.getQtdEstoque() + ip.getQuantidade());
        }
        if (pedido.getCupom() != null) {
            Cupom c = pedido.getCupom();
            c.setQtdUsada(Math.max(0, c.getQtdUsada() - 1));
        }
    }
}
