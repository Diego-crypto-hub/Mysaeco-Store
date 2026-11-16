package br.com.lojavelas.controller;

import br.com.lojavelas.dto.ClienteRequest;
import br.com.lojavelas.dto.ClienteUpdateRequest;
import br.com.lojavelas.dto.EnderecoRequest;
import br.com.lojavelas.model.Cliente;
import br.com.lojavelas.model.Endereco;
import br.com.lojavelas.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Clientes")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criar(@RequestBody @Valid ClienteRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Integer id, @RequestBody @Valid ClienteUpdateRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Integer id) {
        service.desativar(id);
    }

    // ---------- Endereços ----------

    @GetMapping("/{id}/enderecos")
    public List<Endereco> listarEnderecos(@PathVariable Integer id) {
        return service.listarEnderecos(id);
    }

    @PostMapping("/{id}/enderecos")
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco adicionarEndereco(@PathVariable Integer id, @RequestBody @Valid EnderecoRequest req) {
        return service.adicionarEndereco(id, req);
    }

    @PutMapping("/{id}/enderecos/{idEndereco}")
    public Endereco atualizarEndereco(@PathVariable Integer id, @PathVariable Integer idEndereco,
                                      @RequestBody @Valid EnderecoRequest req) {
        return service.atualizarEndereco(id, idEndereco, req);
    }

    @DeleteMapping("/{id}/enderecos/{idEndereco}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerEndereco(@PathVariable Integer id, @PathVariable Integer idEndereco) {
        service.removerEndereco(id, idEndereco);
    }
}
