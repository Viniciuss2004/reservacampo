package br.com.reservacampo.dto;

import br.com.reservacampo.enums.TipoQuadra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuadraRequestDTO {

    @NotBlank(message = "O nome da quadra é obrigatório")
    private String nome;

    @NotNull(message = "O tipo da quadra é obrigatório")
    private TipoQuadra tipo;

    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;

    @NotNull(message = "O preço por hora é obrigatório")
    @Positive(message = "O preço por hora deve ser positivo")
    private BigDecimal precoHora;
}

