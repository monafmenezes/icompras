package com.monalisamenezes.icompras.clientes.repository;

import com.monalisamenezes.icompras.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
