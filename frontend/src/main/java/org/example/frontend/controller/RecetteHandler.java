package org.example.frontend.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.frontend.model.Recette;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gère l'ensemble du cycle de vie des recettes :
 *   - Chargement depuis le backend
 *   - Filtrage (recherche texte + catégorie + régime)
 *   - Affichage sous forme de cartes
 *   - Panneau de détail
 *   - Formulaire de création / édition
 *   - Sauvegarde et suppression
 */
class RecetteHandler {

    private final MainController ctrl;
    private final NavigationHandler nav;

    RecetteHandler(MainController ctrl) {
        this.ctrl = ctrl;
        this.nav  = ctrl.navigation;
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    /** Charge toutes les recettes depuis le backend dans un thread secondaire. */
    void loadRecettes() {
        ctrl.loadingIndicator.setVisible(true);
        new Thread(() -> {
            List<Recette> result = ctrl.recetteService.getAll();
            Platform.runLater(() -> {
                ctrl.allRecettes = result != null ? result : new ArrayList<>();
                filterRecettes();
                ctrl.loadingIndicator.setVisible(false);
                updateCountLabel();
            });
        }).start();
    }

    // ── Filtrage ──────────────────────────────────────────────────────────────

    /**
     * Filtre la liste en mémoire selon :
     *   - le texte saisi dans la barre de recherche (insensible à la casse)
     *   - la catégorie sélectionnée dans le combobox
     *   - le régime sélectionné dans le combobox
     * Appelé automatiquement à chaque changement de ces trois champs.
     */
    void filterRecettes() {
        String search = ctrl.searchField.getText() != null
                ? ctrl.searchField.getText().trim().toLowerCase() : "";
        var cat = ctrl.categorieFilter.getValue();
        var reg = ctrl.regimeFilter.getValue();

        List<Recette> filtered = ctrl.allRecettes.stream()
                .filter(r -> search.isEmpty() || r.getNom().toLowerCase().contains(search))
                .filter(r -> cat == null || (r.getCategorie() != null && r.getCategorie().getId().equals(cat.getId())))
                .filter(r -> reg == null || (r.getRegime() != null && r.getRegime().getId().equals(reg.getId())))
                .collect(Collectors.toList());

        displayRecipes(filtered);
    }

    // ── Affichage des cartes ──────────────────────────────────────────────────

    /** Remplace le contenu de la grille par les cartes des recettes filtrées. */
    private void displayRecipes(List<Recette> recettes) {
        ctrl.cardsContainer.getChildren().clear();
        if (recettes.isEmpty()) {
            Label empty = new Label("Aucune recette trouvée");
            empty.getStyleClass().add("empty-state-text");
            ctrl.cardsContainer.getChildren().add(empty);
        } else {
            for (Recette r : recettes) {
                ctrl.cardsContainer.getChildren().add(createRecipeCard(r));
            }
        }
        updateCountLabel();
    }

    /** Construit la carte visuelle d'une recette (image + titre + badges + méta). */
    private VBox createRecipeCard(Recette recette) {
        VBox card = new VBox();
        card.getStyleClass().add("recipe-card");
        card.setSpacing(0);

        // Zone image : emoji placeholder centré
        VBox imgZone = new VBox();
        imgZone.getStyleClass().add("recipe-card-image");
        imgZone.setPrefHeight(100);
        imgZone.setAlignment(Pos.CENTER);
        Label imgLabel = new Label("🍽");
        imgLabel.setStyle("-fx-font-size: 32px;");
        imgZone.getChildren().add(imgLabel);

        // Corps de la carte : titre, badges, méta-données
        VBox body = new VBox(6);
        body.getStyleClass().add("recipe-card-body");

        Label title = new Label(recette.getNom());
        title.getStyleClass().add("recipe-card-title");
        title.setWrapText(true);

        body.getChildren().addAll(title, buildBadges(recette), buildMeta(recette));
        card.getChildren().addAll(imgZone, body);

        // Clic sur la carte → panneau de détail
        card.setOnMouseClicked(e -> openDetail(recette));
        return card;
    }

    /** Construit la ligne de badges catégorie (orange) et régime (vert). */
    private HBox buildBadges(Recette recette) {
        HBox badges = new HBox(4);
        if (recette.getCategorie() != null) {
            Label catBadge = new Label(recette.getCategorie().getNom());
            catBadge.getStyleClass().add("recipe-card-badge");
            badges.getChildren().add(catBadge);
        }
        if (recette.getRegime() != null) {
            Label regBadge = new Label(recette.getRegime().getNom());
            regBadge.getStyleClass().addAll("recipe-card-badge", "badge-green");
            badges.getChildren().add(regBadge);
        }
        return badges;
    }

    /** Construit la ligne de méta-données : temps de préparation et nombre de personnes. */
    private HBox buildMeta(Recette recette) {
        HBox meta = new HBox(10);
        meta.getStyleClass().add("recipe-card-meta");
        if (recette.getTempsPreparation() != null) {
            Label time = new Label("⏱ " + recette.getTempsPreparation() + " min");
            time.setStyle("-fx-font-size: 11px; -fx-text-fill: #71717A;");
            meta.getChildren().add(time);
        }
        if (recette.getNbPersonnes() != null) {
            Label pers = new Label("👤 " + recette.getNbPersonnes());
            pers.setStyle("-fx-font-size: 11px; -fx-text-fill: #71717A;");
            meta.getChildren().add(pers);
        }
        return meta;
    }

    /** Met à jour le compteur affiché dans la top bar. */
    private void updateCountLabel() {
        int n = ctrl.allRecettes.size();
        ctrl.recettesCountLabel.setText(n + " recette" + (n > 1 ? "s" : ""));
    }

    // ── Détail ────────────────────────────────────────────────────────────────

    /** Remplit le panneau de détail avec les données de la recette et l'affiche. */
    void openDetail(Recette recette) {
        ctrl.selectedRecette = recette;

        // Informations principales
        ctrl.detailNom.setText(recette.getNom());
        ctrl.detailTemps.setText(recette.getTempsPreparation() != null
                ? String.valueOf(recette.getTempsPreparation()) : "—");
        ctrl.detailPersonnes.setText(recette.getNbPersonnes() != null
                ? String.valueOf(recette.getNbPersonnes()) : "—");
        ctrl.detailCategorie.setText(recette.getCategorie() != null
                ? recette.getCategorie().getNom() : "—");
        ctrl.detailRegime.setText(recette.getRegime() != null
                ? recette.getRegime().getNom() : "—");
        ctrl.detailDescription.setText(recette.getDescription() != null
                ? recette.getDescription() : "Pas de description.");

        // Liste des compositions (une ligne = un élément)
        ctrl.compositionsList.getChildren().clear();
        if (recette.getCompositions() != null) {
            for (String comp : recette.getCompositions()) {
                Label l = new Label("• " + comp);
                l.getStyleClass().add("detail-value");
                ctrl.compositionsList.getChildren().add(l);
            }
        }

        ctrl.detailContenu.setText(recette.getContenu() != null ? recette.getContenu() : "");
        nav.showDetail();
    }

    /** Demande confirmation puis supprime la recette sélectionnée. */
    void deleteRecette() {
        if (ctrl.selectedRecette == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer la recette");
        confirm.setHeaderText("Supprimer « " + ctrl.selectedRecette.getNom() + " » ?");
        confirm.setContentText("Cette action est irréversible.");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Long id = ctrl.selectedRecette.getId();
            new Thread(() -> {
                boolean ok = ctrl.recetteService.delete(id);
                Platform.runLater(() -> {
                    if (ok) {
                        ctrl.allRecettes.removeIf(r -> r.getId().equals(id));
                        ctrl.selectedRecette = null;
                        filterRecettes();
                        nav.showList();
                    }
                });
            }).start();
        }
    }

    // ── Formulaire ────────────────────────────────────────────────────────────

    /**
     * Ouvre le formulaire.
     * @param recette null → mode création (champs vides), non-null → mode édition (champs pré-remplis).
     */
    void openForm(Recette recette) {
        ctrl.isEditMode = recette != null;
        ctrl.formTitle.setText(ctrl.isEditMode ? "Modifier la recette" : "Nouvelle recette");

        // Champs texte et zones de texte
        ctrl.formNom.setText(recette != null ? recette.getNom() : "");
        ctrl.formDescription.setText(recette != null && recette.getDescription() != null
                ? recette.getDescription() : "");
        ctrl.formTemps.setText(recette != null && recette.getTempsPreparation() != null
                ? String.valueOf(recette.getTempsPreparation()) : "");
        ctrl.formPersonnes.setText(recette != null && recette.getNbPersonnes() != null
                ? String.valueOf(recette.getNbPersonnes()) : "");
        ctrl.formContenu.setText(recette != null && recette.getContenu() != null
                ? recette.getContenu() : "");
        ctrl.formCompositions.setText(recette != null && recette.getCompositions() != null
                ? String.join("\n", recette.getCompositions()) : "");

        // Sélection de la catégorie dans le combobox (recherche par id)
        if (recette != null && recette.getCategorie() != null) {
            final Long catId = recette.getCategorie().getId();
            ctrl.formCategorie.getItems().stream()
                    .filter(c -> c.getId().equals(catId))
                    .findFirst().ifPresent(ctrl.formCategorie::setValue);
        } else {
            ctrl.formCategorie.setValue(null);
        }

        // Sélection du régime dans le combobox (recherche par id)
        if (recette != null && recette.getRegime() != null) {
            final Long regId = recette.getRegime().getId();
            ctrl.formRegime.getItems().stream()
                    .filter(r -> r.getId().equals(regId))
                    .findFirst().ifPresent(ctrl.formRegime::setValue);
        } else {
            ctrl.formRegime.setValue(null);
        }

        nav.showForm();
    }

    /** Valide, construit et envoie la recette au backend (création ou mise à jour). */
    void saveRecette() {
        String nom = ctrl.formNom.getText().trim();
        if (nom.isEmpty()) {
            MainController.showAlert("Champ requis", "Le nom de la recette est obligatoire.");
            return;
        }

        // Construction de l'objet à partir du formulaire (retourne null si validation échoue)
        Recette r = buildRecetteFromForm(nom);
        if (r == null) return;

        if (ctrl.isEditMode && ctrl.selectedRecette != null) {
            // Mise à jour d'une recette existante
            Long id = ctrl.selectedRecette.getId();
            new Thread(() -> {
                Recette updated = ctrl.recetteService.update(id, r);
                Platform.runLater(() -> {
                    if (updated != null) {
                        ctrl.allRecettes.replaceAll(rec -> rec.getId().equals(id) ? updated : rec);
                        ctrl.selectedRecette = updated;
                        openDetail(updated);
                    }
                });
            }).start();
        } else {
            // Création d'une nouvelle recette
            new Thread(() -> {
                Recette created = ctrl.recetteService.create(r);
                Platform.runLater(() -> {
                    if (created != null) {
                        ctrl.allRecettes.add(created);
                        filterRecettes();
                        openDetail(created);
                    } else {
                        nav.showList();
                    }
                });
            }).start();
        }
    }

    /**
     * Construit un objet {@link Recette} à partir des valeurs du formulaire.
     * Retourne {@code null} si un champ numérique est invalide (l'alerte est déjà affichée).
     */
    private Recette buildRecetteFromForm(String nom) {
        Recette r = new Recette();
        r.setNom(nom);
        r.setDescription(emptyToNull(ctrl.formDescription.getText()));
        r.setContenu(emptyToNull(ctrl.formContenu.getText()));
        r.setCategorie(ctrl.formCategorie.getValue());
        r.setRegime(ctrl.formRegime.getValue());

        // Temps de préparation (entier optionnel)
        if (!ctrl.formTemps.getText().trim().isEmpty()) {
            try {
                r.setTempsPreparation(Integer.parseInt(ctrl.formTemps.getText().trim()));
            } catch (NumberFormatException e) {
                MainController.showAlert("Erreur", "Temps invalide (entier requis).");
                return null;
            }
        }

        // Nombre de personnes (entier optionnel)
        if (!ctrl.formPersonnes.getText().trim().isEmpty()) {
            try {
                r.setNbPersonnes(Integer.parseInt(ctrl.formPersonnes.getText().trim()));
            } catch (NumberFormatException e) {
                MainController.showAlert("Erreur", "Nb personnes invalide (entier requis).");
                return null;
            }
        }

        // Compositions : une entrée par ligne non vide
        String compoText = ctrl.formCompositions.getText().trim();
        if (!compoText.isEmpty()) {
            List<String> compos = new ArrayList<>();
            for (String line : compoText.split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) compos.add(trimmed);
            }
            r.setCompositions(compos);
        }

        return r;
    }

    /** Retourne null si la chaîne est vide après trim, sinon retourne la chaîne trimmée. */
    private String emptyToNull(String s) {
        String t = s != null ? s.trim() : "";
        return t.isEmpty() ? null : t;
    }
}
