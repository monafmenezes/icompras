package com.monalisamenezes.icompras.pedidos.controller;


import com.monalisamenezes.icompras.pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import com.monalisamenezes.icompras.pedidos.controller.dto.NovoPedidoDTO;
import com.monalisamenezes.icompras.pedidos.controller.dto.mappers.PedidoMapper;
import com.monalisamenezes.icompras.pedidos.model.ErroResposta;
import com.monalisamenezes.icompras.pedidos.model.Pedido;
import com.monalisamenezes.icompras.pedidos.model.exception.ItemNaoEncontradoException;
import com.monalisamenezes.icompras.pedidos.model.exception.ValidationException;
import com.monalisamenezes.icompras.pedidos.publisher.DetalhePedidoMapper;
import com.monalisamenezes.icompras.pedidos.publisher.representation.DetalhePedidoRepresentation;
import com.monalisamenezes.icompras.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService service;
    private final PedidoMapper mapper;
    private final DetalhePedidoMapper detalhePedidoMapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO novoPedidoDTO) {
        try {
            Pedido pedido = mapper.map(novoPedidoDTO);
            Pedido novoPedido = service.criarPedido(pedido);
            return ResponseEntity.ok(novoPedido.getCodigo());
        } catch (ValidationException e) {
            ErroResposta Error = new ErroResposta("Erro validação.", e.getField(), e.getMessage());
            return ResponseEntity.badRequest().body(Error);
        }
    }

    @PostMapping("pagamentos")
    public ResponseEntity<Object> adicionarNovoPagamento(@RequestBody AdicaoNovoPagamentoDTO dto) {
       try {
           service.adicionarNovoPagamento(dto.codigoPedido(), dto.dadosCartao(), dto.tipoPagamento());
           return ResponseEntity.noContent().build();
       } catch (ItemNaoEncontradoException e) {
            ErroResposta erro = new ErroResposta(
              "Item não encontrado.",
              "codigoPedido",
                    e.getMessage()
            );
            return ResponseEntity.badRequest().body(erro);
       }
    }

    @GetMapping("{codigo}")
    public ResponseEntity<DetalhePedidoRepresentation> obterDetalhesPedido(@PathVariable Long codigo) {
        return service.carregarDadosCompletosPedido(codigo)
                .map(detalhePedidoMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
