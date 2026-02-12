package integration;

/**
 * Constantes APDU centralisées (Membre 5 - Intégration)
 * Utilisées par l'applet, l'application hôte et les tests
 */
public class APDUConstants {

    // ========== CLA ==========
    public static final byte CLA_ABONNEMENT = (byte) 0xB0;

    // ========== INSTRUCTIONS ==========
    public static final byte INS_VERIFY_PIN = (byte) 0x20;
    public static final byte INS_GET_STATUS = (byte) 0x30;
    public static final byte INS_UPDATE_STATUS = (byte) 0x40;

    // ========== ÉTATS ==========
    public static final byte ETAT_ACTIF = (byte) 0x01;
    public static final byte ETAT_EXPIRE = (byte) 0x00;

    // ========== STATUS WORDS ==========
    public static final short SW_NO_ERROR = (short) 0x9000;
    public static final short SW_PIN_REQUIRED = (short) 0x6982;
    public static final short SW_WRONG_DATA = (short) 0x6A80;
    public static final short SW_INS_NOT_SUPPORTED = (short) 0x6D00;

    // ========== PIN ==========
    public static final byte[] DEFAULT_PIN = { '1', '2', '3', '4' };
}
