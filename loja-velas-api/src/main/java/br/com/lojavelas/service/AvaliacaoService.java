package br.com.lojavelas.service;

import br.com.lojavelas.dto.AvaliacaoRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Avaliacao;
import br.com.lojavelas.repository.AvaliacaoRepository;
import br.com.lojavelas.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    /** Avaliações públicas (já aprovadas pela moderação). */
    public List<Avaliacao> listarAprovadas(Integer idProduto) {
        produtoService.buscar(idProduto);
        return avaliacaoRepository.findByProdutoIdAndAprovadaTrueOrderByDataAvaliacaoDesc(idProduto);
    }

    public List<Avaliacao> listarPendentes() {
        return avaliacaoRepository.findByAprovadaFalseOrderByDataAvaliacaoAsc();
    }

    @Transactional
    public Avaliacao criar(Integer idProduto, AvaliacaoRequest req) {
        if (avaliacaoRepository.existsByClienteIdAndProdutoId(req.idCliente(), idProduto)) {
            throw new BusinessException("Este cliente já avaliou este produto");
        }
        if (!pedidoRepository.clienteRecebeuProduto(req.idCliente(), idProduto)) {
            throw new BusinessException("Só é possível avaliar produtos de pedidos já entregues");
        }
        Avaliacao a = new Avaliacao();
        a.setCliente(clienteService.buscar(req.idCliente()));
        a.setProduto(produtoService.buscar(idProduto));
        a.setNota(req.nota());
        a.setComentario(req.comentario());
        a.setAprovada(false);
        return avaliacaoRepository.save(a);
    }

    @Transactional
    public Avaliacao aprovar(Integer id) {
        Avaliacao a = buscar(id);
        a.setAprovada(true);
        return a;
    }

    @Transactional
    public void excluir(Integer id) {
        avaliacaoRepository.delete(buscar(id));
    }

    private Avaliacao buscar(Integer id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Avaliação", id));
    }
}
