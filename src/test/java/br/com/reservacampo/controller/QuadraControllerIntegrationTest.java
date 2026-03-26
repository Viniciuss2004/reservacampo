package br.com.reservacampo.controller;

import br.com.reservacampo.dto.QuadraRequestDTO;
import br.com.reservacampo.dto.QuadraResponseDTO;
import br.com.reservacampo.enums.TipoQuadra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuadraControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve criar e buscar uma quadra via API")
    void deveCriarEBuscarQuadra() {
        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("Quadra Society")
                .tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A")
                .precoHora(new BigDecimal("150.00"))
                .build();

        ResponseEntity<QuadraResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/quadras", request, QuadraResponseDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();

        Long id = createResponse.getBody().getId();

        ResponseEntity<QuadraResponseDTO> getResponse = restTemplate.getForEntity(
                "/api/quadras/" + id, QuadraResponseDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getNome()).isEqualTo("Quadra Society");
    }

    @Test
    @DisplayName("Deve listar todas as quadras")
    void deveListarTodasQuadras() {
        QuadraRequestDTO q1 = QuadraRequestDTO.builder()
                .nome("Quadra 1").tipo(TipoQuadra.FUTEBOL).localizacao("A").precoHora(new BigDecimal("100")).build();
        QuadraRequestDTO q2 = QuadraRequestDTO.builder()
                .nome("Quadra 2").tipo(TipoQuadra.TENIS).localizacao("B").precoHora(new BigDecimal("80")).build();

        restTemplate.postForEntity("/api/quadras", q1, QuadraResponseDTO.class);
        restTemplate.postForEntity("/api/quadras", q2, QuadraResponseDTO.class);

        ResponseEntity<QuadraResponseDTO[]> response = restTemplate.getForEntity(
                "/api/quadras", QuadraResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("Deve atualizar uma quadra")
    void deveAtualizarQuadra() {
        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("Quadra Antiga").tipo(TipoQuadra.VOLEI).localizacao("C").precoHora(new BigDecimal("90")).build();

        ResponseEntity<QuadraResponseDTO> created = restTemplate.postForEntity(
                "/api/quadras", request, QuadraResponseDTO.class);
        Long id = created.getBody().getId();

        QuadraRequestDTO update = QuadraRequestDTO.builder()
                .nome("Quadra Nova").tipo(TipoQuadra.BASQUETE).localizacao("D").precoHora(new BigDecimal("120")).build();

        ResponseEntity<QuadraResponseDTO> updateResponse = restTemplate.exchange(
                "/api/quadras/" + id, HttpMethod.PUT, new HttpEntity<>(update), QuadraResponseDTO.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getNome()).isEqualTo("Quadra Nova");
    }

    @Test
    @DisplayName("Deve deletar uma quadra")
    void deveDeletarQuadra() {
        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("Quadra Delete").tipo(TipoQuadra.FUTSAL).localizacao("E").precoHora(new BigDecimal("70")).build();

        ResponseEntity<QuadraResponseDTO> created = restTemplate.postForEntity(
                "/api/quadras", request, QuadraResponseDTO.class);
        Long id = created.getBody().getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/quadras/" + id, HttpMethod.DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/api/quadras/" + id, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() {
        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("")
                .tipo(null)
                .localizacao("")
                .precoHora(null)
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/quadras", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

