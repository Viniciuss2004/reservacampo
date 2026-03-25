package br.com.reservacampo.dto;

import br.com.reservacampo.enums.TipoQuadra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuadraResponseDTO {

    private Long id;
    private String nome;
    private TipoQuadra tipo;
    private String localizacao;
    private BigDecimal precoHora;
}

