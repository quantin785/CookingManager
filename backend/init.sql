-- Script d'initialisation pour la base de données recettes_db
-- Ce script sera exécuté automatiquement au premier démarrage du conteneur MySQL

-- Création de la base de données si elle n'existe pas (déjà créée par les variables d'environnement)
-- CREATE DATABASE IF NOT EXISTS recettes_db;

-- Utilisation de la base de données
USE recettes_db;

-- Insertion de données initiales (optionnel)
-- Vous pouvez décommenter ces lignes pour avoir des données de test

-- INSERT INTO categorie (nom) VALUES 
-- ('Entrée'),
-- ('Plat principal'),
-- ('Dessert'),
-- ('Boisson');

-- INSERT INTO regime (nom) VALUES 
-- ('Végétarien'),
-- ('Végan'),
-- ('Sans gluten'),
-- ('Cétogène'),
-- ('Méditerranéen');

-- INSERT INTO ingredient (nom, uniteDefaut) VALUES 
-- ('Tomate', 'pièce'),
-- ('Oignon', 'pièce'),
-- ('Ail', 'gousse'),
-- ('Huile d''olive', 'c.à.s'),
-- ('Sel', 'pincée'),
-- ('Poivre', 'pincée'),
-- ('Farine', 'g'),
-- ('Sucre', 'g'),
-- ('Beurre', 'g'),
-- ('Lait', 'ml');
