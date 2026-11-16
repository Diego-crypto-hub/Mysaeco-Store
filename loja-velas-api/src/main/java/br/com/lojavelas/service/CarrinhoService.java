package br.com.lojavelas.service;

import br.com.lojavelas.dto.ItemCarrinhoRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Carrinho;
import br.com.lojavelas.model.ItemCarrinho;
import br.com.lojavelas.model.VariacaoProduto;
import br.com.lojavelas.repository.CarrinhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    /** Retorna o carrinho do cliente, criando um vazio na primeira vez. */
    @Transactional
    public Carrinho obter(Integer idCliente) {
        return carrinhoRepository.findByClienteId(idCliente).orElseGet(() -> {
            Carrinho c = new Carrinho();
            c.setCliente(clienteService.buscar(idCliente));
            return carrinhoRepository.save(c);
        });
    }

    @Transactional
    public Carrinho adicionarItem(Integer idCliente, ItemCarrinhoRequest req) {
        Carrinho carrinho = obter(idCliente);
        VariacaoProduto variacao = produtoService.buscarVariacao(req.idVariacao());
        if (!variacao.getAtivo() || !variacao.getProduto().getAtivo()) {
            throw new BusinessException("Este produto não está mais disponível");
        }
        Optional<ItemCarrinho> existente = procurarItem(carrinho, req.idVariacao());
        int novaQtd = req.quantidade() + existente.map(ItemCarrinho::getQuantidade).orElse(0);
        validarEstoque(variacao, novaQtd);

        if (existente.isPresent()) {
            existente.get().setQuantidade(novaQtd);
        } else {
            carrinho.getItens().add(new ItemCarrinho(carrinho, variacao, novaQtd));
        }
        carrinho.setDataAtualizacao(LocalDateTime.now());
        return carrinho;
    }

    @Transactional
    public Carrinho alterarQuantidade(Integer idCliente, Integer idVariacao, Integer quantidade) {
        Carrinho carrinho = obter(idCliente);
        ItemCarrinho item = procurarItem(carrinho, idVariacao)
                .orElseThrow(() -> new NotFoundException("Item do carrinho (variação)", idVariacao));
        validarEstoque(item.getVariacao(), quantidade);
        item.setQuantidade(quantidade);
        carrinho.setDataAtualizacao(LocalDateTime.now());
        return carrinho;
    }

    @Transactional
    public Carrinho removerItem(Integer idCliente, Integer idVariacao) {
        Carrinho carrinho = obter(idCliente);
        boolean removeu = carrinho.getItens()
                .removeIf(i -> i.getVariacao().getId().equals(idVariacao));
        if (!removeu) {
            throw new NotFoundException("Item do carrinho (variação)", idVariacao);
        }
        carrinho.setDataAtualizacao(LocalDateTime.now());
        return carrinho;
    }

    @Transactional
    public void esvaziar(Integer idCliente) {
        Carrinho carrinho = obter(idCliente);
        carrinho.getItens().clear();
        carrinho.setDataAtualizacao(LocalDateTime.now());
    }

    private Optional<ItemCarrinho> procurarItem(Carrinho carrinho, Integer idVariacao) {
        return carrinho.getItens().stream()
                .filter(i -> i.getVariacao().getId().equals(idVariacao))
                .findFirst();
    }

    private void validarEstoque(VariacaoProduto v, int quantidade) {
        if (quantidade > v.getQtdEstoque()) {
            throw new BusinessException("Estoque insuficiente para o SKU " + v.getSku()
                    + " (disponível: " + v.getQtdEstoque() + ")");
        }
    }
}
