package org.example.backend.Repository;

import org.example.backend.Entity.Regime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegimeRepository extends JpaRepository<Regime, Long> {
    Optional<Regime> findByNom(String nom);
    boolean existsByNom(String nom);
}
