package org.example.frontend.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.frontend.model.Categorie;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gère les catégories de recettes :
 *   - Chargement initial depuis le backend
 *   - Affichage dans la sidebar (liste avec bouton de suppression)
 *   - Synchronisation des combobox (filtre sidebar + combobox formulaire)
 *   - Ajout et suppression
 */
class CategorieHandler {

    private final MainController ctrl;

    CategorieHandler(MainController ctrl) {
        this.ctrl = ctrl;
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    /** Charge toutes les catégories depuis le backend dans un thread secondaire. */
    void loadCategories() {
        new Thread(() -> {
            List<Categorie> result = ctrl.categorieService.getAll();
            Platform.runLater(() -> {
                ctrl.categories = result != null ? result : new ArrayList<>();
                refreshCategoriesUI();
                refreshCategorieFilters();
            });
        }).start();
    }

    // ── Affichage sidebar ─────────────────────────────────────────────────────

    /**
     * Recrée la liste des catégories dans la sidebar.
     * Chaque ligne affiche le nom et un bouton ✕ pour supprimer.
     */
    void refreshCategoriesUI() {
        ctrl.categoriesList.getChildren().clear();
        for (Categorie c : ctrl.categories) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(c.getNom());
            nameLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #52525B; "
                    + "-fx-cursor: hand; -fx-padding: 2 5; -fx-font-size: 10px;");
            delBtn.setOnAction(e -> deleteCategorie(c));

            row.getChildren().addAll(nameLabel, delBtn);
            ctrl.categoriesList.getChildren().add(row);
        }
    }

    // ── Synchronisation des combobox ──────────────────────────────────────────

    /**
     * Met à jour le combobox de filtre (sidebar) et le combobox du formulaire
     * avec la liste courante des catégories.
     * La sélection précédente du filtre est restaurée si la catégorie existe encore.
     */
    void refreshCategorieFilters() {
        Categorie selected = ctrl.categorieFilter.getValue();
        ctrl.categorieFilter.getItems().setAll(ctrl.categories);
        ctrl.formCategorie.getItems().setAll(ctrl.categories);

        // Restauration de la sélection après rechargement
        if (selected != null) {
            ctrl.categorieFilter.getItems().stream()
                    .filter(c -> c.getId().equals(selected.getId()))
                    .findFirst().ifPresent(ctrl.categorieFilter::setValue);
        }
    }

    // ── Ajout ─────────────────────────────────────────────────────────────────

    /** Crée une nouvelle catégorie à partir du champ de saisie de la sidebar. */
    void addCategorie() {
        String nom = ctrl.newCategorieField.getText().trim();
        if (nom.isEmpty()) return;

        Categorie c = new Categorie();
        c.setNom(nom);
        new Thread(() -> {
            Categorie created = ctrl.categorieService.create(c);
            Platform.runLater(() -> {
                if (created != null) {
                    ctrl.categories.add(created);
                    refreshCategoriesUI();
                    refreshCategorieFilters();
                    ctrl.newCategorieField.clear();
                }
            });
        }).start();
    }

    // ── Suppression ───────────────────────────────────────────────────────────

    /** Demande confirmation puis supprime la catégorie. */
    private void deleteCategorie(Categorie c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer");
        confirm.setHeaderText("Supprimer la catégorie « " + c.getNom() + " » ?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            new Thread(() -> {
                boolean ok = ctrl.categorieService.delete(c.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        ctrl.categories.removeIf(cat -> cat.getId().equals(c.getId()));
                        refreshCategoriesUI();
                        refreshCategorieFilters();
                    }
                });
            }).start();
        }
    }
}
