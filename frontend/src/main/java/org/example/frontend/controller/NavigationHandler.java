package org.example.frontend.controller;

import javafx.scene.layout.Region;

/**
 * Gère la navigation entre les trois panneaux de la page unique.
 *
 * Les panneaux sont empilés dans un StackPane ; un seul est visible à la fois.
 * Pour masquer un panneau sans qu'il continue à occuper de l'espace dans le layout,
 * il faut désactiver à la fois setVisible ET setManaged.
 */
class NavigationHandler {

    private final MainController ctrl;

    NavigationHandler(MainController ctrl) {
        this.ctrl = ctrl;
    }

    /** Affiche la grille de cartes et masque détail et formulaire. */
    void showList() {
        show(ctrl.listPanel,   true);
        show(ctrl.detailPanel, false);
        show(ctrl.formPanel,   false);
    }

    /** Affiche le détail d'une recette et masque liste et formulaire. */
    void showDetail() {
        show(ctrl.listPanel,   false);
        show(ctrl.detailPanel, true);
        show(ctrl.formPanel,   false);
    }

    /** Affiche le formulaire de création / édition et masque les autres panneaux. */
    void showForm() {
        show(ctrl.listPanel,   false);
        show(ctrl.detailPanel, false);
        show(ctrl.formPanel,   true);
    }

    // ── Utilitaire privé ──────────────────────────────────────────────────────

    /** Active ou désactive simultanément la visibilité et la gestion de layout d'un nœud. */
    private void show(Region node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
