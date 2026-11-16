package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import br.com.lojavelas.model.enums.Tamanho;

import java.math.BigDecimal;

@Entity
@Table(name = "variacao_produto")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VariacaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variacao")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_aroma")
    private Aroma aroma;

    @Column(nullable = false, unique = true, length = 30)
    private String sku;

    @Enumerated(EnumType.STRING)
    private Tamanho tamanho;

    private Integer pesoGramas;
    private String cor;
    private Integer tempoQueimaHoras;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoPromocional;

    @Column(nullable = false)
    private Integer qtdEstoque = 0;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Integer getIdProduto() {
        return produto == null ? null : produto.getId();
    }

    public String getNomeProduto() {
        return produto == null ? null : produto.getNome();
    }

    /** Preço efetivamente cobrado: o promocional, se houver. */
    public BigDecimal getPrecoFinal() {
        return precoPromocional != null ? precoPromocional : preco;
    }
}
