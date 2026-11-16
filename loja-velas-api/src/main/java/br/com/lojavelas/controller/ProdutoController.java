package br.com.lojavelas.controller;

import br.com.lojavelas.dto.*;
import br.com.lojavelas.model.Avaliacao;
import br.com.lojavelas.model.ImagemProduto;
import br.com.lojavelas.model.Produto;
import br.com.lojavelas.model.VariacaoProduto;
import br.com.lojavelas.service.AvaliacaoService;
import br.com.lojavelas.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Produtos")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final AvaliacaoService avaliacaoService;

    // ---------- Produtos ----------

    /** Catálogo: GET /api/produtos?categoria=1&aroma=2&busca=lavanda&page=0&size=12&sort=nome */
    @GetMapping("/produtos")
    public Page<Produto> listar(@RequestParam(required = false) Integer categoria,
                                @RequestParam(required = false) Integer aroma,
                                @RequestParam(required = false) String busca,
                                @PageableDefault(size = 12, sort = "nome") Pageable pageable) {
        return produtoService.listar(categoria, aroma, busca, pageable);
    }

    @GetMapping("/produtos/{id}")
    public Produto buscar(@PathVariable Integer id) {
        return produtoService.buscar(id);
    }

    @PostMapping("/produtos")
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criar(@RequestBody @Valid ProdutoRequest req) {
        return produtoService.criar(req);
    }

    @PutMapping("/produtos/{id}")
    public Produto atualizar(@PathVariable Integer id, @RequestBody @Valid ProdutoRequest req) {
        return produtoService.atualizar(id, req);
    }

    @DeleteMapping("/produtos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Integer id) {
        produtoService.desativar(id);
    }

    // ---------- Variações ----------

    @PostMapping("/produtos/{id}/variacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public VariacaoProduto adicionarVariacao(@PathVariable Integer id, @RequestBody @Valid VariacaoRequest req) {
        return produtoService.adicionarVariacao(id, req);
    }

    @GetMapping("/variacoes/{idVariacao}")
    public VariacaoProduto buscarVariacao(@PathVariable Integer idVariacao) {
        return produtoService.buscarVariacao(idVariacao);
    }

    @PutMapping("/variacoes/{idVariacao}")
    public VariacaoProduto atualizarVariacao(@PathVariable Integer idVariacao,
                                             @RequestBody @Valid VariacaoRequest req) {
        return produtoService.atualizarVariacao(idVariacao, req);
    }

    @PatchMapping("/variacoes/{idVariacao}/estoque")
    public VariacaoProduto atualizarEstoque(@PathVariable Integer idVariacao,
                                            @RequestBody @Valid EstoqueRequest req) {
        return produtoService.atualizarEstoque(idVariacao, req);
    }

    @DeleteMapping("/variacoes/{idVariacao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarVariacao(@PathVariable Integer idVariacao) {
        produtoService.desativarVariacao(idVariacao);
    }

    // ---------- Imagens ----------

    @PostMapping("/produtos/{id}/imagens")
    @ResponseStatus(HttpStatus.CREATED)
    public ImagemProduto adicionarImagem(@PathVariable Integer id, @RequestBody @Valid ImagemRequest req) {
        return produtoService.adicionarImagem(id, req);
    }

    @DeleteMapping("/imagens/{idImagem}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerImagem(@PathVariable Integer idImagem) {
        produtoService.removerImagem(idImagem);
    }

    // ---------- Avaliações ----------

    @GetMapping("/produtos/{id}/avaliacoes")
    public List<Avaliacao> listarAvaliacoes(@PathVariable Integer id) {
        return avaliacaoService.listarAprovadas(id);
    }

    @PostMapping("/produtos/{id}/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Avaliacao avaliar(@PathVariable Integer id, @RequestBody @Valid AvaliacaoRequest req) {
        return avaliacaoService.criar(id, req);
    }

    @GetMapping("/avaliacoes/pendentes")
    public List<Avaliacao> listarPendentes() {
        return avaliacaoService.listarPendentes();
    }

    @PatchMapping("/avaliacoes/{idAvaliacao}/aprovar")
    public Avaliacao aprovar(@PathVariable Integer idAvaliacao) {
        return avaliacaoService.aprovar(idAvaliacao);
    }

    @DeleteMapping("/avaliacoes/{idAvaliacao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirAvaliacao(@PathVariable Integer idAvaliacao) {
        avaliacaoService.excluir(idAvaliacao);
    }
}
