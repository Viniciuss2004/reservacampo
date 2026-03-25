package br.com.reservacampo.entity;

import br.com.reservacampo.enums.TipoQuadra;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "quadras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da quadra é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "O tipo da quadra é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoQuadra tipo;

    @NotBlank(message = "A localização é obrigatória")
    @Column(nullable = false)
    private String localizacao;

    @NotNull(message = "O preço por hora é obrigatório")
    @Positive(message = "O preço por hora deve ser positivo")
    @Column(name = "preco_hora", nullable = false)
    private BigDecimal precoHora;
}

