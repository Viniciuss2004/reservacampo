package br.com.reservacampo.service;

import br.com.reservacampo.dto.ReservaRequestDTO;
import br.com.reservacampo.dto.ReservaResponseDTO;
import br.com.reservacampo.entity.Quadra;
import br.com.reservacampo.entity.Reserva;
import br.com.reservacampo.entity.Usuario;
import br.com.reservacampo.enums.StatusReserva;
import br.com.reservacampo.enums.TipoQuadra;
import br.com.reservacampo.exception.HorarioInvalidoException;
import br.com.reservacampo.exception.ResourceNotFoundException;
import br.com.reservacampo.exception.ReservaConflictException;
import br.com.reservacampo.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private QuadraService quadraService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ReservaService reservaService;

    private Quadra quadra;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        quadra = Quadra.builder()
                .id(1L)
                .nome("Quadra Society")
                .tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A")
                .precoHora(new BigDecimal("100.00"))
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();
    }

    @Test
    @DisplayName("Deve criar uma reserva com sucesso e calcular valor total")
    void deveCriarReservaComSucesso() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 3, 15, 12, 0);

        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(1L)
                .usuarioId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .build();

        when(quadraService.buscarEntidadePorId(1L)).thenReturn(quadra);
        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(reservaRepository.existsConflito(eq(1L), eq(inicio), eq(fim))).thenReturn(false);

        Reserva reservaSalva = Reserva.builder()
                .id(1L)
                .quadra(quadra)
                .usuario(usuario)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .valorTotal(new BigDecimal("200.00"))
                .status(StatusReserva.CONFIRMADA)
                .build();

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSalva);

        ReservaResponseDTO response = reservaService.criar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getValorTotal()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(response.getStatus()).isEqualTo(StatusReserva.CONFIRMADA);
        assertThat(response.getQuadraNome()).isEqualTo("Quadra Society");
        assertThat(response.getUsuarioNome()).isEqualTo("João Silva");
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve bloquear reserva com conflito de horário")
    void deveBloquearReservaComConflito() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 3, 15, 12, 0);

        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(1L)
                .usuarioId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .build();

        when(quadraService.buscarEntidadePorId(1L)).thenReturn(quadra);
        when(usuarioService.buscarEntidadePorId(1L)).thenReturn(usuario);
        when(reservaRepository.existsConflito(eq(1L), eq(inicio), eq(fim))).thenReturn(true);

        assertThatThrownBy(() -> reservaService.criar(request))
                .isInstanceOf(ReservaConflictException.class)
                .hasMessageContaining("Já existe uma reserva");

        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando horário de fim é anterior ao início")
    void deveLancarExcecaoHorarioInvalido() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 15, 14, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 3, 15, 12, 0);

        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(1L)
                .usuarioId(1L)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .build();

        assertThatThrownBy(() -> reservaService.criar(request))
                .isInstanceOf(HorarioInvalidoException.class)
                .hasMessageContaining("data/hora de fim deve ser posterior");
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente para 1h30min")
    void deveCalcularValorTotalParaUmaHoraEMeia() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(1L)
                .usuarioId(1L)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 11, 30))
                .build();

        BigDecimal valorTotal = reservaService.calcularValorTotal(request, quadra);

        // 1.5 horas × R$100.00 = R$150.00
        assertThat(valorTotal).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve cancelar uma reserva com sucesso")
    void deveCancelarReservaComSucesso() {
        Reserva reserva = Reserva.builder()
                .id(1L)
                .quadra(quadra)
                .usuario(usuario)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .valorTotal(new BigDecimal("200.00"))
                .status(StatusReserva.CONFIRMADA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponseDTO response = reservaService.cancelar(1L);

        assertThat(response.getStatus()).isEqualTo(StatusReserva.CANCELADA);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar reserva já cancelada")
    void deveLancarExcecaoAoCancelarReservaJaCancelada() {
        Reserva reserva = Reserva.builder()
                .id(1L)
                .quadra(quadra)
                .usuario(usuario)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .valorTotal(new BigDecimal("200.00"))
                .status(StatusReserva.CANCELADA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(1L))
                .isInstanceOf(ReservaConflictException.class)
                .hasMessageContaining("já foi cancelada");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar reserva inexistente")
    void deveLancarExcecaoReservaInexistente() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada");
    }

    @Test
    @DisplayName("Deve listar reservas por usuário")
    void deveListarReservasPorUsuario() {
        Reserva reserva = Reserva.builder()
                .id(1L).quadra(quadra).usuario(usuario)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .valorTotal(new BigDecimal("200.00")).status(StatusReserva.CONFIRMADA).build();

        when(reservaRepository.findByUsuarioId(1L)).thenReturn(List.of(reserva));

        List<ReservaResponseDTO> resultado = reservaService.buscarPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve listar reservas por quadra")
    void deveListarReservasPorQuadra() {
        Reserva reserva = Reserva.builder()
                .id(1L).quadra(quadra).usuario(usuario)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .valorTotal(new BigDecimal("200.00")).status(StatusReserva.CONFIRMADA).build();

        when(reservaRepository.findByQuadraId(1L)).thenReturn(List.of(reserva));

        List<ReservaResponseDTO> resultado = reservaService.buscarPorQuadra(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getQuadraNome()).isEqualTo("Quadra Society");
    }
}

