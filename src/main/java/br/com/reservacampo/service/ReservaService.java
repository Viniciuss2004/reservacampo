package br.com.reservacampo.service;

import br.com.reservacampo.dto.ReservaRequestDTO;
import br.com.reservacampo.dto.ReservaResponseDTO;
import br.com.reservacampo.entity.Quadra;
import br.com.reservacampo.entity.Reserva;
import br.com.reservacampo.entity.Usuario;
import br.com.reservacampo.enums.StatusReserva;
import br.com.reservacampo.exception.HorarioInvalidoException;
import br.com.reservacampo.exception.ResourceNotFoundException;
import br.com.reservacampo.exception.ReservaConflictException;
import br.com.reservacampo.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final QuadraService quadraService;
    private final UsuarioService usuarioService;

    @Transactional
    public ReservaResponseDTO criar(ReservaRequestDTO dto) {
        validarHorarios(dto);

        Quadra quadra = quadraService.buscarEntidadePorId(dto.getQuadraId());
        Usuario usuario = usuarioService.buscarEntidadePorId(dto.getUsuarioId());

        boolean conflito = reservaRepository.existsConflito(
                dto.getQuadraId(), dto.getDataHoraInicio(), dto.getDataHoraFim());
        if (conflito) {
            throw new ReservaConflictException(
                    "Já existe uma reserva para esta quadra no horário solicitado");
        }

        BigDecimal valorTotal = calcularValorTotal(dto, quadra);

        Reserva reserva = Reserva.builder()
                .quadra(quadra)
                .usuario(usuario)
                .dataHoraInicio(dto.getDataHoraInicio())
                .dataHoraFim(dto.getDataHoraFim())
                .valorTotal(valorTotal)
                .status(StatusReserva.CONFIRMADA)
                .build();

        Reserva salva = reservaRepository.save(reserva);
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarPorId(Long id) {
        Reserva reserva = buscarEntidadePorId(id);
        return toResponseDTO(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarPorQuadra(Long quadraId) {
        return reservaRepository.findByQuadraId(quadraId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = buscarEntidadePorId(id);
        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new ReservaConflictException("Esta reserva já foi cancelada");
        }
        reserva.setStatus(StatusReserva.CANCELADA);
        Reserva atualizada = reservaRepository.save(reserva);
        return toResponseDTO(atualizada);
    }

    private void validarHorarios(ReservaRequestDTO dto) {
        if (!dto.getDataHoraFim().isAfter(dto.getDataHoraInicio())) {
            throw new HorarioInvalidoException(
                    "A data/hora de fim deve ser posterior à data/hora de início");
        }
    }

    BigDecimal calcularValorTotal(ReservaRequestDTO dto, Quadra quadra) {
        long minutos = Duration.between(dto.getDataHoraInicio(), dto.getDataHoraFim()).toMinutes();
        BigDecimal horas = BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        return horas.multiply(quadra.getPrecoHora()).setScale(2, RoundingMode.HALF_UP);
    }

    private Reserva buscarEntidadePorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + id));
    }

    private ReservaResponseDTO toResponseDTO(Reserva reserva) {
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .quadraId(reserva.getQuadra().getId())
                .quadraNome(reserva.getQuadra().getNome())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioNome(reserva.getUsuario().getNome())
                .dataHoraInicio(reserva.getDataHoraInicio())
                .dataHoraFim(reserva.getDataHoraFim())
                .valorTotal(reserva.getValorTotal())
                .status(reserva.getStatus())
                .build();
    }
}

