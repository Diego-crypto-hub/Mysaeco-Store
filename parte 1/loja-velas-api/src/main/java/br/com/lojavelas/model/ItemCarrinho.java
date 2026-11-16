package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "item_carrinho")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ItemCarrinho {

    @JsonIgnore
    @EmbeddedId
    private ItemCarrinhoId id = new ItemCarrinhoId();

    @JsonIgnore
    @MapsId("idCarrinho")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrinho")
    private Carrinho carrinho;

    @MapsId("idVariacao")
    @ManyToOne
    @JoinColumn(name = "id_variacao")
    private VariacaoProduto variacao;

    @Column(nullable = false)
    private Integer quantidade;

    public ItemCarrinho(Carrinho carrinho, VariacaoProduto variacao, Integer quantidade) {
        this.id = new ItemCarrinhoId(carrinho.getId(), variacao.getId());
        this.carrinho = carrinho;
        this.variacao = variacao;
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return variacao.getPrecoFinal().multiply(BigDecimal.valueOf(quantidade));
    }
}
