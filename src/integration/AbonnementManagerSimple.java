package integration;

import host.CardCommunicationSimple;

/**
 * Gestionnaire d'abonnement - Version Simplifiée (Membre 5)
 * Coordonne l'applet, la communication et l'interface
 */
public class AbonnementManagerSimple {

    private CardCommunicationSimple cardComm;
    private boolean isConnected = false;
    private boolean isAuthenticated = false;

    public AbonnementManagerSimple() {
        cardComm = new CardCommunicationSimple();
    }

    /**
     * Connexion à la carte
     */
    public boolean connect() {
        isConnected = cardComm.connect();
        return isConnected;
    }

    /**
     * Authentification avec PIN
     */
    public boolean authenticate(String pin) {
        if (!isConnected) {
            System.err.println("Carte non connectée");
            return false;
        }

        byte[] pinBytes = pin.getBytes();
        isAuthenticated = cardComm.verifyPIN(pinBytes);
        return isAuthenticated;
    }

    /**
     * Obtenir le statut de l'abonnement
     */
    public String getStatus() {
        if (!isConnected || !isAuthenticated) {
            System.err.println("Non authentifié");
            return null;
        }

        byte status = cardComm.getStatus();
        return CardCommunicationSimple.statusToString(status);
    }

    /**
     * Activer l'abonnement
     */
    public boolean activateSubscription() {
        if (!isConnected || !isAuthenticated) {
            System.err.println("Non authentifié");
            return false;
        }

        return cardComm.updateStatus(APDUConstants.ETAT_ACTIF);
    }

    /**
     * Expirer l'abonnement
     */
    public boolean expireSubscription() {
        if (!isConnected || !isAuthenticated) {
            System.err.println("Non authentifié");
            return false;
        }

        return cardComm.updateStatus(APDUConstants.ETAT_EXPIRE);
    }

    /**
     * Déconnexion
     */
    public void disconnect() {
        if (cardComm != null) {
            cardComm.disconnect();
            isConnected = false;
            isAuthenticated = false;
        }
    }

    /**
     * Vérifier si connecté
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Vérifier si authentifié
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Test en ligne de commande
     */
    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   Test Gestionnaire d'Abonnement (Simplifie)    ");
        System.out.println("====================================================\n");

        AbonnementManagerSimple manager = new AbonnementManagerSimple();

        // Connexion
        System.out.println("1. Connexion...");
        if (!manager.connect()) {
            System.err.println("Échec connexion");
            return;
        }

        // Authentification
        System.out.println("\n2. Authentification...");
        if (!manager.authenticate("1234")) {
            System.err.println("Échec authentification");
            return;
        }

        // Consultation
        System.out.println("\n3. Consultation du statut...");
        String status = manager.getStatus();
        System.out.println("   → Statut: " + status);

        // Activation
        System.out.println("\n4. Activation de l'abonnement...");
        manager.activateSubscription();
        status = manager.getStatus();
        System.out.println("   → Statut après activation: " + status);

        // Expiration
        System.out.println("\n5. Expiration de l'abonnement...");
        manager.expireSubscription();
        status = manager.getStatus();
        System.out.println("   → Statut après expiration: " + status);

        // Réactivation
        System.out.println("\n6. Réactivation...");
        manager.activateSubscription();
        status = manager.getStatus();
        System.out.println("   → Statut final: " + status);

        // Déconnexion
        System.out.println("\n7. Déconnexion...");
        manager.disconnect();

        System.out.println("\n====================================================");
        System.out.println("   Test termine avec succes                       ");
        System.out.println("====================================================");
    }
}
