package br.com.lojavelas.service;

import br.com.lojavelas.dto.CategoriaRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Categoria;
import br.com.lojavelas.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listar() {
        return categoriaRepository.findAll(Sort.by("nome"));
    }

    public Categoria buscar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria", id));
    }

    @Transactional
    public Categoria criar(CategoriaRequest req) {
        if (categoriaRepository.existsByNomeIgnoreCase(req.nome())) {
            throw new BusinessException("Já existe uma categoria com esse nome");
        }
        Categoria c = new Categoria();
        preencher(c, req);
        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria atualizar(Integer id, CategoriaRequest req) {
        Categoria c = buscar(id);
        if (id.equals(req.idCategoriaPai())) {
            throw new BusinessException("Uma categoria não pode ser subcategoria dela mesma");
        }
        preencher(c, req);
        return c;
    }

    @Transactional
    public void excluir(Integer id) {
        categoriaRepository.delete(buscar(id));
        categoriaRepository.flush(); // erro 409 se houver produtos na categoria
    }

    private void preencher(Categoria c, CategoriaRequest req) {
        c.setNome(req.nome());
        c.setDescricao(req.descricao());
        c.setCategoriaPai(req.idCategoriaPai() == null ? null : buscar(req.idCategoriaPai()));
    }
}
