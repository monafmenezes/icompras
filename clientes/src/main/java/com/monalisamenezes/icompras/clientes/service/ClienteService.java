package com.monalisamenezes.icompras.clientes.service;

import com.monalisamenezes.icompras.clientes.dto.ClienteDTO;
import com.monalisamenezes.icompras.clientes.repository.ClienteRepository;
import com.monalisamenezes.icompras.clientes.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteDTO store(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setBairro(dto.bairro());
        cliente.setCpf(dto.cpf());
        cliente.setEmail(dto.email());
        cliente.setLogradouro(dto.logradouro());
        cliente.setNumero(dto.numero());
        cliente.setTelefone(dto.telefone());

        Cliente newCliente = repository.save(cliente);

        return new ClienteDTO(
                newCliente.getCodigo(),
                newCliente.getNome(),
                newCliente.getCpf(),
                newCliente.getLogradouro(),
                newCliente.getNumero(),
                newCliente.getBairro(),
                newCliente.getEmail(),
                newCliente.getTelefone()
        );
    }

    public Optional<ClienteDTO> findById(Long codigo) {
        return repository.findById(codigo)
                .map(c -> new ClienteDTO(
                        c.getCodigo(),
                        c.getNome(),
                        c.getCpf(),
                        c.getLogradouro(),
                        c.getNumero(),
                        c.getBairro(),
                        c.getEmail(),
                        c.getTelefone()
                ));
    }

    public List<ClienteDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(c -> new ClienteDTO(
                        c.getCodigo(),
                        c.getNome(),
                        c.getCpf(),
                        c.getLogradouro(),
                        c.getNumero(),
                        c.getBairro(),
                        c.getEmail(),
                        c.getTelefone()
                ))
                .toList();
    }

    public void delete(Long codigo) {
        Cliente cliente = repository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado com o código: " + codigo
                ));

        cliente.setAtivo(false);

        repository.save(cliente);
    }
}
