package br.com.lojavelas.repository;

import br.com.lojavelas.model.ListaDesejos;
import br.com.lojavelas.model.ListaDesejosId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaDesejosRepository extends JpaRepository<ListaDesejos, ListaDesejosId> {

    List<ListaDesejos> findByClienteIdOrderByDataAdicaoDesc(Integer idCliente);
}
