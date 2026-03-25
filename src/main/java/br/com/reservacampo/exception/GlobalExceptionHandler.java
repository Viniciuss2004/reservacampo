package br.com.reservacampo.exception;

import br.com.reservacampo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex,
                                                                    HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Recurso não encontrado")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReservaConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleReservaConflict(ReservaConflictException ex,
                                                                   HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erro("Conflito de horário")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailJaCadastrado(EmailJaCadastradoException ex,
                                                                     HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erro("E-mail já cadastrado")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HorarioInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleHorarioInvalido(HorarioInvalidoException ex,
                                                                   HttpServletRequest request) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Horário inválido")
                .mensagem(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                campos.put(fieldError.getField(), fieldError.getDefaultMessage()));

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Erro de validação")
                .mensagem("Um ou mais campos possuem valores inválidos")
                .path(request.getRequestURI())
                .campos(campos)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

