package org.example.backend.Controller;

import org.example.backend.Entity.Recette;
import org.example.backend.Entity.Categorie;
import org.example.backend.Entity.Regime;
import org.example.backend.Service.RecetteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recettes")
@CrossOrigin(origins = "*")
public class RecetteController {

    private final RecetteService recetteService;

    public RecetteController(RecetteService recetteService) {
        this.recetteService = recetteService;
    }

    @GetMapping
    public ResponseEntity<List<Recette>> getAllRecettes() {
        return ResponseEntity.ok(recetteService.getAllRecettes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recette> getRecetteById(@PathVariable Long id) {
        Optional<Recette> recette = recetteService.getRecetteById(id);
        return recette.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recette> createRecette(@RequestBody Recette recette) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetteService.saveRecette(recette));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recette> updateRecette(@PathVariable Long id, @RequestBody Recette recetteDetails) {
        Recette updated = recetteService.updateRecette(id, recetteDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecette(@PathVariable Long id) {
        return recetteService.deleteRecette(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<Recette>> getRecettesByCategorie(@PathVariable Long categorieId) {
        Categorie categorie = new Categorie();
        categorie.setId(categorieId);
        return ResponseEntity.ok(recetteService.findByCategorie(categorie));
    }

    @GetMapping("/regime/{regimeId}")
    public ResponseEntity<List<Recette>> getRecettesByRegime(@PathVariable Long regimeId) {
        Regime regime = new Regime();
        regime.setId(regimeId);
        return ResponseEntity.ok(recetteService.findByRegime(regime));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recette>> searchRecettes(@RequestParam String nom) {
        return ResponseEntity.ok(recetteService.searchByNom(nom));
    }
}
