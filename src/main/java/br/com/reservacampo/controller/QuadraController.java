package br.com.reservacampo.controller;

import br.com.reservacampo.dto.QuadraRequestDTO;
import br.com.reservacampo.dto.QuadraResponseDTO;
import br.com.reservacampo.service.QuadraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quadras")
@RequiredArgsConstructor
public class QuadraController {

    private final QuadraService quadraService;

    @PostMapping
    public ResponseEntity<QuadraResponseDTO> criar(@Valid @RequestBody QuadraRequestDTO dto) {
        QuadraResponseDTO resposta = quadraService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<QuadraResponseDTO>> listarTodas() {
        return ResponseEntity.ok(quadraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuadraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(quadraService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuadraResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody QuadraRequestDTO dto) {
        return ResponseEntity.ok(quadraService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        quadraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

