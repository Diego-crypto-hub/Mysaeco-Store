package br.com.lojavelas.controller;

import br.com.lojavelas.dto.ItemCarrinhoRequest;
import br.com.lojavelas.dto.QuantidadeRequest;
import br.com.lojavelas.model.Carrinho;
import br.com.lojavelas.model.ListaDesejos;
import br.com.lojavelas.service.CarrinhoService;
import br.com.lojavelas.service.ListaDesejosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Carrinho e lista de desejos")
@RestController
@RequestMapping("/api/clientes/{idCliente}")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final ListaDesejosService listaDesejosService;

    // ---------- Carrinho ----------

    @GetMapping("/carrinho")
    public Carrinho obter(@PathVariable Integer idCliente) {
        return carrinhoService.obter(idCliente);
    }

    @PostMapping("/carrinho/itens")
    public Carrinho adicionarItem(@PathVariable Integer idCliente, @RequestBody @Valid ItemCarrinhoRequest req) {
        return carrinhoService.adicionarItem(idCliente, req);
    }

    @PutMapping("/carrinho/itens/{idVariacao}")
    public Carrinho alterarQuantidade(@PathVariable Integer idCliente, @PathVariable Integer idVariacao,
                                      @RequestBody @Valid QuantidadeRequest req) {
        return carrinhoService.alterarQuantidade(idCliente, idVariacao, req.quantidade());
    }

    @DeleteMapping("/carrinho/itens/{idVariacao}")
    public Carrinho removerItem(@PathVariable Integer idCliente, @PathVariable Integer idVariacao) {
        return carrinhoService.removerItem(idCliente, idVariacao);
    }

    @DeleteMapping("/carrinho")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void esvaziar(@PathVariable Integer idCliente) {
        carrinhoService.esvaziar(idCliente);
    }

    // ---------- Lista de desejos ----------

    @GetMapping("/lista-desejos")
    public List<ListaDesejos> listarDesejos(@PathVariable Integer idCliente) {
        return listaDesejosService.listar(idCliente);
    }

    @PostMapping("/lista-desejos/{idProduto}")
    @ResponseStatus(HttpStatus.CREATED)
    public ListaDesejos adicionarDesejo(@PathVariable Integer idCliente, @PathVariable Integer idProduto) {
        return listaDesejosService.adicionar(idCliente, idProduto);
    }

    @DeleteMapping("/lista-desejos/{idProduto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerDesejo(@PathVariable Integer idCliente, @PathVariable Integer idProduto) {
        listaDesejosService.remover(idCliente, idProduto);
    }
}
