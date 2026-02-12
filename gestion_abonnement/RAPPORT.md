# RAPPORT DE PROJET
## Gestion d'Abonnement sur Java Card

---

## 📋 TABLE DES MATIÈRES

1. [Introduction](#1-introduction)
2. [Architecture du système](#2-architecture-du-système)
3. [Répartition du travail](#3-répartition-du-travail)
4. [Spécifications techniques](#4-spécifications-techniques)
5. [Implémentation](#5-implémentation)
6. [Tests et validation](#6-tests-et-validation)
7. [Conclusion](#7-conclusion)

---

## 1. INTRODUCTION

### 1.1 Contexte
Ce projet implémente un système de gestion d'abonnement utilisant la technologie Java Card. Il permet de stocker et gérer l'état d'un abonnement (ACTIF/EXPIRÉ) de manière sécurisée sur une carte à puce.

### 1.2 Objectifs
- Développer une applet Java Card pour gérer l'état d'un abonnement
- Implémenter un système de sécurité basé sur PIN
- Créer une application hôte pour communiquer avec la carte
- Développer une interface graphique utilisateur
- Intégrer tous les composants dans un système cohérent

### 1.3 Technologies utilisées
- **Java Card 3.0.5** : Plateforme pour l'applet
- **Java SE** : Application hôte et interface graphique
- **Swing** : Framework pour l'interface graphique
- **APDU** : Protocole de communication

---

## 2. ARCHITECTURE DU SYSTÈME

### 2.1 Vue d'ensemble

```
┌─────────────────────────────────────────────────────────┐
│                  INTERFACE GRAPHIQUE                    │
│              (AbonnementGUISimple.java)                 │
│   [Authentification] [Affichage] [Actions]              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│               COUCHE D'INTÉGRATION                      │
│           (AbonnementManagerSimple.java)                │
│   Workflow: Connexion → Auth → Lecture → MAJ           │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│            COMMUNICATION AVEC LA CARTE                  │
│          (CardCommunicationSimple.java)                 │
│   Envoi APDU ⟷ Réception Réponse                      │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              SIMULATEUR / JAVA CARD                     │
│           (SimpleCardSimulator.java)                    │
│   État: ACTIF/EXPIRÉ | PIN | Sécurité                  │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Flux de communication

```
Utilisateur → GUI → Manager → Communication → Simulateur
                                                   ↓
            ← GUI ← Manager ← Communication ← Simulateur
```

---

## 3. RÉPARTITION DU TRAVAIL

### 👤 Membre 1 – Applet Java Card : Gestion de l'état

**Responsabilités :**
- Création de l'applet `AbonnementApplet.java`
- Stockage de l'état d'abonnement (ACTIF/EXPIRÉ) en EEPROM
- Implémentation de la commande APDU de consultation (INS 0x30)
- Gestion des codes SW (Status Words)
- Structure de base de l'applet

**Livrables :**
```java
- AbonnementApplet.java
  ├── États: ACTIF (0x01), EXPIRE (0x00)
  ├── Méthode: consulterStatut()
  ├── Gestion mémoire persistante
  └── Codes erreur appropriés
```

**Tests :**
- Vérification de l'état initial (EXPIRÉ)
- Lecture de l'état après modification
- Persistance des données

---

### 👤 Membre 2 – Applet Java Card : Sécurité & Mise à jour

**Responsabilités :**
- Implémentation du mécanisme de sécurité (PIN)
- Commande APDU de vérification PIN (INS 0x20)
- Commande APDU de mise à jour sécurisée (INS 0x40)
- Gestion des tentatives invalides (3 essais maximum)
- Protection des opérations sensibles

**Livrables :**
```java
- AbonnementApplet.java (partie sécurité)
  ├── PIN: 4 chiffres, 3 tentatives
  ├── Méthode: verifyPin()
  ├── Méthode: mettreAJourStatut()
  └── Vérification avant chaque opération
```

**Tests :**
- Authentification avec bon/mauvais PIN
- Blocage après 3 tentatives
- Rejet d'opération sans authentification

---

### 👤 Membre 3 – Application hôte : Communication carte

**Responsabilités :**
- Développement de `CardCommunicationSimple.java`
- Gestion de la connexion au simulateur
- Envoi des commandes APDU
- Réception et décodage des réponses
- Gestion des erreurs de communication

**Livrables :**
```java
- CardCommunicationSimple.java
  ├── connect() : Connexion au simulateur
  ├── verifyPIN() : Envoi commande PIN
  ├── getStatus() : Lecture de l'état
  ├── updateStatus() : Mise à jour
  └── Gestion des erreurs
```

**Tests :**
- Connexion/déconnexion
- Transmission APDU correcte
- Gestion erreurs (carte absente, etc.)

---

### 👤 Membre 4 – Interface graphique (GUI)

**Responsabilités :**
- Développement de `AbonnementGUISimple.java`
- Design de l'interface utilisateur
- Champ de saisie PIN
- Bouton "Vérifier validité"
- Affichage dynamique du statut (ACTIF/EXPIRÉ)
- Boutons d'action (Activer/Expirer)
- Journal d'événements

**Livrables :**
```java
- AbonnementGUISimple.java
  ├── Panel authentification
  ├── Panel affichage statut
  ├── Panel actions
  ├── Zone de log
  └── Gestion des événements
```

**Tests utilisateur :**
- Ergonomie de l'interface
- Clarté des messages
- Feedback visuel approprié

---

### 👤 Membre 5 – Intégration & Logique globale

**Responsabilités :**
- Coordination entre tous les composants
- Création de `APDUConstants.java` (constantes centralisées)
- Développement de `AbonnementManagerSimple.java`
- Gestion du workflow complet
- Scripts de compilation et exécution
- Documentation et tests globaux

**Livrables :**
```java
- APDUConstants.java : Constantes APDU
- AbonnementManagerSimple.java : Gestionnaire
- compile.bat : Script compilation
- run_gui.bat : Lancement GUI
- run_test.bat : Lancement tests
- Documentation complète
```

**Tests d'intégration :**
- Workflow complet fonctionnel
- Cohérence entre composants
- Gestion des erreurs bout-en-bout

---

## 4. SPÉCIFICATIONS TECHNIQUES

### 4.1 Commandes APDU

#### 4.1.1 Vérifier PIN (INS 0x20)
```
Commande:
  CLA: B0
  INS: 20
  P1:  00
  P2:  00
  Lc:  04
  Data: [PIN - 4 octets]

Réponse:
  SW: 9000 (succès) ou 6982 (échec)
```

#### 4.1.2 Consulter statut (INS 0x30)
```
Commande:
  CLA: B0
  INS: 30
  P1:  00
  P2:  00
  Le:  01

Réponse:
  Data: [01 = ACTIF, 00 = EXPIRÉ]
  SW: 9000 (succès) ou 6982 (non authentifié)
```

#### 4.1.3 Mettre à jour (INS 0x40)
```
Commande:
  CLA: B0
  INS: 40
  P1:  00
  P2:  00
  Lc:  01
  Data: [01 = ACTIF, 00 = EXPIRÉ]

Réponse:
  SW: 9000 (succès) ou 6982/6A80 (erreur)
```

### 4.2 Status Words (SW)

| Code | Signification |
|------|---------------|
| 0x9000 | Succès |
| 0x6982 | Sécurité non satisfaite (authentification requise) |
| 0x6A80 | Données incorrectes |
| 0x6D00 | Instruction non supportée |

### 4.3 États de l'abonnement

| Valeur | État | Description |
|--------|------|-------------|
| 0x01 | ACTIF | Abonnement valide |
| 0x00 | EXPIRÉ | Abonnement non valide |

---

## 5. IMPLÉMENTATION

### 5.1 Applet Java Card (Membres 1 & 2)

**Fichier:** `src/applet/AbonnementApplet.java`

**Caractéristiques principales:**
- Héritage de `javacard.framework.Applet`
- Stockage persistant en EEPROM
- Gestion du cycle de vie de l'applet
- Méthode `process()` pour traiter les APDU

**Code clé:**
```java
private static final byte ACTIF = (byte) 0x01;
private static final byte EXPIRE = (byte) 0x00;
private OwnerPIN pin;
private byte etatAbonnement;
```

### 5.2 Simulateur (Support)

**Fichier:** `src/simulator/SimpleCardSimulator.java`

**Fonctionnalités:**
- Simulation du comportement d'une Java Card
- Gestion du PIN avec compteur de tentatives
- Stockage de l'état d'abonnement
- Validation des commandes

### 5.3 Communication (Membre 3)

**Fichier:** `src/host/CardCommunicationSimple.java`

**Méthodes principales:**
```java
boolean connect()                  // Connexion
boolean verifyPIN(byte[] pin)      // Authentification
byte getStatus()                   // Consultation
boolean updateStatus(byte status)  // Mise à jour
void disconnect()                  // Déconnexion
```

### 5.4 Gestionnaire (Membre 5)

**Fichier:** `src/integration/AbonnementManagerSimple.java`

**Rôle:** Orchestration du workflow complet
```
connect() → authenticate() → getStatus() → updateStatus()
```

### 5.5 Interface graphique (Membre 4)

**Fichier:** `src/gui/AbonnementGUISimple.java`

**Composants:**
- Panel d'authentification (PIN)
- Panel de statut (affichage visuel)
- Panel d'actions (boutons)
- Zone de journalisation (log)

**Fonctionnalités:**
- Feedback visuel clair (couleurs)
- Messages d'erreur explicites
- Horodatage des événements

---

## 6. TESTS ET VALIDATION

### 6.1 Tests unitaires

**Membre 1 - Logique métier:**
- ✅ État initial = EXPIRÉ
- ✅ Lecture de l'état
- ✅ Persistance des données

**Membre 2 - Sécurité:**
- ✅ Vérification PIN correct
- ✅ Rejet PIN incorrect
- ✅ Blocage après 3 tentatives
- ✅ Protection des opérations

**Membre 3 - Communication:**
- ✅ Connexion au simulateur
- ✅ Envoi APDU correct
- ✅ Réception réponse
- ✅ Gestion erreurs

**Membre 4 - Interface:**
- ✅ Affichage correct du statut
- ✅ Interaction utilisateur fluide
- ✅ Messages clairs

### 6.2 Tests d'intégration (Membre 5)

**Scénario 1: Workflow complet**
```
1. Lancer l'application ✅
2. Connexion automatique ✅
3. Saisir PIN (1234) ✅
4. Authentification réussie ✅
5. Vérifier statut (EXPIRÉ) ✅
6. Activer abonnement ✅
7. Vérifier statut (ACTIF) ✅
```

**Scénario 2: Sécurité**
```
1. Connexion ✅
2. Mauvais PIN × 3 ✅
3. Blocage de la carte ✅
4. Tentative d'opération → Rejetée ✅
```

**Scénario 3: Gestion d'erreur**
```
1. Tentative lecture sans auth → Erreur ✅
2. Tentative MAJ sans auth → Erreur ✅
3. Valeur invalide → Rejetée ✅
```

### 6.3 Résultats

| Test | Statut | Remarques |
|------|--------|-----------|
| Compilation | ✅ | Sans erreur ni warning |
| Lancement GUI | ✅ | Interface s'affiche correctement |
| Authentification | ✅ | PIN validé/rejeté comme prévu |
| Lecture statut | ✅ | Valeur correcte retournée |
| Mise à jour | ✅ | Changement d'état persistant |
| Sécurité | ✅ | Protection effective |
| Ergonomie | ✅ | Interface claire et intuitive |

---

## 7. CONCLUSION

### 7.1 Objectifs atteints

✅ **Applet Java Card fonctionnelle**
- Gestion de l'état d'abonnement
- Sécurité par PIN
- Commandes APDU complètes

✅ **Application hôte robuste**
- Communication fiable
- Gestion des erreurs
- API claire

✅ **Interface utilisateur moderne**
- Design professionnel
- Feedback visuel
- Facilité d'utilisation

✅ **Intégration complète**
- Workflow cohérent
- Documentation exhaustive
- Scripts de déploiement

### 7.2 Points forts du projet

1. **Architecture modulaire** : Séparation claire des responsabilités
2. **Sécurité robuste** : Protection par PIN avec gestion des tentatives
3. **Ergonomie** : Interface intuitive et visuellement claire
4. **Documentation** : Guide complet et commentaires détaillés
5. **Testabilité** : Simulateur permettant tests sans carte physique

### 7.3 Améliorations possibles

1. **Durée d'abonnement** : Ajouter une date d'expiration
2. **Historique** : Enregistrer les opérations effectuées
3. **Cryptographie** : Chiffrement des données sensibles
4. **Multi-utilisateurs** : Gestion de plusieurs abonnements
5. **Déploiement** : Support carte physique complète

### 7.4 Compétences développées

- **Java Card** : Développement d'applets sécurisées
- **APDU** : Protocole de communication carte à puce
- **Sécurité** : Mécanismes d'authentification
- **Architecture** : Conception de systèmes distribués
- **Collaboration** : Travail d'équipe et intégration
- **Documentation** : Rédaction technique complète

---

## ANNEXES

### A. Structure complète du projet
```
gestion_abonnement/
├── src/
│   ├── applet/           # Membres 1 & 2
│   ├── simulator/        # Support
│   ├── host/             # Membre 3
│   ├── gui/              # Membre 4
│   ├── integration/      # Membre 5
│   └── test/             # Tous
├── bin/                  # Fichiers compilés
├── compile.bat           # Compilation
├── run_gui.bat           # Lancement GUI
├── run_test.bat          # Tests
├── README.md             # Vue d'ensemble
├── GUIDE_UTILISATION.md  # Guide utilisateur
└── RAPPORT.md            # Ce rapport
```

### B. Références
- Java Card Specification 3.0.5
- ISO 7816-4 (APDU)
- Java Swing Documentation
- Smart Card Development Best Practices

---

**Date:** Février 2026  
**Projet:** Gestion d'Abonnement Java Card  
**Équipe:** 5 membres
