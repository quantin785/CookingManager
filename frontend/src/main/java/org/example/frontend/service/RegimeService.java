package org.example.frontend.service;

import org.example.frontend.config.ApiConfig;
import org.example.frontend.model.Regime;

import java.util.List;

/** Service CRUD pour les régimes alimentaires. */
public class RegimeService extends ApiService {

    private static final String URL = ApiConfig.BASE_URL + "/regimes";

    public List<Regime> getAll() {
        return getList(URL, Regime.class);
    }

    public Regime getById(Long id) {
        return get(URL + "/" + id, Regime.class);
    }

    public Regime create(Regime regime) {
        return post(URL, regime, Regime.class);
    }

    public Regime update(Long id, Regime regime) {
        return put(URL + "/" + id, regime, Regime.class);
    }

    public boolean delete(Long id) {
        return delete(URL + "/" + id);
    }
}
