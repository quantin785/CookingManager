package org.example.frontend.model;

import java.util.List;

public class Recette {

    private Long id;
    private String nom;
    private String description;
    private Integer tempsPreparation;
    private Integer nbPersonnes;
    private String photoPath;
    private Categorie categorie;
    private Regime regime;
    private List<String> compositions;
    private String contenu;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTempsPreparation() { return tempsPreparation; }
    public void setTempsPreparation(Integer tempsPreparation) { this.tempsPreparation = tempsPreparation; }

    public Integer getNbPersonnes() { return nbPersonnes; }
    public void setNbPersonnes(Integer nbPersonnes) { this.nbPersonnes = nbPersonnes; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public Regime getRegime() { return regime; }
    public void setRegime(Regime regime) { this.regime = regime; }

    public List<String> getCompositions() { return compositions; }
    public void setCompositions(List<String> compositions) { this.compositions = compositions; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
}
