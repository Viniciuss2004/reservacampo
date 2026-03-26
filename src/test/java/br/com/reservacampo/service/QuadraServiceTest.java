package br.com.reservacampo.service;

import br.com.reservacampo.dto.QuadraRequestDTO;
import br.com.reservacampo.dto.QuadraResponseDTO;
import br.com.reservacampo.entity.Quadra;
import br.com.reservacampo.enums.TipoQuadra;
import br.com.reservacampo.exception.ResourceNotFoundException;
import br.com.reservacampo.repository.QuadraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuadraServiceTest {

    @Mock
    private QuadraRepository quadraRepository;

    @InjectMocks
    private QuadraService quadraService;

    @Test
    @DisplayName("Deve criar uma quadra com sucesso")
    void deveCriarQuadraComSucesso() {
        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("Quadra Society")
                .tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A")
                .precoHora(new BigDecimal("150.00"))
                .build();

        Quadra quadraSalva = Quadra.builder()
                .id(1L)
                .nome("Quadra Society")
                .tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A")
                .precoHora(new BigDecimal("150.00"))
                .build();

        when(quadraRepository.save(any(Quadra.class))).thenReturn(quadraSalva);

        QuadraResponseDTO response = quadraService.criar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Quadra Society");
        assertThat(response.getTipo()).isEqualTo(TipoQuadra.FUTEBOL);
        assertThat(response.getPrecoHora()).isEqualByComparingTo(new BigDecimal("150.00"));
        verify(quadraRepository).save(any(Quadra.class));
    }

    @Test
    @DisplayName("Deve listar todas as quadras")
    void deveListarTodasQuadras() {
        Quadra quadra1 = Quadra.builder().id(1L).nome("Quadra 1").tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A").precoHora(new BigDecimal("100.00")).build();
        Quadra quadra2 = Quadra.builder().id(2L).nome("Quadra 2").tipo(TipoQuadra.TENIS)
                .localizacao("Bloco B").precoHora(new BigDecimal("80.00")).build();

        when(quadraRepository.findAll()).thenReturn(List.of(quadra1, quadra2));

        List<QuadraResponseDTO> resultado = quadraService.listarTodas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Quadra 1");
        assertThat(resultado.get(1).getNome()).isEqualTo("Quadra 2");
    }

    @Test
    @DisplayName("Deve buscar quadra por ID com sucesso")
    void deveBuscarQuadraPorIdComSucesso() {
        Quadra quadra = Quadra.builder().id(1L).nome("Quadra 1").tipo(TipoQuadra.VOLEI)
                .localizacao("Bloco C").precoHora(new BigDecimal("90.00")).build();

        when(quadraRepository.findById(1L)).thenReturn(Optional.of(quadra));

        QuadraResponseDTO response = quadraService.buscarPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Quadra 1");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar quadra inexistente")
    void deveLancarExcecaoQuadraInexistente() {
        when(quadraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quadraService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quadra não encontrada");
    }

    @Test
    @DisplayName("Deve atualizar uma quadra com sucesso")
    void deveAtualizarQuadraComSucesso() {
        Quadra quadraExistente = Quadra.builder().id(1L).nome("Quadra Antiga").tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A").precoHora(new BigDecimal("100.00")).build();

        QuadraRequestDTO request = QuadraRequestDTO.builder()
                .nome("Quadra Nova")
                .tipo(TipoQuadra.BASQUETE)
                .localizacao("Bloco B")
                .precoHora(new BigDecimal("120.00"))
                .build();

        when(quadraRepository.findById(1L)).thenReturn(Optional.of(quadraExistente));
        when(quadraRepository.save(any(Quadra.class))).thenAnswer(inv -> inv.getArgument(0));

        QuadraResponseDTO response = quadraService.atualizar(1L, request);

        assertThat(response.getNome()).isEqualTo("Quadra Nova");
        assertThat(response.getTipo()).isEqualTo(TipoQuadra.BASQUETE);
    }

    @Test
    @DisplayName("Deve deletar uma quadra com sucesso")
    void deveDeletarQuadraComSucesso() {
        Quadra quadra = Quadra.builder().id(1L).nome("Quadra").tipo(TipoQuadra.FUTEBOL)
                .localizacao("Bloco A").precoHora(new BigDecimal("100.00")).build();

        when(quadraRepository.findById(1L)).thenReturn(Optional.of(quadra));

        quadraService.deletar(1L);

        verify(quadraRepository).delete(quadra);
    }
}

