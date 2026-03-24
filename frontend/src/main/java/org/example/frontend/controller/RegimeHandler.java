package org.example.frontend.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.frontend.model.Regime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gère les régimes alimentaires :
 *   - Chargement initial depuis le backend
 *   - Affichage dans la sidebar (liste avec bouton de suppression)
 *   - Synchronisation des combobox (filtre sidebar + combobox formulaire)
 *   - Ajout et suppression
 */
class RegimeHandler {

    private final MainController ctrl;

    RegimeHandler(MainController ctrl) {
        this.ctrl = ctrl;
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    /** Charge tous les régimes depuis le backend dans un thread secondaire. */
    void loadRegimes() {
        new Thread(() -> {
            List<Regime> result = ctrl.regimeService.getAll();
            Platform.runLater(() -> {
                ctrl.regimes = result != null ? result : new ArrayList<>();
                refreshRegimesUI();
                refreshRegimeFilters();
            });
        }).start();
    }

    // ── Affichage sidebar ─────────────────────────────────────────────────────

    /**
     * Recrée la liste des régimes dans la sidebar.
     * Chaque ligne affiche le nom et un bouton ✕ pour supprimer.
     */
    void refreshRegimesUI() {
        ctrl.regimesList.getChildren().clear();
        for (Regime r : ctrl.regimes) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(r.getNom());
            nameLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #52525B; "
                    + "-fx-cursor: hand; -fx-padding: 2 5; -fx-font-size: 10px;");
            delBtn.setOnAction(e -> deleteRegime(r));

            row.getChildren().addAll(nameLabel, delBtn);
            ctrl.regimesList.getChildren().add(row);
        }
    }

    // ── Synchronisation des combobox ──────────────────────────────────────────

    /**
     * Met à jour le combobox de filtre (sidebar) et le combobox du formulaire
     * avec la liste courante des régimes.
     * La sélection précédente du filtre est restaurée si le régime existe encore.
     */
    void refreshRegimeFilters() {
        Regime selected = ctrl.regimeFilter.getValue();
        ctrl.regimeFilter.getItems().setAll(ctrl.regimes);
        ctrl.formRegime.getItems().setAll(ctrl.regimes);

        // Restauration de la sélection après rechargement
        if (selected != null) {
            ctrl.regimeFilter.getItems().stream()
                    .filter(r -> r.getId().equals(selected.getId()))
                    .findFirst().ifPresent(ctrl.regimeFilter::setValue);
        }
    }

    // ── Ajout ─────────────────────────────────────────────────────────────────

    /** Crée un nouveau régime à partir du champ de saisie de la sidebar. */
    void addRegime() {
        String nom = ctrl.newRegimeField.getText().trim();
        if (nom.isEmpty()) return;

        Regime r = new Regime();
        r.setNom(nom);
        new Thread(() -> {
            Regime created = ctrl.regimeService.create(r);
            Platform.runLater(() -> {
                if (created != null) {
                    ctrl.regimes.add(created);
                    refreshRegimesUI();
                    refreshRegimeFilters();
                    ctrl.newRegimeField.clear();
                }
            });
        }).start();
    }

    // ── Suppression ───────────────────────────────────────────────────────────

    /** Demande confirmation puis supprime le régime. */
    private void deleteRegime(Regime r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer");
        confirm.setHeaderText("Supprimer le régime « " + r.getNom() + " » ?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            new Thread(() -> {
                boolean ok = ctrl.regimeService.delete(r.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        ctrl.regimes.removeIf(reg -> reg.getId().equals(r.getId()));
                        refreshRegimesUI();
                        refreshRegimeFilters();
                    }
                });
            }).start();
        }
    }
}
