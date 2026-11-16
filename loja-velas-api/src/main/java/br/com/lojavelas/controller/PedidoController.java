package br.com.lojavelas.controller;

import br.com.lojavelas.dto.*;
import br.com.lojavelas.model.Pagamento;
import br.com.lojavelas.model.Pedido;
import br.com.lojavelas.model.enums.StatusPedido;
import br.com.lojavelas.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos, pagamentos e entregas")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    // ---------- Pedidos ----------

    /** Fecha o pedido com os itens do carrinho do cliente. */
    @PostMapping("/clientes/{idCliente}/pedidos")
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido checkout(@PathVariable Integer idCliente, @RequestBody @Valid CheckoutRequest req) {
        return service.checkout(idCliente, req);
    }

    @GetMapping("/clientes/{idCliente}/pedidos")
    public List<Pedido> listarDoCliente(@PathVariable Integer idCliente) {
        return service.listarDoCliente(idCliente);
    }

    @GetMapping("/pedidos/{id}")
    public Pedido buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PatchMapping("/pedidos/{id}/status")
    public Pedido alterarStatus(@PathVariable Integer id, @RequestBody @Valid StatusPedidoRequest req) {
        return service.alterarStatus(id, req.status());
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public Pedido cancelar(@PathVariable Integer id) {
        return service.alterarStatus(id, StatusPedido.CANCELADO);
    }

    // ---------- Pagamentos ----------

    @PostMapping("/pedidos/{id}/pagamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public Pagamento registrarPagamento(@PathVariable Integer id, @RequestBody @Valid PagamentoRequest req) {
        return service.registrarPagamento(id, req);
    }

    @PatchMapping("/pagamentos/{idPagamento}/status")
    public Pagamento atualizarPagamento(@PathVariable Integer idPagamento,
                                        @RequestBody @Valid StatusPagamentoRequest req) {
        return service.atualizarPagamento(idPagamento, req);
    }

    // ---------- Entrega ----------

    @PutMapping("/pedidos/{id}/entrega")
    public Pedido registrarEnvio(@PathVariable Integer id, @RequestBody @Valid EntregaRequest req) {
        return service.registrarEnvio(id, req);
    }

    @PatchMapping("/pedidos/{id}/entrega/confirmar")
    public Pedido confirmarEntrega(@PathVariable Integer id) {
        return service.confirmarEntrega(id);
    }
}
