package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @Column(nullable = false)
    private Integer nota;

    private String comentario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAvaliacao;

    @Column(nullable = false)
    private Boolean aprovada = false;

    public String getNomeCliente() {
        return cliente == null ? null : cliente.getNome();
    }

    public Integer getIdProduto() {
        return produto == null ? null : produto.getId();
    }

    @PrePersist
    void prePersist() {
        if (dataAvaliacao == null) dataAvaliacao = LocalDateTime.now();
    }
}
