package br.com.lojavelas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lista_desejos")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ListaDesejos {

    @JsonIgnore
    @EmbeddedId
    private ListaDesejosId id = new ListaDesejosId();

    @JsonIgnore
    @MapsId("idCliente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @MapsId("idProduto")
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAdicao;

    public ListaDesejos(Cliente cliente, Produto produto) {
        this.id = new ListaDesejosId(cliente.getId(), produto.getId());
        this.cliente = cliente;
        this.produto = produto;
    }

    @PrePersist
    void prePersist() {
        if (dataAdicao == null) dataAdicao = LocalDateTime.now();
    }
}
