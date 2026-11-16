package br.com.lojavelas.controller;

import br.com.lojavelas.dto.CupomRequest;
import br.com.lojavelas.dto.CupomValidacaoResponse;
import br.com.lojavelas.model.Cupom;
import br.com.lojavelas.service.CupomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Cupons")
@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomService service;

    @GetMapping
    public List<Cupom> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Cupom buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    /** Simula o desconto: GET /api/cupons/validar/BEMVINDO10?subtotal=150.00 */
    @GetMapping("/validar/{codigo}")
    public CupomValidacaoResponse validar(@PathVariable String codigo, @RequestParam BigDecimal subtotal) {
        return service.simular(codigo, subtotal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cupom criar(@RequestBody @Valid CupomRequest req) {
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public Cupom atualizar(@PathVariable Integer id, @RequestBody @Valid CupomRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        service.excluir(id);
    }
}
