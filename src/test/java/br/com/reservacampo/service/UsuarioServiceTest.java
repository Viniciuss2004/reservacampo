package br.com.reservacampo.service;

import br.com.reservacampo.dto.UsuarioRequestDTO;
import br.com.reservacampo.dto.UsuarioResponseDTO;
import br.com.reservacampo.entity.Usuario;
import br.com.reservacampo.exception.EmailJaCadastradoException;
import br.com.reservacampo.exception.ResourceNotFoundException;
import br.com.reservacampo.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();

        Usuario usuarioSalvo = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDTO response = usuarioService.criar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(request))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("E-mail já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        Usuario u1 = Usuario.builder().id(1L).nome("João").email("joao@email.com").telefone("111").build();
        Usuario u2 = Usuario.builder().id(2L).nome("Maria").email("maria@email.com").telefone("222").build();

        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertThat(resultado).hasSize(2);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").email("joao@email.com").telefone("111").build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.buscarPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoUsuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        Usuario existente = Usuario.builder().id(1L).nome("João").email("joao@email.com").telefone("111").build();

        UsuarioRequestDTO request = UsuarioRequestDTO.builder()
                .nome("João Atualizado")
                .email("joao@email.com")
                .telefone("222")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO response = usuarioService.atualizar(1L, request);

        assertThat(response.getNome()).isEqualTo("João Atualizado");
        assertThat(response.getTelefone()).isEqualTo("222");
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").email("joao@email.com").telefone("111").build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deletar(1L);

        verify(usuarioRepository).delete(usuario);
    }
}

