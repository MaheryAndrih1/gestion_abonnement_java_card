package simulator;

/**
 * Simulateur simplifié de Java Card pour tests locaux
 * Alternative à jcardsim pour démonstration
 */
public class SimpleCardSimulator {

    private byte etatAbonnement;
    private byte[] pin;
    private boolean isAuthenticated;
    private int pinTries;

    private static final byte ACTIF = (byte) 0x01;
    private static final byte EXPIRE = (byte) 0x00;
    private static final int MAX_PIN_TRIES = 3;

    public SimpleCardSimulator() {
        etatAbonnement = EXPIRE;
        pin = new byte[] { '1', '2', '3', '4' };
        isAuthenticated = false;
        pinTries = MAX_PIN_TRIES;
    }

    /**
     * Vérifier le PIN
     */
    public boolean verifyPIN(byte[] inputPin) {
        if (pinTries <= 0) {
            System.err.println("Carte bloquée - trop de tentatives");
            return false;
        }

        if (inputPin.length != pin.length) {
            pinTries--;
            return false;
        }

        for (int i = 0; i < pin.length; i++) {
            if (inputPin[i] != pin[i]) {
                pinTries--;
                isAuthenticated = false;
                return false;
            }
        }

        isAuthenticated = true;
        pinTries = MAX_PIN_TRIES; // Réinitialiser le compteur
        return true;
    }

    /**
     * Consulter le statut
     */
    public byte getStatus() {
        if (!isAuthenticated) {
            throw new SecurityException("Authentification requise");
        }
        return etatAbonnement;
    }

    /**
     * Mettre à jour le statut
     */
    public boolean updateStatus(byte newStatus) {
        if (!isAuthenticated) {
            throw new SecurityException("Authentification requise");
        }

        if (newStatus != ACTIF && newStatus != EXPIRE) {
            throw new IllegalArgumentException("Valeur invalide");
        }

        etatAbonnement = newStatus;
        return true;
    }

    /**
     * Obtenir le nombre de tentatives restantes
     */
    public int getRemainingTries() {
        return pinTries;
    }

    /**
     * Vérifier si authentifié
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Réinitialiser l'authentification
     */
    public void logout() {
        isAuthenticated = false;
    }
}
