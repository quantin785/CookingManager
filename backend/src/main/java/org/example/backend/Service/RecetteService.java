package org.example.backend.Service;

import org.example.backend.Entity.Recette;
import org.example.backend.Entity.Categorie;
import org.example.backend.Entity.Regime;
import org.example.backend.Repository.RecetteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecetteService {

    private final RecetteRepository recetteRepository;

    public RecetteService(RecetteRepository recetteRepository) {
        this.recetteRepository = recetteRepository;
    }

    public List<Recette> getAllRecettes() {
        return recetteRepository.findAll();
    }

    public Optional<Recette> getRecetteById(Long id) {
        return recetteRepository.findById(id);
    }

    public Recette saveRecette(Recette recette) {
        return recetteRepository.save(recette);
    }

    public Recette updateRecette(Long id, Recette recetteDetails) {
        Optional<Recette> recetteOptional = recetteRepository.findById(id);
        if (recetteOptional.isPresent()) {
            Recette recette = recetteOptional.get();
            recette.setNom(recetteDetails.getNom());
            recette.setDescription(recetteDetails.getDescription());
            recette.setTempsPreparation(recetteDetails.getTempsPreparation());
            recette.setNbPersonnes(recetteDetails.getNbPersonnes());
            recette.setPhotoPath(recetteDetails.getPhotoPath());
            recette.setCategorie(recetteDetails.getCategorie());
            recette.setRegime(recetteDetails.getRegime());
            recette.setCompositions(recetteDetails.getCompositions());
            recette.setContenu(recetteDetails.getContenu());
            return recetteRepository.save(recette);
        }
        return null;
    }

    public boolean deleteRecette(Long id) {
        if (recetteRepository.existsById(id)) {
            recetteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Recette> findByCategorie(Categorie categorie) {
        return recetteRepository.findByCategorie(categorie);
    }

    public List<Recette> findByRegime(Regime regime) {
        return recetteRepository.findByRegime(regime);
    }

    public List<Recette> searchByNom(String nom) {
        return recetteRepository.findByNomContainingIgnoreCase(nom);
    }
}
