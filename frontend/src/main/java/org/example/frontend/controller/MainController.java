package org.example.frontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.frontend.model.Categorie;
import org.example.frontend.model.Recette;
import org.example.frontend.model.Regime;
import org.example.frontend.service.CategorieService;
import org.example.frontend.service.RecetteService;
import org.example.frontend.service.RegimeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    // ── Services ──────────────────────────────────────────────────────────────
    private final RecetteService recetteService = new RecetteService();
    private final CategorieService categorieService = new CategorieService();
    private final RegimeService regimeService = new RegimeService();

    // ── État ──────────────────────────────────────────────────────────────────
    private List<Recette> allRecettes = new ArrayList<>();
    private List<Categorie> categories = new ArrayList<>();
    private List<Regime> regimes = new ArrayList<>();
    private Recette selectedRecette;
    private boolean isEditMode;

    // ── Top bar ───────────────────────────────────────────────────────────────
    @FXML private Label recettesCountLabel;

    // ── Sidebar ───────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private ComboBox<Categorie> categorieFilter;
    @FXML private ComboBox<Regime> regimeFilter;
    @FXML private VBox categoriesList;
    @FXML private VBox regimesList;
    @FXML private TextField newCategorieField;
    @FXML private TextField newRegimeField;

    // ── Panneaux (StackPane) ──────────────────────────────────────────────────
    @FXML private VBox listPanel;
    @FXML private ScrollPane detailPanel;
    @FXML private ScrollPane formPanel;

    // ── Liste recettes ────────────────────────────────────────────────────────
    @FXML private FlowPane cardsContainer;
    @FXML private ProgressIndicator loadingIndicator;

    // ── Détail recette ────────────────────────────────────────────────────────
    @FXML private Label detailNom;
    @FXML private Label detailTemps;
    @FXML private Label detailPersonnes;
    @FXML private Label detailCategorie;
    @FXML private Label detailRegime;
    @FXML private Label detailDescription;
    @FXML private VBox compositionsList;
    @FXML private TextArea detailContenu;

    // ── Formulaire ────────────────────────────────────────────────────────────
    @FXML private Label formTitle;
    @FXML private TextField formNom;
    @FXML private TextArea formDescription;
    @FXML private TextField formTemps;
    @FXML private TextField formPersonnes;
    @FXML private ComboBox<Categorie> formCategorie;
    @FXML private ComboBox<Regime> formRegime;
    @FXML private TextArea formCompositions;
    @FXML private TextArea formContenu;

    // ── Initialisation ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((obs, o, n) -> filterRecettes());
        categorieFilter.valueProperty().addListener((obs, o, n) -> filterRecettes());
        regimeFilter.valueProperty().addListener((obs, o, n) -> filterRecettes());

        loadCategories();
        loadRegimes();
        loadRecettes();
    }

    // ── Navigation entre panneaux ─────────────────────────────────────────────

    @FXML
    public void showList() {
        listPanel.setVisible(true);
        listPanel.setManaged(true);
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    private void showDetail() {
        listPanel.setVisible(false);
        listPanel.setManaged(false);
        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
        formPanel.setVisible(false);
        formPanel.setManaged(false);
    }

    private void showForm() {
        listPanel.setVisible(false);
        listPanel.setManaged(false);
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    // ── Chargement des données ────────────────────────────────────────────────

    private void loadRecettes() {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            List<Recette> result = recetteService.getAll();
            Platform.runLater(() -> {
                allRecettes = result != null ? result : new ArrayList<>();
                filterRecettes();
                loadingIndicator.setVisible(false);
                updateCountLabel();
            });
        }).start();
    }

    private void loadCategories() {
        new Thread(() -> {
            List<Categorie> result = categorieService.getAll();
            Platform.runLater(() -> {
                categories = result != null ? result : new ArrayList<>();
                refreshCategoriesUI();
                refreshCategorieFilters();
            });
        }).start();
    }

    private void loadRegimes() {
        new Thread(() -> {
            List<Regime> result = regimeService.getAll();
            Platform.runLater(() -> {
                regimes = result != null ? result : new ArrayList<>();
                refreshRegimesUI();
                refreshRegimeFilters();
            });
        }).start();
    }

    // ── Filtrage et affichage des recettes ────────────────────────────────────

    private void filterRecettes() {
        String search = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        Categorie cat = categorieFilter.getValue();
        Regime reg = regimeFilter.getValue();

        List<Recette> filtered = allRecettes.stream()
                .filter(r -> search.isEmpty() || r.getNom().toLowerCase().contains(search))
                .filter(r -> cat == null || (r.getCategorie() != null && r.getCategorie().getId().equals(cat.getId())))
                .filter(r -> reg == null || (r.getRegime() != null && r.getRegime().getId().equals(reg.getId())))
                .collect(Collectors.toList());

        displayRecipes(filtered);
    }

    private void displayRecipes(List<Recette> recettes) {
        cardsContainer.getChildren().clear();
        if (recettes.isEmpty()) {
            Label empty = new Label("Aucune recette trouvée");
            empty.getStyleClass().add("empty-state-text");
            cardsContainer.getChildren().add(empty);
        } else {
            for (Recette r : recettes) {
                cardsContainer.getChildren().add(createRecipeCard(r));
            }
        }
        updateCountLabel();
    }

    private VBox createRecipeCard(Recette recette) {
        VBox card = new VBox();
        card.getStyleClass().add("recipe-card");
        card.setSpacing(0);

        // Zone image (placeholder)
        VBox imgZone = new VBox();
        imgZone.getStyleClass().add("recipe-card-image");
        imgZone.setPrefHeight(100);
        imgZone.setAlignment(Pos.CENTER);
        Label imgLabel = new Label("🍽");
        imgLabel.setStyle("-fx-font-size: 32px;");
        imgZone.getChildren().add(imgLabel);

        // Corps de la carte
        VBox body = new VBox(6);
        body.getStyleClass().add("recipe-card-body");

        Label title = new Label(recette.getNom());
        title.getStyleClass().add("recipe-card-title");
        title.setWrapText(true);

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

        body.getChildren().addAll(title, badges, meta);
        card.getChildren().addAll(imgZone, body);
        card.setOnMouseClicked(e -> openDetail(recette));
        return card;
    }

    private void updateCountLabel() {
        int n = allRecettes.size();
        recettesCountLabel.setText(n + " recette" + (n > 1 ? "s" : ""));
    }

    // ── Détail recette ────────────────────────────────────────────────────────

    private void openDetail(Recette recette) {
        selectedRecette = recette;
        detailNom.setText(recette.getNom());
        detailTemps.setText(recette.getTempsPreparation() != null ? String.valueOf(recette.getTempsPreparation()) : "—");
        detailPersonnes.setText(recette.getNbPersonnes() != null ? String.valueOf(recette.getNbPersonnes()) : "—");
        detailCategorie.setText(recette.getCategorie() != null ? recette.getCategorie().getNom() : "—");
        detailRegime.setText(recette.getRegime() != null ? recette.getRegime().getNom() : "—");
        detailDescription.setText(recette.getDescription() != null ? recette.getDescription() : "Pas de description.");

        compositionsList.getChildren().clear();
        if (recette.getCompositions() != null) {
            for (String comp : recette.getCompositions()) {
                Label l = new Label("• " + comp);
                l.getStyleClass().add("detail-value");
                compositionsList.getChildren().add(l);
            }
        }

        detailContenu.setText(recette.getContenu() != null ? recette.getContenu() : "");
        showDetail();
    }

    @FXML
    private void showEditForm() {
        if (selectedRecette != null) {
            openForm(selectedRecette);
        }
    }

    @FXML
    public void deleteRecette() {
        if (selectedRecette == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer la recette");
        confirm.setHeaderText("Supprimer « " + selectedRecette.getNom() + " » ?");
        confirm.setContentText("Cette action est irréversible.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Long id = selectedRecette.getId();
            new Thread(() -> {
                boolean ok = recetteService.delete(id);
                Platform.runLater(() -> {
                    if (ok) {
                        allRecettes.removeIf(r -> r.getId().equals(id));
                        selectedRecette = null;
                        filterRecettes();
                        showList();
                    }
                });
            }).start();
        }
    }

    // ── Formulaire recette ────────────────────────────────────────────────────

    @FXML
    public void showNewForm() {
        openForm(null);
    }

    private void openForm(Recette recette) {
        isEditMode = recette != null;
        formTitle.setText(isEditMode ? "Modifier la recette" : "Nouvelle recette");

        formNom.setText(recette != null ? recette.getNom() : "");
        formDescription.setText(recette != null && recette.getDescription() != null ? recette.getDescription() : "");
        formTemps.setText(recette != null && recette.getTempsPreparation() != null ? String.valueOf(recette.getTempsPreparation()) : "");
        formPersonnes.setText(recette != null && recette.getNbPersonnes() != null ? String.valueOf(recette.getNbPersonnes()) : "");
        formContenu.setText(recette != null && recette.getContenu() != null ? recette.getContenu() : "");

        if (recette != null && recette.getCompositions() != null) {
            formCompositions.setText(String.join("\n", recette.getCompositions()));
        } else {
            formCompositions.setText("");
        }

        if (recette != null && recette.getCategorie() != null) {
            final Long catId = recette.getCategorie().getId();
            formCategorie.getItems().stream()
                    .filter(c -> c.getId().equals(catId))
                    .findFirst().ifPresent(formCategorie::setValue);
        } else {
            formCategorie.setValue(null);
        }

        if (recette != null && recette.getRegime() != null) {
            final Long regId = recette.getRegime().getId();
            formRegime.getItems().stream()
                    .filter(r -> r.getId().equals(regId))
                    .findFirst().ifPresent(formRegime::setValue);
        } else {
            formRegime.setValue(null);
        }

        showForm();
    }

    @FXML
    public void saveRecette() {
        String nom = formNom.getText().trim();
        if (nom.isEmpty()) {
            showAlert("Champ requis", "Le nom de la recette est obligatoire.");
            return;
        }

        Recette r = new Recette();
        r.setNom(nom);
        r.setDescription(formDescription.getText().trim().isEmpty() ? null : formDescription.getText().trim());
        r.setCategorie(formCategorie.getValue());
        r.setRegime(formRegime.getValue());
        r.setContenu(formContenu.getText().trim().isEmpty() ? null : formContenu.getText().trim());

        if (!formTemps.getText().trim().isEmpty()) {
            try { r.setTempsPreparation(Integer.parseInt(formTemps.getText().trim())); }
            catch (NumberFormatException e) { showAlert("Erreur", "Temps invalide (entier requis)."); return; }
        }
        if (!formPersonnes.getText().trim().isEmpty()) {
            try { r.setNbPersonnes(Integer.parseInt(formPersonnes.getText().trim())); }
            catch (NumberFormatException e) { showAlert("Erreur", "Nb personnes invalide (entier requis)."); return; }
        }

        String compoText = formCompositions.getText().trim();
        if (!compoText.isEmpty()) {
            List<String> compos = new ArrayList<>();
            for (String line : compoText.split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) compos.add(trimmed);
            }
            r.setCompositions(compos);
        }

        if (isEditMode && selectedRecette != null) {
            Long id = selectedRecette.getId();
            new Thread(() -> {
                Recette updated = recetteService.update(id, r);
                Platform.runLater(() -> {
                    if (updated != null) {
                        allRecettes.replaceAll(rec -> rec.getId().equals(id) ? updated : rec);
                        selectedRecette = updated;
                        openDetail(updated);
                    }
                });
            }).start();
        } else {
            new Thread(() -> {
                Recette created = recetteService.create(r);
                Platform.runLater(() -> {
                    if (created != null) {
                        allRecettes.add(created);
                        filterRecettes();
                        openDetail(created);
                    } else {
                        showList();
                    }
                });
            }).start();
        }
    }

    // ── Catégories ────────────────────────────────────────────────────────────

    private void refreshCategoriesUI() {
        categoriesList.getChildren().clear();
        for (Categorie c : categories) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(c.getNom());
            nameLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #52525B; -fx-cursor: hand; -fx-padding: 2 5; -fx-font-size: 10px;");
            delBtn.setOnAction(e -> deleteCategorie(c));
            row.getChildren().addAll(nameLabel, delBtn);
            categoriesList.getChildren().add(row);
        }
    }

    private void refreshCategorieFilters() {
        Categorie selected = categorieFilter.getValue();
        categorieFilter.getItems().setAll(categories);
        formCategorie.getItems().setAll(categories);
        if (selected != null) {
            categorieFilter.getItems().stream()
                    .filter(c -> c.getId().equals(selected.getId()))
                    .findFirst().ifPresent(categorieFilter::setValue);
        }
    }

    @FXML
    public void addCategorie() {
        String nom = newCategorieField.getText().trim();
        if (nom.isEmpty()) return;
        Categorie c = new Categorie();
        c.setNom(nom);
        new Thread(() -> {
            Categorie created = categorieService.create(c);
            Platform.runLater(() -> {
                if (created != null) {
                    categories.add(created);
                    refreshCategoriesUI();
                    refreshCategorieFilters();
                    newCategorieField.clear();
                }
            });
        }).start();
    }

    private void deleteCategorie(Categorie c) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer");
        confirm.setHeaderText("Supprimer la catégorie « " + c.getNom() + " » ?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            new Thread(() -> {
                boolean ok = categorieService.delete(c.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        categories.removeIf(cat -> cat.getId().equals(c.getId()));
                        refreshCategoriesUI();
                        refreshCategorieFilters();
                    }
                });
            }).start();
        }
    }

    // ── Régimes ───────────────────────────────────────────────────────────────

    private void refreshRegimesUI() {
        regimesList.getChildren().clear();
        for (Regime r : regimes) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(r.getNom());
            nameLabel.setStyle("-fx-text-fill: #A1A1AA; -fx-font-size: 12px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #52525B; -fx-cursor: hand; -fx-padding: 2 5; -fx-font-size: 10px;");
            delBtn.setOnAction(e -> deleteRegime(r));
            row.getChildren().addAll(nameLabel, delBtn);
            regimesList.getChildren().add(row);
        }
    }

    private void refreshRegimeFilters() {
        Regime selected = regimeFilter.getValue();
        regimeFilter.getItems().setAll(regimes);
        formRegime.getItems().setAll(regimes);
        if (selected != null) {
            regimeFilter.getItems().stream()
                    .filter(r -> r.getId().equals(selected.getId()))
                    .findFirst().ifPresent(regimeFilter::setValue);
        }
    }

    @FXML
    public void addRegime() {
        String nom = newRegimeField.getText().trim();
        if (nom.isEmpty()) return;
        Regime r = new Regime();
        r.setNom(nom);
        new Thread(() -> {
            Regime created = regimeService.create(r);
            Platform.runLater(() -> {
                if (created != null) {
                    regimes.add(created);
                    refreshRegimesUI();
                    refreshRegimeFilters();
                    newRegimeField.clear();
                }
            });
        }).start();
    }

    private void deleteRegime(Regime r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer");
        confirm.setHeaderText("Supprimer le régime « " + r.getNom() + " » ?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            new Thread(() -> {
                boolean ok = regimeService.delete(r.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        regimes.removeIf(reg -> reg.getId().equals(r.getId()));
                        refreshRegimesUI();
                        refreshRegimeFilters();
                    }
                });
            }).start();
        }
    }

    // ── Filtres ───────────────────────────────────────────────────────────────

    @FXML
    public void resetFilters() {
        searchField.clear();
        categorieFilter.setValue(null);
        regimeFilter.setValue(null);
        showList();
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
