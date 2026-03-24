package org.example.backend.Controller;

import org.example.backend.Entity.Regime;
import org.example.backend.Service.RegimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/regimes")
@CrossOrigin(origins = "*")
public class RegimeController {

    private final RegimeService regimeService;

    public RegimeController(RegimeService regimeService) {
        this.regimeService = regimeService;
    }

    @GetMapping
    public ResponseEntity<List<Regime>> getAllRegimes() {
        List<Regime> regimes = regimeService.getAllRegimes();
        return ResponseEntity.ok(regimes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regime> getRegimeById(@PathVariable Long id) {
        Optional<Regime> regime = regimeService.getRegimeById(id);
        return regime.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<Regime> getRegimeByNom(@PathVariable String nom) {
        Optional<Regime> regime = regimeService.getRegimeByNom(nom);
        return regime.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Regime> createRegime(@RequestBody Regime regime) {
        if (regimeService.existsByNom(regime.getNom())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Regime savedRegime = regimeService.saveRegime(regime);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRegime);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Regime> updateRegime(@PathVariable Long id, @RequestBody Regime regimeDetails) {
        Regime updatedRegime = regimeService.updateRegime(id, regimeDetails);
        if (updatedRegime != null) {
            return ResponseEntity.ok(updatedRegime);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegime(@PathVariable Long id) {
        boolean deleted = regimeService.deleteRegime(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
