package br.com.lojavelas.repository;

import br.com.lojavelas.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Integer idCliente);

    /** Verdadeiro se o cliente já recebeu (pedido ENTREGUE) alguma variação do produto. */
    @Query("""
            select case when count(ip) > 0 then true else false end
            from ItemPedido ip
            where ip.pedido.cliente.id = :idCliente
              and ip.variacao.produto.id = :idProduto
              and ip.pedido.status = br.com.lojavelas.model.enums.StatusPedido.ENTREGUE
            """)
    boolean clienteRecebeuProduto(@Param("idCliente") Integer idCliente,
                                  @Param("idProduto") Integer idProduto);
}
