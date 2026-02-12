# Guide d'utilisation - Gestion d'Abonnement Java Card

## 🚀 Démarrage rapide (Windows)

### Étape 1 : Compilation
Double-cliquez sur `compile.bat` ou exécutez dans le terminal :
```cmd
compile.bat
```

### Étape 2 : Lancer l'application
Double-cliquez sur `run_gui.bat` ou exécutez :
```cmd
run_gui.bat
```

### Étape 3 : Utilisation
1. **Connexion** : L'application se connecte automatiquement au simulateur
2. **Authentification** : Entrez le PIN (par défaut : `1234`) et cliquez sur "Se connecter"
3. **Vérification** : Cliquez sur "🔍 Vérifier Validité" pour voir l'état
4. **Actions** : Activez ou expirez l'abonnement avec les boutons

## 📋 Tests

Pour exécuter les tests :
```cmd
run_test.bat
```

## 📁 Structure du projet

```
gestion_abonnement/
│
├── src/
│   ├── applet/
│   │   └── AbonnementApplet.java          # Membre 1 & 2 - Applet Java Card
│   │
│   ├── simulator/
│   │   └── SimpleCardSimulator.java       # Simulateur simple
│   │
│   ├── host/
│   │   ├── CardCommunication.java         # Membre 3 - Version complète (jcardsim)
│   │   └── CardCommunicationSimple.java   # Membre 3 - Version simplifiée
│   │
│   ├── gui/
│   │   ├── AbonnementGUI.java             # Membre 4 - Interface (jcardsim)
│   │   └── AbonnementGUISimple.java       # Membre 4 - Interface simplifiée
│   │
│   ├── integration/
│   │   ├── APDUConstants.java             # Membre 5 - Constantes centralisées
│   │   ├── AbonnementManager.java         # Membre 5 - Gestionnaire (jcardsim)
│   │   └── AbonnementManagerSimple.java   # Membre 5 - Gestionnaire simplifié
│   │
│   └── test/
│       └── TestAbonnementComplet.java     # Tests
│
├── compile.bat                            # Script de compilation
├── run_gui.bat                            # Lancer l'interface
├── run_test.bat                           # Lancer les tests
└── README.md                              # Documentation

```

## 👥 Répartition du travail

### Membre 1 - Logique métier de l'applet
- ✅ Création de l'applet `AbonnementApplet.java`
- ✅ Stockage de l'état (ACTIF/EXPIRÉ)
- ✅ Commande APDU : consultation du statut (INS 0x30)
- ✅ Gestion mémoire EEPROM
- ✅ Codes SW (succès/erreur)

### Membre 2 - Sécurité
- ✅ Implémentation du PIN dans l'applet
- ✅ Vérification d'autorisation avant mise à jour
- ✅ Commande APDU sécurisée : vérification PIN (INS 0x20)
- ✅ Commande APDU sécurisée : mise à jour (INS 0x40)
- ✅ Gestion des tentatives invalides (3 essais)

### Membre 3 - Communication avec la carte
- ✅ Application Java : `CardCommunication.java`
- ✅ Envoi des APDU vers la carte
- ✅ Réception et décodage des réponses
- ✅ Gestion des erreurs (carte absente, accès refusé)
- ✅ Support simulateur et carte physique

### Membre 4 - Interface graphique
- ✅ Interface graphique Swing : `AbonnementGUISimple.java`
- ✅ Champ de saisie PIN
- ✅ Bouton "Vérifier validité"
- ✅ Affichage du statut (ACTIF/EXPIRÉ)
- ✅ Boutons d'action (Activer/Expirer)
- ✅ Journal d'événements

### Membre 5 - Intégration & cohérence (VOTRE RÔLE)
- ✅ Lien entre applet, application hôte et GUI
- ✅ Gestion du workflow complet
- ✅ Centralisation des constantes APDU : `APDUConstants.java`
- ✅ Gestionnaire d'abonnement : `AbonnementManagerSimple.java`
- ✅ Scripts de compilation et exécution
- ✅ Documentation complète

## 🔧 Commandes APDU

| Commande | CLA | INS | P1 | P2 | Lc | Data | Le | Description |
|----------|-----|-----|----|----|-------|------|-----|-------------|
| Vérifier PIN | B0 | 20 | 00 | 00 | 04 | PIN (4 bytes) | - | Authentification |
| Consulter statut | B0 | 30 | 00 | 00 | - | - | 01 | Obtenir l'état |
| Mettre à jour | B0 | 40 | 00 | 00 | 01 | État (01/00) | - | Modifier l'abonnement |

## 📊 États de l'abonnement

- `0x01` : **ACTIF** - L'abonnement est valide
- `0x00` : **EXPIRÉ** - L'abonnement n'est pas valide

## 🔐 Sécurité

- **PIN par défaut** : `1234`
- **Tentatives maximales** : 3
- **Protection** : Toutes les opérations nécessitent une authentification préalable

## ⚠️ Notes importantes

### Version Simplifiée (actuellement utilisée)
- Utilise `SimpleCardSimulator` : un simulateur Java simple
- Ne nécessite pas de bibliothèque externe
- Parfait pour la démonstration et les tests

### Version Complète (avec jcardsim)
Pour utiliser la version avec le vrai simulateur jcardsim :
1. Télécharger `jcardsim-3.0.5-SNAPSHOT.jar`
2. Placer dans un dossier `lib/`
3. Utiliser les classes sans suffixe "Simple"
4. Compiler avec : `javac -cp lib/jcardsim-3.0.5-SNAPSHOT.jar ...`

## 🐛 Dépannage

### Erreur de compilation
- Vérifiez que Java JDK est installé : `java -version`
- Vérifiez que `javac` est dans le PATH

### L'interface ne se lance pas
- Assurez-vous d'avoir compilé d'abord : `compile.bat`
- Vérifiez que le dossier `bin/` existe

### Problèmes d'authentification
- Le PIN par défaut est `1234`
- Après 3 tentatives échouées, le simulateur est bloqué (relancez l'application)

## 📞 Support

Pour toute question, référez-vous à :
- `README.md` : Vue d'ensemble du projet
- Code source : Commentaires détaillés dans chaque fichier
- Tests : `TestAbonnementComplet.java`
