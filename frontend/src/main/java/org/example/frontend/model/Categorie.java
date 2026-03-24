package org.example.frontend.model;

/** Modèle miroir de l'entité backend Categorie. */
public class Categorie {

    private Long id;
    private String nom;

    public Categorie() {}

    public Categorie(Long id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    /** Utilisé par ComboBox pour afficher la catégorie. */
    @Override
    public String toString() {
        return nom != null ? nom : "";
    }
}
