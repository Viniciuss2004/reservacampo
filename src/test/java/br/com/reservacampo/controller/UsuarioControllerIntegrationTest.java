package br.com.reservacampo.controller;

import br.com.reservacampo.dto.UsuarioRequestDTO;
import br.com.reservacampo.dto.UsuarioResponseDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UsuarioControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve criar e buscar um usuário via API")
    void deveCriarEBuscarUsuario() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();

        ResponseEntity<UsuarioResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/usuarios", request, UsuarioResponseDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getId()).isNotNull();

        Long id = createResponse.getBody().getId();

        ResponseEntity<UsuarioResponseDTO> getResponse = restTemplate.getForEntity(
                "/api/usuarios/" + id, UsuarioResponseDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar 409 ao cadastrar e-mail duplicado")
    void deveRetornar409EmailDuplicado() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .email("duplicado@email.com")
                .telefone("11999999999")
                .build();

        restTemplate.postForEntity("/api/usuarios", request, UsuarioResponseDTO.class);

        UsuarioRequestDTO duplicado = UsuarioRequestDTO.builder()
                .nome("Maria Santos")
                .email("duplicado@email.com")
                .telefone("11888888888")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/usuarios", duplicado, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Deve atualizar um usuário")
    void deveAtualizarUsuario() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João").email("joao.update@email.com").telefone("111").build();

        ResponseEntity<UsuarioResponseDTO> created = restTemplate.postForEntity(
                "/api/usuarios", request, UsuarioResponseDTO.class);
        Long id = created.getBody().getId();

        UsuarioRequestDTO update = UsuarioRequestDTO.builder()
                .nome("João Atualizado").email("joao.update@email.com").telefone("222").build();

        ResponseEntity<UsuarioResponseDTO> updateResponse = restTemplate.exchange(
                "/api/usuarios/" + id, HttpMethod.PUT, new HttpEntity<>(update), UsuarioResponseDTO.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getNome()).isEqualTo("João Atualizado");
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void deveDeletarUsuario() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("Delete Me").email("delete@email.com").telefone("333").build();

        ResponseEntity<UsuarioResponseDTO> created = restTemplate.postForEntity(
                "/api/usuarios", request, UsuarioResponseDTO.class);
        Long id = created.getBody().getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/usuarios/" + id, HttpMethod.DELETE, null, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("")
                .email("email-invalido")
                .telefone("")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/usuarios", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

