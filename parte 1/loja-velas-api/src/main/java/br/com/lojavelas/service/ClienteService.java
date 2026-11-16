package br.com.lojavelas.service;

import br.com.lojavelas.dto.ClienteRequest;
import br.com.lojavelas.dto.ClienteUpdateRequest;
import br.com.lojavelas.dto.EnderecoRequest;
import br.com.lojavelas.exception.BusinessException;
import br.com.lojavelas.exception.NotFoundException;
import br.com.lojavelas.model.Cliente;
import br.com.lojavelas.model.Endereco;
import br.com.lojavelas.repository.ClienteRepository;
import br.com.lojavelas.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscar(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente", id));
    }

    @Transactional
    public Cliente criar(ClienteRequest req) {
        String email = req.email().trim().toLowerCase();
        if (clienteRepository.existsByEmail(email)) {
            throw new BusinessException("E-mail já cadastrado");
        }
        if (req.cpf() != null && clienteRepository.existsByCpf(req.cpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        Cliente c = new Cliente();
        c.setNome(req.nome());
        c.setEmail(email);
        c.setSenhaHash(passwordEncoder.encode(req.senha()));
        c.setCpf(req.cpf());
        c.setTelefone(req.telefone());
        c.setDataNascimento(req.dataNascimento());
        return clienteRepository.save(c);
    }

    @Transactional
    public Cliente atualizar(Integer id, ClienteUpdateRequest req) {
        Cliente c = buscar(id);
        c.setNome(req.nome());
        c.setTelefone(req.telefone());
        c.setDataNascimento(req.dataNascimento());
        return c;
    }

    /** Exclusão lógica: o cliente fica inativo, mas o histórico de pedidos é preservado. */
    @Transactional
    public void desativar(Integer id) {
        buscar(id).setAtivo(false);
    }

    // ---------------- Endereços ----------------

    public List<Endereco> listarEnderecos(Integer idCliente) {
        buscar(idCliente);
        return enderecoRepository.findByClienteId(idCliente);
    }

    public Endereco buscarEndereco(Integer idCliente, Integer idEndereco) {
        return enderecoRepository.findByIdAndClienteId(idEndereco, idCliente)
                .orElseThrow(() -> new NotFoundException("Endereço", idEndereco));
    }

    @Transactional
    public Endereco adicionarEndereco(Integer idCliente, EnderecoRequest req) {
        Cliente cliente = buscar(idCliente);
        Endereco e = new Endereco();
        e.setCliente(cliente);
        preencher(e, req);
        // o primeiro endereço do cliente vira o principal automaticamente
        if (enderecoRepository.findByClienteId(idCliente).isEmpty()) {
            e.setPrincipal(true);
        }
        if (Boolean.TRUE.equals(e.getPrincipal())) {
            desmarcarPrincipal(idCliente);
        }
        return enderecoRepository.save(e);
    }

    @Transactional
    public Endereco atualizarEndereco(Integer idCliente, Integer idEndereco, EnderecoRequest req) {
        Endereco e = buscarEndereco(idCliente, idEndereco);
        if (Boolean.TRUE.equals(req.principal())) {
            desmarcarPrincipal(idCliente);
        }
        preencher(e, req);
        return e;
    }

    @Transactional
    public void removerEndereco(Integer idCliente, Integer idEndereco) {
        enderecoRepository.delete(buscarEndereco(idCliente, idEndereco));
        enderecoRepository.flush(); // dispara o erro de FK aqui se o endereço estiver em algum pedido
    }

    private void desmarcarPrincipal(Integer idCliente) {
        enderecoRepository.findByClienteId(idCliente).forEach(end -> end.setPrincipal(false));
    }

    private void preencher(Endereco e, EnderecoRequest req) {
        e.setApelido(req.apelido());
        e.setCep(req.cep());
        e.setLogradouro(req.logradouro());
        e.setNumero(req.numero());
        e.setComplemento(req.complemento());
        e.setBairro(req.bairro());
        e.setCidade(req.cidade());
        e.setUf(req.uf());
        if (req.principal() != null) {
            e.setPrincipal(req.principal());
        }
    }
}
