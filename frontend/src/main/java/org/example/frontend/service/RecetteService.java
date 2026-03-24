package org.example.frontend.service;

import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.Recette;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RecetteService extends ApiService {

    private static final String URL = ApiConfig.BASE_URL + "/recettes";

    public List<Recette> getAll() {
        return getList(URL, Recette.class);
    }

    public Recette getById(Long id) {
        return get(URL + "/" + id, Recette.class);
    }

    public Recette create(Recette recette) {
        return post(URL, recette, Recette.class);
    }

    public Recette update(Long id, Recette recette) {
        return put(URL + "/" + id, recette, Recette.class);
    }

    public boolean delete(Long id) {
        return delete(URL + "/" + id);
    }

    public List<Recette> searchByNom(String nom) {
        String encoded = URLEncoder.encode(nom, StandardCharsets.UTF_8);
        return getList(URL + "/search?nom=" + encoded, Recette.class);
    }

    public List<Recette> getByCategorie(Long categorieId) {
        return getList(URL + "/categorie/" + categorieId, Recette.class);
    }

    public List<Recette> getByRegime(Long regimeId) {
        return getList(URL + "/regime/" + regimeId, Recette.class);
    }
}
