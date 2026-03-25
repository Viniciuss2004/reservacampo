package br.com.reservacampo.repository;

import br.com.reservacampo.entity.Quadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuadraRepository extends JpaRepository<Quadra, Long> {
}

