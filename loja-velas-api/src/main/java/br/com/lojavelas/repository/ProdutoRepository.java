package br.com.lojavelas.repository;

import br.com.lojavelas.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    /**
     * Busca do catálogo com filtros opcionais (categoria, aroma e texto no nome).
     * O parâmetro "busca" recebe "" quando não há filtro de texto.
     */
    @Query(value = """
            select distinct p from Produto p
            left join p.variacoes v
            where p.ativo = true
              and (:idCategoria is null or p.categoria.id = :idCategoria)
              and (:idAroma is null or v.aroma.id = :idAroma)
              and lower(p.nome) like lower(concat('%', :busca, '%'))
            """,
            countQuery = """
            select count(distinct p) from Produto p
            left join p.variacoes v
            where p.ativo = true
              and (:idCategoria is null or p.categoria.id = :idCategoria)
              and (:idAroma is null or v.aroma.id = :idAroma)
              and lower(p.nome) like lower(concat('%', :busca, '%'))
            """)
    Page<Produto> buscar(@Param("idCategoria") Integer idCategoria,
                         @Param("idAroma") Integer idAroma,
                         @Param("busca") String busca,
                         Pageable pageable);
}
