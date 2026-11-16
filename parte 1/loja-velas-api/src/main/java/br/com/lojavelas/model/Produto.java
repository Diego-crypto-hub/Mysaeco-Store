package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import br.com.lojavelas.model.enums.TipoCera;
import br.com.lojavelas.model.enums.TipoPavio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(nullable = false, length = 120)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoCera tipoCera;

    @Enumerated(EnumType.STRING)
    private TipoPavio tipoPavio;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "produto")
    @OrderBy("id")
    private List<VariacaoProduto> variacoes = new ArrayList<>();

    @OneToMany(mappedBy = "produto")
    @OrderBy("ordem")
    private List<ImagemProduto> imagens = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
        if (ativo == null) ativo = true;
    }
}
