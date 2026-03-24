# CookingManager

Application de gestion de recettes culinaires — Client lourd JavaFX + API REST Spring Boot.

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java JDK | 21 |
| Maven | 3.8+ |
| MySQL | 8.0+ (ou H2 en dev) |
| Git | 2.x |

---

## Structure du projet

```
CookingManager/
├── backend/    — API REST Spring Boot (port 8080)
└── frontend/   — Client lourd JavaFX
```

---

## Installation & Lancement

### 1. Cloner le projet

```bash
git clone https://github.com/quantin785/CookingManager.git
cd CookingManager
```

### 2. Configurer la base de données

#### Option A — MySQL

Créer la base de données :

```sql
CREATE DATABASE cookingmanager;
```

Modifier le fichier `backend/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cookingmanager
spring.datasource.username=VOTRE_UTILISATEUR
spring.datasource.password=VOTRE_MOT_DE_PASSE
spring.jpa.hibernate.ddl-auto=update
```

#### Option B — Docker (recommandé)

```bash
cd backend
docker-compose up -d
```

### 3. Lancer le backend

```bash
cd backend
mvn spring-boot:run
```

L'API est accessible sur : `http://localhost:8080`

### 4. Lancer le frontend

```bash
cd frontend
mvn javafx:run
```

---

## Endpoints API

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/recettes` | Liste toutes les recettes |
| GET | `/api/recettes/{id}` | Détail d'une recette |
| GET | `/api/recettes/search?nom=` | Recherche par nom |
| GET | `/api/recettes/categorie/{id}` | Filtrer par catégorie |
| GET | `/api/recettes/regime/{id}` | Filtrer par régime |
| POST | `/api/recettes` | Créer une recette |
| PUT | `/api/recettes/{id}` | Modifier une recette |
| DELETE | `/api/recettes/{id}` | Supprimer une recette |
| GET | `/api/categories` | Liste des catégories |
| POST | `/api/categories` | Créer une catégorie |
| PUT | `/api/categories/{id}` | Modifier une catégorie |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie |
| GET | `/api/regimes` | Liste des régimes |
| POST | `/api/regimes` | Créer un régime |
| PUT | `/api/regimes/{id}` | Modifier un régime |
| DELETE | `/api/regimes/{id}` | Supprimer un régime |

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 21 | Langage principal |
| JavaFX | 21 | Interface graphique |
| Spring Boot | 3.x | Framework backend |
| Maven | 3.8+ | Gestion des dépendances |
| AtlantaFX | 2.0.1 | Thème graphique |
| Gson | 2.10.1 | Sérialisation JSON |
| MySQL | 8.0+ | Base de données |

---

## Auteur

**quantin785** — Projet réalisé dans le cadre d'une formation BTS SIO SLAM.
