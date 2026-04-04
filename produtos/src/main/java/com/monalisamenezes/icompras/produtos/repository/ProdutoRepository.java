package com.monalisamenezes.icompras.produtos.repository;

import com.monalisamenezes.icompras.produtos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
