package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemPedido {

    @JsonIgnore
    @EmbeddedId
    private ItemPedidoId id = new ItemPedidoId();

    @JsonIgnore
    @MapsId("idPedido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @MapsId("idVariacao")
    @ManyToOne
    @JoinColumn(name = "id_variacao")
    private VariacaoProduto variacao;

    @Column(nullable = false)
    private Integer quantidade;

    /** Preço congelado no momento da compra. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    public ItemPedido(Pedido pedido, VariacaoProduto variacao, Integer quantidade, BigDecimal precoUnitario) {
        this.pedido = pedido;
        this.variacao = variacao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
