package br.com.lojavelas.service;

import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.ListaDesejos;
import br.com.lojavelas.model.ListaDesejosId;
import br.com.lojavelas.repository.ListaDesejosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaDesejosService {

    private final ListaDesejosRepository listaRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public List<ListaDesejos> listar(Integer idCliente) {
        clienteService.buscar(idCliente);
        return listaRepository.findByClienteIdOrderByDataAdicaoDesc(idCliente);
    }

    /** Adiciona o produto; se ele já estiver na lista, devolve o registro existente. */
    @Transactional
    public ListaDesejos adicionar(Integer idCliente, Integer idProduto) {
        ListaDesejosId id = new ListaDesejosId(idCliente, idProduto);
        return listaRepository.findById(id).orElseGet(() -> listaRepository.save(
                new ListaDesejos(clienteService.buscar(idCliente), produtoService.buscar(idProduto))));
    }

    @Transactional
    public void remover(Integer idCliente, Integer idProduto) {
        ListaDesejosId id = new ListaDesejosId(idCliente, idProduto);
        ListaDesejos item = listaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto na lista de desejos", idProduto));
        listaRepository.delete(item);
    }
}
