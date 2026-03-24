package org.example.backend.Repository;

import org.example.backend.Entity.Recette;
import org.example.backend.Entity.Categorie;
import org.example.backend.Entity.Regime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetteRepository extends JpaRepository<Recette, Long> {
    List<Recette> findByCategorie(Categorie categorie);
    List<Recette> findByRegime(Regime regime);
    List<Recette> findByNomContainingIgnoreCase(String nom);
}
