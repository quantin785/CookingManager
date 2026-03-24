package org.example.frontend.controller;

import javafx.fxml.FXML;
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

/**
 * Contrôleur principal de la page unique.
 *
 * Ce fichier est le point d'entrée JavaFX (fx:controller dans main.fxml).
 * Son seul rôle est de déclarer les bindings @FXML, d'initialiser les handlers
 * et de câbler les actions FXML sur ces handlers.
 *
 * Chaque fonctionnalité est déléguée à un handler dédié :
 *   - NavigationHandler  → changement de panneau
 *   - RecetteHandler     → CRUD recettes, filtrage, cartes, détail, formulaire
 *   - CategorieHandler   → CRUD catégories
 *   - RegimeHandler      → CRUD régimes
 */
public class MainController {

    // ── Services (accès HTTP vers le backend) ─────────────────────────────────
    final RecetteService   recetteService   = new RecetteService();
    final CategorieService categorieService = new CategorieService();
    final RegimeService    regimeService    = new RegimeService();

    // ── État partagé entre handlers ───────────────────────────────────────────
    List<Recette>   allRecettes    = new ArrayList<>();
    List<Categorie> categories     = new ArrayList<>();
    List<Regime>    regimes        = new ArrayList<>();
    Recette         selectedRecette;
    boolean         isEditMode;

    // ── Top bar ───────────────────────────────────────────────────────────────
    @FXML Label recettesCountLabel;

    // ── Sidebar ───────────────────────────────────────────────────────────────
    @FXML TextField          searchField;
    @FXML ComboBox<Categorie> categorieFilter;
    @FXML ComboBox<Regime>    regimeFilter;
    @FXML VBox               categoriesList;
    @FXML VBox               regimesList;
    @FXML TextField          newCategorieField;
    @FXML TextField          newRegimeField;

    // ── Panneaux (StackPane — un seul visible à la fois) ─────────────────────
    @FXML VBox       listPanel;
    @FXML ScrollPane detailPanel;
    @FXML ScrollPane formPanel;

    // ── Liste des recettes ────────────────────────────────────────────────────
    @FXML FlowPane         cardsContainer;
    @FXML ProgressIndicator loadingIndicator;

    // ── Panneau de détail ─────────────────────────────────────────────────────
    @FXML Label   detailNom;
    @FXML Label   detailTemps;
    @FXML Label   detailPersonnes;
    @FXML Label   detailCategorie;
    @FXML Label   detailRegime;
    @FXML Label   detailDescription;
    @FXML VBox    compositionsList;
    @FXML TextArea detailContenu;

    // ── Formulaire de création / édition ──────────────────────────────────────
    @FXML Label              formTitle;
    @FXML TextField          formNom;
    @FXML TextArea           formDescription;
    @FXML TextField          formTemps;
    @FXML TextField          formPersonnes;
    @FXML ComboBox<Categorie> formCategorie;
    @FXML ComboBox<Regime>    formRegime;
    @FXML TextArea           formCompositions;
    @FXML TextArea           formContenu;

    // ── Handlers (injectés dans initialize) ──────────────────────────────────
    NavigationHandler navigation;
    RecetteHandler    recetteHandler;
    CategorieHandler  categorieHandler;
    RegimeHandler     regimeHandler;

    // ── Initialisation ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Instanciation des handlers en leur passant ce contrôleur comme contexte partagé
        navigation       = new NavigationHandler(this);
        categorieHandler = new CategorieHandler(this);
        regimeHandler    = new RegimeHandler(this);
        recetteHandler   = new RecetteHandler(this);

        // Filtrage en temps réel dès qu'un critère change
        searchField.textProperty().addListener((obs, o, n) -> recetteHandler.filterRecettes());
        categorieFilter.valueProperty().addListener((obs, o, n) -> recetteHandler.filterRecettes());
        regimeFilter.valueProperty().addListener((obs, o, n) -> recetteHandler.filterRecettes());

        // Chargement initial depuis le backend
        categorieHandler.loadCategories();
        regimeHandler.loadRegimes();
        recetteHandler.loadRecettes();
    }

    // ── Actions FXML – Navigation ─────────────────────────────────────────────

    /** Retour à la liste (bouton « ← » dans les panneaux détail et formulaire). */
    @FXML public void showList() { navigation.showList(); }

    // ── Actions FXML – Recettes ───────────────────────────────────────────────

    /** Ouvre le formulaire vide pour créer une nouvelle recette. */
    @FXML public void showNewForm()   { recetteHandler.openForm(null); }

    /** Ouvre le formulaire pré-rempli pour modifier la recette affichée. */
    @FXML public void showEditForm()  { if (selectedRecette != null) recetteHandler.openForm(selectedRecette); }

    /** Supprime la recette affichée après confirmation. */
    @FXML public void deleteRecette() { recetteHandler.deleteRecette(); }

    /** Valide et envoie le formulaire (création ou mise à jour). */
    @FXML public void saveRecette()   { recetteHandler.saveRecette(); }

    // ── Actions FXML – Catégories ─────────────────────────────────────────────

    /** Ajoute la catégorie saisie dans la sidebar. */
    @FXML public void addCategorie() { categorieHandler.addCategorie(); }

    // ── Actions FXML – Régimes ────────────────────────────────────────────────

    /** Ajoute le régime saisi dans la sidebar. */
    @FXML public void addRegime() { regimeHandler.addRegime(); }

    // ── Actions FXML – Filtres ────────────────────────────────────────────────

    /** Réinitialise tous les filtres et revient à la liste complète. */
    @FXML public void resetFilters() {
        searchField.clear();
        categorieFilter.setValue(null);
        regimeFilter.setValue(null);
        navigation.showList();
    }

    // ── Utilitaire partagé ────────────────────────────────────────────────────

    /** Affiche une alerte d'erreur modale. Accessible depuis tous les handlers. */
    static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
