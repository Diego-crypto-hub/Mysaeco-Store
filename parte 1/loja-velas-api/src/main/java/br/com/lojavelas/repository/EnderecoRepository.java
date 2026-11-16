package br.com.lojavelas.repository;

import br.com.lojavelas.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

    List<Endereco> findByClienteId(Integer idCliente);

    Optional<Endereco> findByIdAndClienteId(Integer id, Integer idCliente);
}
