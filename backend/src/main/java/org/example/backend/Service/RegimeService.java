package org.example.backend.Service;

import org.example.backend.Entity.Regime;
import org.example.backend.Repository.RegimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegimeService {

    private final RegimeRepository regimeRepository;

    public RegimeService(RegimeRepository regimeRepository) {
        this.regimeRepository = regimeRepository;
    }

    public List<Regime> getAllRegimes() {
        return regimeRepository.findAll();
    }

    public Optional<Regime> getRegimeById(Long id) {
        return regimeRepository.findById(id);
    }

    public Optional<Regime> getRegimeByNom(String nom) {
        return regimeRepository.findByNom(nom);
    }

    public Regime saveRegime(Regime regime) {
        return regimeRepository.save(regime);
    }

    public Regime updateRegime(Long id, Regime regimeDetails) {
        Optional<Regime> regimeOptional = regimeRepository.findById(id);
        if (regimeOptional.isPresent()) {
            Regime regime = regimeOptional.get();
            regime.setNom(regimeDetails.getNom());
            return regimeRepository.save(regime);
        }
        return null;
    }

    public boolean deleteRegime(Long id) {
        if (regimeRepository.existsById(id)) {
            regimeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByNom(String nom) {
        return regimeRepository.existsByNom(nom);
    }
}
