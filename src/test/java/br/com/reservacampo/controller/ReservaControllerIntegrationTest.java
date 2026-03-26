package br.com.reservacampo.controller;

import br.com.reservacampo.dto.*;
import br.com.reservacampo.enums.StatusReserva;
import br.com.reservacampo.enums.TipoQuadra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservaControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long quadraId;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        QuadraRequestDTO quadraRequest = QuadraRequestDTO.builder()
                .nome("Quadra Society")
                .tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A")
                .precoHora(new BigDecimal("100.00"))
                .build();
        ResponseEntity<QuadraResponseDTO> quadraResponse = restTemplate.postForEntity(
                "/api/quadras", quadraRequest, QuadraResponseDTO.class);
        quadraId = quadraResponse.getBody().getId();

        UsuarioRequestDTO usuarioRequest = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();
        ResponseEntity<UsuarioResponseDTO> usuarioResponse = restTemplate.postForEntity(
                "/api/usuarios", usuarioRequest, UsuarioResponseDTO.class);
        usuarioId = usuarioResponse.getBody().getId();
    }

    @Test
    @DisplayName("Deve criar uma reserva com sucesso e calcular valor total")
    void deveCriarReservaComSucesso() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(quadraId)
                .usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();

        ResponseEntity<ReservaResponseDTO> response = restTemplate.postForEntity(
                "/api/reservas", request, ReservaResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(StatusReserva.CONFIRMADA);
        assertThat(response.getBody().getValorTotal()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(response.getBody().getQuadraNome()).isEqualTo("Quadra Society");
        assertThat(response.getBody().getUsuarioNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve bloquear reserva com conflito de horário (sobreposição total)")
    void deveBloquearReservaComConflitoTotal() {
        ReservaRequestDTO reserva1 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        restTemplate.postForEntity("/api/reservas", reserva1, ReservaResponseDTO.class);

        ReservaRequestDTO reserva2 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/reservas", reserva2, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Deve bloquear reserva com conflito de horário (sobreposição parcial)")
    void deveBloquearReservaComConflitoParcial() {
        ReservaRequestDTO reserva1 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        restTemplate.postForEntity("/api/reservas", reserva1, ReservaResponseDTO.class);

        ReservaRequestDTO reserva2 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 11, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 13, 0))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/reservas", reserva2, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Deve permitir reserva em horário adjacente (sem sobreposição)")
    void devePermitirReservaHorarioAdjacente() {
        ReservaRequestDTO reserva1 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        restTemplate.postForEntity("/api/reservas", reserva1, ReservaResponseDTO.class);

        ReservaRequestDTO reserva2 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 12, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 14, 0))
                .build();

        ResponseEntity<ReservaResponseDTO> response = restTemplate.postForEntity(
                "/api/reservas", reserva2, ReservaResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Deve permitir reserva após cancelar a original")
    void devePermitirReservaAposCancelar() {
        ReservaRequestDTO reserva1 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        ResponseEntity<ReservaResponseDTO> created = restTemplate.postForEntity(
                "/api/reservas", reserva1, ReservaResponseDTO.class);
        Long reservaId = created.getBody().getId();

        restTemplate.patchForObject("/api/reservas/" + reservaId + "/cancelar", null, ReservaResponseDTO.class);

        ReservaRequestDTO reserva2 = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();

        ResponseEntity<ReservaResponseDTO> response = restTemplate.postForEntity(
                "/api/reservas", reserva2, ReservaResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Deve cancelar uma reserva")
    void deveCancelarReserva() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();

        ResponseEntity<ReservaResponseDTO> created = restTemplate.postForEntity(
                "/api/reservas", request, ReservaResponseDTO.class);
        Long reservaId = created.getBody().getId();

        ResponseEntity<ReservaResponseDTO> cancelResponse = restTemplate.exchange(
                "/api/reservas/" + reservaId + "/cancelar", HttpMethod.PATCH,
                null, ReservaResponseDTO.class);

        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cancelResponse.getBody().getStatus()).isEqualTo(StatusReserva.CANCELADA);
    }

    @Test
    @DisplayName("Deve retornar 400 para horário inválido (fim antes do início)")
    void deveRetornar400HorarioInvalido() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 14, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/reservas", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Deve buscar reservas por usuário")
    void deveBuscarReservasPorUsuario() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        restTemplate.postForEntity("/api/reservas", request, ReservaResponseDTO.class);

        ResponseEntity<ReservaResponseDTO[]> response = restTemplate.getForEntity(
                "/api/reservas/usuario/" + usuarioId, ReservaResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar reservas por quadra")
    void deveBuscarReservasPorQuadra() {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .quadraId(quadraId).usuarioId(usuarioId)
                .dataHoraInicio(LocalDateTime.of(2026, 3, 15, 10, 0))
                .dataHoraFim(LocalDateTime.of(2026, 3, 15, 12, 0))
                .build();
        restTemplate.postForEntity("/api/reservas", request, ReservaResponseDTO.class);

        ResponseEntity<ReservaResponseDTO[]> response = restTemplate.getForEntity(
                "/api/reservas/quadra/" + quadraId, ReservaResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}

