package br.com.lojavelas.model.enums;

import java.util.Map;
import java.util.Set;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO, PAGO, EM_SEPARACAO, ENVIADO, ENTREGUE, CANCELADO;

    private static final Map<StatusPedido, Set<StatusPedido>> TRANSICOES = Map.of(
            AGUARDANDO_PAGAMENTO, Set.of(PAGO, CANCELADO),
            PAGO, Set.of(EM_SEPARACAO, ENVIADO, CANCELADO),
            EM_SEPARACAO, Set.of(ENVIADO, CANCELADO),
            ENVIADO, Set.of(ENTREGUE),
            ENTREGUE, Set.of(),
            CANCELADO, Set.of()
    );

    /** Indica se o pedido pode sair deste status para o status informado. */
    public boolean podeIrPara(StatusPedido novo) {
        return TRANSICOES.get(this).contains(novo);
    }
}
