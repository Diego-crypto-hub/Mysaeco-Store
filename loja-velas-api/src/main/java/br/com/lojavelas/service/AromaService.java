package br.com.lojavelas.service;

import br.com.lojavelas.dto.AromaRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Aroma;
import br.com.lojavelas.repository.AromaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AromaService {

    private final AromaRepository aromaRepository;

    public List<Aroma> listar() {
        return aromaRepository.findAll(Sort.by("nome"));
    }

    public Aroma buscar(Integer id) {
        return aromaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aroma", id));
    }

    @Transactional
    public Aroma criar(AromaRequest req) {
        if (aromaRepository.existsByNomeIgnoreCase(req.nome())) {
            throw new BusinessException("Já existe um aroma com esse nome");
        }
        Aroma a = new Aroma();
        a.setNome(req.nome());
        a.setFamiliaOlfativa(req.familiaOlfativa());
        return aromaRepository.save(a);
    }

    @Transactional
    public Aroma atualizar(Integer id, AromaRequest req) {
        Aroma a = buscar(id);
        a.setNome(req.nome());
        a.setFamiliaOlfativa(req.familiaOlfativa());
        return a;
    }

    @Transactional
    public void excluir(Integer id) {
        aromaRepository.delete(buscar(id)); // as variações ficam com aroma NULL (ON DELETE SET NULL)
    }
}
