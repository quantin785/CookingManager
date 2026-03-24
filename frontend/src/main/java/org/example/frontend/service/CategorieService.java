package org.example.frontend.service;

import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.Categorie;

import java.util.List;

/** Service CRUD pour les catégories. */
public class CategorieService extends ApiService {

    private static final String URL = ApiConfig.BASE_URL + "/categories";

    public List<Categorie> getAll() {
        return getList(URL, Categorie.class);
    }

    public Categorie getById(Long id) {
        return get(URL + "/" + id, Categorie.class);
    }

    public Categorie create(Categorie categorie) {
        return post(URL, categorie, Categorie.class);
    }

    public Categorie update(Long id, Categorie categorie) {
        return put(URL + "/" + id, categorie, Categorie.class);
    }

    public boolean delete(Long id) {
        return delete(URL + "/" + id);
    }
}
