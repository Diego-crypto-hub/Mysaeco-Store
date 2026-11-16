package br.com.lojavelas.service;

import br.com.lojavelas.dto.EstoqueRequest;
import br.com.lojavelas.dto.ImagemRequest;
import br.com.lojavelas.dto.ProdutoRequest;
import br.com.lojavelas.dto.VariacaoRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.ImagemProduto;
import br.com.lojavelas.model.Produto;
import br.com.lojavelas.model.VariacaoProduto;
import br.com.lojavelas.repository.ImagemProdutoRepository;
import br.com.lojavelas.repository.ProdutoRepository;
import br.com.lojavelas.repository.VariacaoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final VariacaoProdutoRepository variacaoRepository;
    private final ImagemProdutoRepository imagemRepository;
    private final CategoriaService categoriaService;
    private final AromaService aromaService;

    // ---------------- Produtos ----------------

    public Page<Produto> listar(Integer idCategoria, Integer idAroma, String busca, Pageable pageable) {
        String texto = busca == null ? "" : busca.trim();
        return produtoRepository.buscar(idCategoria, idAroma, texto, pageable);
    }

    public Produto buscar(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto", id));
    }

    @Transactional
    public Produto criar(ProdutoRequest req) {
        Produto p = new Produto();
        preencher(p, req);
        return produtoRepository.save(p);
    }

    @Transactional
    public Produto atualizar(Integer id, ProdutoRequest req) {
        Produto p = buscar(id);
        preencher(p, req);
        return p;
    }

    /** Exclusão lógica: o produto some do catálogo, mas continua nos pedidos antigos. */
    @Transactional
    public void desativar(Integer id) {
        buscar(id).setAtivo(false);
    }

    private void preencher(Produto p, ProdutoRequest req) {
        p.setCategoria(categoriaService.buscar(req.idCategoria()));
        p.setNome(req.nome());
        p.setDescricao(req.descricao());
        p.setTipoCera(req.tipoCera());
        p.setTipoPavio(req.tipoPavio());
    }

    // ---------------- Variações ----------------

    public VariacaoProduto buscarVariacao(Integer id) {
        return variacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variação", id));
    }

    @Transactional
    public VariacaoProduto adicionarVariacao(Integer idProduto, VariacaoRequest req) {
        Produto produto = buscar(idProduto);
        if (variacaoRepository.existsBySku(req.sku())) {
            throw new BusinessException("SKU já cadastrado: " + req.sku());
        }
        VariacaoProduto v = new VariacaoProduto();
        v.setProduto(produto);
        preencher(v, req);
        return variacaoRepository.save(v);
    }

    @Transactional
    public VariacaoProduto atualizarVariacao(Integer id, VariacaoRequest req) {
        VariacaoProduto v = buscarVariacao(id);
        if (!v.getSku().equals(req.sku()) && variacaoRepository.existsBySku(req.sku())) {
            throw new BusinessException("SKU já cadastrado: " + req.sku());
        }
        preencher(v, req);
        return v;
    }

    @Transactional
    public VariacaoProduto atualizarEstoque(Integer id, EstoqueRequest req) {
        VariacaoProduto v = buscarVariacao(id);
        v.setQtdEstoque(req.qtdEstoque());
        return v;
    }

    @Transactional
    public void desativarVariacao(Integer id) {
        buscarVariacao(id).setAtivo(false);
    }

    private void preencher(VariacaoProduto v, VariacaoRequest req) {
        if (req.precoPromocional() != null && req.precoPromocional().compareTo(req.preco()) >= 0) {
            throw new BusinessException("O preço promocional deve ser menor que o preço normal");
        }
        v.setAroma(req.idAroma() == null ? null : aromaService.buscar(req.idAroma()));
        v.setSku(req.sku());
        v.setTamanho(req.tamanho());
        v.setPesoGramas(req.pesoGramas());
        v.setCor(req.cor());
        v.setTempoQueimaHoras(req.tempoQueimaHoras());
        v.setPreco(req.preco());
        v.setPrecoPromocional(req.precoPromocional());
        v.setQtdEstoque(req.qtdEstoque());
    }

    // ---------------- Imagens ----------------

    @Transactional
    public ImagemProduto adicionarImagem(Integer idProduto, ImagemRequest req) {
        Produto produto = buscar(idProduto);
        ImagemProduto img = new ImagemProduto();
        img.setProduto(produto);
        img.setUrl(req.url());
        img.setOrdem(req.ordem() == null ? produto.getImagens().size() + 1 : req.ordem());
        if (req.idVariacao() != null) {
            VariacaoProduto v = buscarVariacao(req.idVariacao());
            if (!v.getIdProduto().equals(idProduto)) {
                throw new BusinessException("A variação informada não pertence a este produto");
            }
            img.setVariacao(v);
        }
        return imagemRepository.save(img);
    }

    @Transactional
    public void removerImagem(Integer idImagem) {
        ImagemProduto img = imagemRepository.findById(idImagem)
                .orElseThrow(() -> new NotFoundException("Imagem", idImagem));
        imagemRepository.delete(img);
    }
}
