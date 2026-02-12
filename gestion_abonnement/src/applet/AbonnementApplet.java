package applet;

import javacard.framework.*;
import javacard.security.*;

/**
 * Applet de gestion d'abonnement
 * Combine la logique métier (Membre 1) et la sécurité (Membre 2)
 */
public class AbonnementApplet extends Applet {

    // ========== CONSTANTES APDU (Membre 5) ==========
    private static final byte CLA_ABONNEMENT = (byte) 0xB0;
    
    // Instructions
    private static final byte INS_VERIFY_PIN = (byte) 0x20;
    private static final byte INS_GET_STATUS = (byte) 0x30;
    private static final byte INS_UPDATE_STATUS = (byte) 0x40;

    // ========== ÉTATS D'ABONNEMENT (Membre 1) ==========
    private static final byte ACTIF = (byte) 0x01;
    private static final byte EXPIRE = (byte) 0x00;

    // ========== SÉCURITÉ (Membre 2) ==========
    private static final byte PIN_TRY_LIMIT = (byte) 3;
    private static final byte PIN_SIZE = (byte) 4;
    
    private OwnerPIN pin;
    private byte etatAbonnement;

    /**
     * Constructeur : initialisation de l'applet
     */
    protected AbonnementApplet() {
        // Initialisation du PIN (Membre 2)
        pin = new OwnerPIN(PIN_TRY_LIMIT, PIN_SIZE);
        byte[] defaultPin = { '1', '2', '3', '4' };
        pin.update(defaultPin, (short) 0, PIN_SIZE);
        
        // État initial : EXPIRÉ (Membre 1)
        etatAbonnement = EXPIRE;
        
        register();
    }

    /**
     * Installation de l'applet
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new AbonnementApplet();
    }

    /**
     * Traitement des APDU (Membre 1 + 2)
     */
    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        // Sélection de l'applet
        if (selectingApplet()) {
            return;
        }

        // Vérification de la classe
        if (buffer[ISO7816.OFFSET_CLA] != CLA_ABONNEMENT) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        // Traitement selon l'instruction
        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_VERIFY_PIN:
                verifyPin(apdu);
                break;

            case INS_GET_STATUS:
                consulterStatut(apdu);
                break;

            case INS_UPDATE_STATUS:
                mettreAJourStatut(apdu);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    /**
     * Vérification du PIN (Membre 2 - Sécurité)
     */
    private void verifyPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();

        // Vérification du PIN
        if (pin.check(buffer, ISO7816.OFFSET_CDATA, (byte) lc)) {
            // PIN correct - succès
            return;
        } else {
            // PIN incorrect
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
    }

    /**
     * Consultation du statut (Membre 1 - Logique métier)
     */
    private void consulterStatut(APDU apdu) {
        // Vérification de l'authentification (Membre 2)
        if (!pin.isValidated()) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }

        byte[] buffer = apdu.getBuffer();
        buffer[0] = etatAbonnement;
        
        apdu.setOutgoingAndSend((short) 0, (short) 1);
    }

    /**
     * Mise à jour du statut (Membre 1 + Membre 2)
     */
    private void mettreAJourStatut(APDU apdu) {
        // Vérification de l'authentification (Membre 2 - Sécurité)
        if (!pin.isValidated()) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }

        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();
        
        byte nouvelleValeur = buffer[ISO7816.OFFSET_CDATA];

        // Validation de la valeur (Membre 1)
        if (nouvelleValeur != ACTIF && nouvelleValeur != EXPIRE) {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }

        // Mise à jour en EEPROM (Membre 1)
        etatAbonnement = nouvelleValeur;
    }
}
