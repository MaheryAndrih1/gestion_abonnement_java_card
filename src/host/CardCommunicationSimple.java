package host;

import integration.APDUConstants;
import simulator.SimpleCardSimulator;

/**
 * Communication avec la Java Card (Membre 3) - Version Simplifiée
 * Gère l'envoi et la réception des APDU avec simulateur simple
 */
public class CardCommunicationSimple {

    private SimpleCardSimulator simulator;
    private boolean isConnected;

    /**
     * Constructeur
     */
    public CardCommunicationSimple() {
        this.isConnected = false;
    }

    /**
     * Connexion au simulateur
     */
    public boolean connect() {
        try {
            simulator = new SimpleCardSimulator();
            isConnected = true;
            System.out.println("✓ Connecté au simulateur");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Erreur connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérification du PIN
     */
    public boolean verifyPIN(byte[] pin) {
        if (!isConnected) {
            System.err.println("✗ Carte non connectée");
            return false;
        }

        try {
            boolean result = simulator.verifyPIN(pin);
            if (result) {
                System.out.println("✓ PIN correct");
            } else {
                System.err.println("✗ PIN incorrect (" + 
                    simulator.getRemainingTries() + " tentatives restantes)");
            }
            return result;
        } catch (Exception e) {
            System.err.println("✗ Erreur vérification PIN : " + e.getMessage());
            return false;
        }
    }

    /**
     * Consultation du statut de l'abonnement
     */
    public byte getStatus() {
        if (!isConnected) {
            System.err.println("✗ Carte non connectée");
            return -1;
        }

        try {
            return simulator.getStatus();
        } catch (SecurityException e) {
            System.err.println("✗ Authentification requise");
            return -1;
        } catch (Exception e) {
            System.err.println("✗ Erreur lecture statut : " + e.getMessage());
            return -1;
        }
    }

    /**
     * Mise à jour du statut de l'abonnement
     */
    public boolean updateStatus(byte newStatus) {
        if (!isConnected) {
            System.err.println("✗ Carte non connectée");
            return false;
        }

        try {
            boolean result = simulator.updateStatus(newStatus);
            if (result) {
                System.out.println("✓ Statut mis à jour");
            }
            return result;
        } catch (SecurityException e) {
            System.err.println("✗ Authentification requise");
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("✗ Valeur invalide");
            return false;
        } catch (Exception e) {
            System.err.println("✗ Erreur mise à jour : " + e.getMessage());
            return false;
        }
    }

    /**
     * Déconnexion
     */
    public void disconnect() {
        if (simulator != null) {
            simulator.logout();
        }
        isConnected = false;
        System.out.println("✓ Déconnecté");
    }

    /**
     * Vérifier si connecté
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Conversion statut vers String
     */
    public static String statusToString(byte status) {
        if (status == APDUConstants.ETAT_ACTIF) {
            return "ACTIF";
        } else if (status == APDUConstants.ETAT_EXPIRE) {
            return "EXPIRE";
        } else {
            return "INCONNU";
        }
    }
}
