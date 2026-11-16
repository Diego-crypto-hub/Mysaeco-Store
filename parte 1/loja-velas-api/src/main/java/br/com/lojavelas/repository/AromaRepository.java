package br.com.lojavelas.repository;

import br.com.lojavelas.model.Aroma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AromaRepository extends JpaRepository<Aroma, Integer> {

    boolean existsByNomeIgnoreCase(String nome);
}
