package br.com.lojavelas.repository;

import br.com.lojavelas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
