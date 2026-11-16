package br.com.lojavelas.controller;

import br.com.lojavelas.dto.AromaRequest;
import br.com.lojavelas.model.Aroma;
import br.com.lojavelas.service.AromaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Aromas")
@RestController
@RequestMapping("/api/aromas")
@RequiredArgsConstructor
public class AromaController {

    private final AromaService service;

    @GetMapping
    public List<Aroma> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Aroma buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aroma criar(@RequestBody @Valid AromaRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public Aroma atualizar(@PathVariable Integer id, @RequestBody @Valid AromaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        service.excluir(id);
    }
}
