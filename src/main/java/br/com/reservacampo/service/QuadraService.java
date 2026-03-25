package br.com.reservacampo.service;

import br.com.reservacampo.dto.QuadraRequestDTO;
import br.com.reservacampo.dto.QuadraResponseDTO;
import br.com.reservacampo.entity.Quadra;
import br.com.reservacampo.exception.ResourceNotFoundException;
import br.com.reservacampo.repository.QuadraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuadraService {

    private final QuadraRepository quadraRepository;

    @Transactional
    public QuadraResponseDTO criar(QuadraRequestDTO dto) {
        Quadra quadra = Quadra.builder()
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .localizacao(dto.getLocalizacao())
                .precoHora(dto.getPrecoHora())
                .build();
        Quadra salva = quadraRepository.save(quadra);
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<QuadraResponseDTO> listarTodas() {
        return quadraRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuadraResponseDTO buscarPorId(Long id) {
        Quadra quadra = buscarEntidadePorId(id);
        return toResponseDTO(quadra);
    }

    @Transactional
    public QuadraResponseDTO atualizar(Long id, QuadraRequestDTO dto) {
        Quadra quadra = buscarEntidadePorId(id);
        quadra.setNome(dto.getNome());
        quadra.setTipo(dto.getTipo());
        quadra.setLocalizacao(dto.getLocalizacao());
        quadra.setPrecoHora(dto.getPrecoHora());
        Quadra atualizada = quadraRepository.save(quadra);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Quadra quadra = buscarEntidadePorId(id);
        quadraRepository.delete(quadra);
    }

    public Quadra buscarEntidadePorId(Long id) {
        return quadraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quadra não encontrada com id: " + id));
    }

    private QuadraResponseDTO toResponseDTO(Quadra quadra) {
        return QuadraResponseDTO.builder()
                .id(quadra.getId())
                .nome(quadra.getNome())
                .tipo(quadra.getTipo())
                .localizacao(quadra.getLocalizacao())
                .precoHora(quadra.getPrecoHora())
                .build();
    }
}

