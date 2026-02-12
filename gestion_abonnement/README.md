# Projet Gestion d'Abonnement sur Java Card

## 📋 Description
Système de gestion d'abonnement utilisant une Java Card avec interface graphique.

## 🏗️ Architecture

### 1. Applet Java Card (`src/applet/`)
- **AbonnementApplet.java** : Logique métier et sécurité
  - Gestion de l'état (ACTIF/EXPIRÉ)
  - Vérification PIN
  - Commandes APDU sécurisées

### 2. Application Hôte (`src/host/`)
- **CardCommunication.java** : Communication avec la carte
  - Envoi/réception APDU
  - Gestion des erreurs

### 3. Interface Graphique (`src/gui/`)
- **AbonnementGUI.java** : Interface utilisateur
  - Affichage du statut
  - Bouton "Vérifier validité"

### 4. Intégration (`src/integration/`)
- **APDUConstants.java** : Constantes APDU centralisées
- **AbonnementManager.java** : Workflow complet

### 5. Tests (`src/test/`)
- **TestAbonnementComplet.java** : Tests bout-en-bout

## 🚀 Compilation et Exécution

### Version Simplifiée (Recommandée - Sans bibliothèques externes)
```cmd
REM Compilation
compile.bat

REM Lancer l'interface graphique
run_gui.bat

REM Lancer les tests
run_test.bat
```

### Version Complète (Nécessite jcardsim)
```bash
# Télécharger jcardsim-3.0.5-SNAPSHOT.jar dans lib/
# Compilation
javac -cp lib/jcardsim-3.0.5-SNAPSHOT.jar -d bin src/applet/*.java src/host/CardCommunication.java src/gui/AbonnementGUI.java src/integration/AbonnementManager.java

# Exécution
java -cp "bin;lib/jcardsim-3.0.5-SNAPSHOT.jar" gui.AbonnementGUI
```

## 📌 Commandes APDU

| Commande | CLA | INS | Description |
|----------|-----|-----|-------------|
| Vérifier PIN | B0 | 20 | Authentification |
| Consulter statut | B0 | 30 | Obtenir l'état |
| Mettre à jour | B0 | 40 | Modifier l'abonnement |

## 👥 Répartition du travail
- **Membre 1** : Logique métier applet
- **Membre 2** : Sécurité (PIN)
- **Membre 3** : Communication carte
- **Membre 4** : Interface graphique
- **Membre 5** : Intégration globale
