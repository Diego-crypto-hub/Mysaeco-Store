package br.com.lojavelas.repository;

import br.com.lojavelas.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {

    Optional<Carrinho> findByClienteId(Integer idCliente);
}
