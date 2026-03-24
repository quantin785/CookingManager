package org.example.backend.Service;

import org.example.backend.Entity.Categorie;
import org.example.backend.Repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    public Optional<Categorie> getCategorieByNom(String nom) {
        return categorieRepository.findByNom(nom);
    }

    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public Categorie updateCategorie(Long id, Categorie categorieDetails) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);
        if (categorieOptional.isPresent()) {
            Categorie categorie = categorieOptional.get();
            categorie.setNom(categorieDetails.getNom());
            return categorieRepository.save(categorie);
        }
        return null;
    }

    public boolean deleteCategorie(Long id) {
        if (categorieRepository.existsById(id)) {
            categorieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByNom(String nom) {
        return categorieRepository.existsByNom(nom);
    }
}
