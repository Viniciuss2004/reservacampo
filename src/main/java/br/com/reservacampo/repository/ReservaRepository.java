package br.com.reservacampo.repository;

import br.com.reservacampo.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Verifica se existe conflito de horário para uma quadra específica.
     * Duas reservas conflitam quando: inicio1 < fim2 AND fim1 > inicio2
     * Reservas canceladas são ignoradas.
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r " +
           "WHERE r.quadra.id = :quadraId " +
           "AND r.status <> br.com.reservacampo.enums.StatusReserva.CANCELADA " +
           "AND r.dataHoraInicio < :fim " +
           "AND r.dataHoraFim > :inicio")
    boolean existsConflito(@Param("quadraId") Long quadraId,
                           @Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim);

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByQuadraId(Long quadraId);
}

