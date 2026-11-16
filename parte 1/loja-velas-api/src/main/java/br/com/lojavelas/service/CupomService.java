package br.com.lojavelas.service;

import br.com.lojavelas.dto.CupomRequest;
import br.com.lojavelas.dto.CupomValidacaoResponse;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Cupom;
import br.com.lojavelas.model.enums.TipoDesconto;
import br.com.lojavelas.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CupomService {

    private final CupomRepository cupomRepository;

    public List<Cupom> listar() {
        return cupomRepository.findAll();
    }

    public Cupom buscar(Integer id) {
        return cupomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cupom", id));
    }

    @Transactional
    public Cupom criar(CupomRequest req) {
        if (cupomRepository.existsByCodigoIgnoreCase(req.codigo())) {
            throw new BusinessException("Já existe um cupom com esse código");
        }
        Cupom c = new Cupom();
        preencher(c, req);
        return cupomRepository.save(c);
    }

    @Transactional
    public Cupom atualizar(Integer id, CupomRequest req) {
        Cupom c = buscar(id);
        preencher(c, req);
        return c;
    }

    @Transactional
    public void excluir(Integer id) {
        cupomRepository.delete(buscar(id)); // pedidos antigos ficam com cupom NULL
    }

    /** Usado pelo front para mostrar o desconto antes de fechar o pedido. */
    public CupomValidacaoResponse simular(String codigo, BigDecimal subtotal) {
        Cupom cupom = validar(codigo, subtotal);
        BigDecimal desconto = calcularDesconto(cupom, subtotal);
        return new CupomValidacaoResponse(cupom.getCodigo(), subtotal, desconto, subtotal.subtract(desconto));
    }

    /** Confere se o cupom existe, está no prazo, tem usos disponíveis e se o pedido atinge o mínimo. */
    public Cupom validar(String codigo, BigDecimal subtotal) {
        Cupom c = cupomRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new BusinessException("Cupom inválido: " + codigo));
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isBefore(c.getDataInicio()) || agora.isAfter(c.getDataFim())) {
            throw new BusinessException("Cupom fora do período de validade");
        }
        if (c.getLimiteUso() != null && c.getQtdUsada() >= c.getLimiteUso()) {
            throw new BusinessException("Cupom esgotado");
        }
        if (c.getValorMinimoPedido() != null && subtotal.compareTo(c.getValorMinimoPedido()) < 0) {
            throw new BusinessException("Pedido abaixo do valor mínimo do cupom (R$ "
                    + c.getValorMinimoPedido() + ")");
        }
        return c;
    }

    public BigDecimal calcularDesconto(Cupom c, BigDecimal subtotal) {
        BigDecimal desconto = c.getTipoDesconto() == TipoDesconto.PERCENTUAL
                ? subtotal.multiply(c.getValorDesconto()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : c.getValorDesconto();
        return desconto.min(subtotal); // o desconto nunca passa do subtotal
    }

    private void preencher(Cupom c, CupomRequest req) {
        if (!req.dataFim().isAfter(req.dataInicio())) {
            throw new BusinessException("A data final deve ser posterior à data inicial");
        }
        if (req.tipoDesconto() == TipoDesconto.PERCENTUAL
                && req.valorDesconto().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Desconto percentual não pode passar de 100%");
        }
        c.setCodigo(req.codigo().trim().toUpperCase());
        c.setTipoDesconto(req.tipoDesconto());
        c.setValorDesconto(req.valorDesconto());
        c.setValorMinimoPedido(req.valorMinimoPedido());
        c.setDataInicio(req.dataInicio());
        c.setDataFim(req.dataFim());
        c.setLimiteUso(req.limiteUso());
    }
}
