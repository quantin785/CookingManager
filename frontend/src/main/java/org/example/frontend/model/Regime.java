package org.example.frontend.model;

/** Modèle miroir de l'entité backend Regime. */
public class Regime {

    private Long id;
    private String nom;

    public Regime() {}

    public Regime(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    /** Utilisé par ComboBox pour afficher le régime. */
    @Override
    public String toString() {
        return nom != null ? nom : "";
    }
}
