package br.com.lojavelas.repository;

import br.com.lojavelas.model.VariacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariacaoProdutoRepository extends JpaRepository<VariacaoProduto, Integer> {

    boolean existsBySku(String sku);
}
