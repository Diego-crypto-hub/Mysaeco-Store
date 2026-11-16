package br.com.lojavelas.repository;

import br.com.lojavelas.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {

    List<Avaliacao> findByProdutoIdAndAprovadaTrueOrderByDataAvaliacaoDesc(Integer idProduto);

    List<Avaliacao> findByAprovadaFalseOrderByDataAvaliacaoAsc();

    boolean existsByClienteIdAndProdutoId(Integer idCliente, Integer idProduto);
}
