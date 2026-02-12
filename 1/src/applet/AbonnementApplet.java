package applet;

import applet.MockJC.*;

public class AbonnementApplet extends Applet {

    private static final byte ACTIF = (byte)0x01;
    private static final byte EXPIRE = (byte)0x00;

    private byte etatAbonnement;

    public AbonnementApplet() {
        etatAbonnement = EXPIRE;
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new AbonnementApplet();
    }

    public void process(APDU apdu) {

        if (selectingApplet()) {
            return;
        }

        byte[] buffer = apdu.getBuffer();

        switch (buffer[ISO7816.OFFSET_INS]) {

            case (byte) 0x10:
                consulterStatut(apdu);
                break;

            case (byte) 0x20:
                mettreAJourEtat(apdu);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void consulterStatut(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        buffer[0] = etatAbonnement;

        apdu.setOutgoingAndSend((short)0, (short)1);
    }

    private void mettreAJourEtat(APDU apdu) {

        byte[] buffer = apdu.getBuffer();

        byte nouvelleValeur = buffer[ISO7816.OFFSET_CDATA];

        if (nouvelleValeur != ACTIF && nouvelleValeur != EXPIRE) {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }

        etatAbonnement = nouvelleValeur;

        ISOException.throwIt(ISO7816.SW_NO_ERROR);
    }

    // Méthodes pour Mock Test
    public byte consulterStatutMock() {
        return etatAbonnement;
    }

    public void mettreAJourEtatMock(byte nouvelleValeur) {
        if (nouvelleValeur != ACTIF && nouvelleValeur != EXPIRE) {
            ISOException.throwIt(ISO7816.SW_WRONG_DATA);
        }
        etatAbonnement = nouvelleValeur;
    }
}