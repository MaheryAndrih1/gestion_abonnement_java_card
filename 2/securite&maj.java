package abonnement;

import javacard.framework.*;

public class AbonnementApplet extends Applet {

    // CLA
    private static final byte CLA_ABONNEMENT = (byte) 0xB0;

    // Instructions
    private static final byte INS_VERIFY_PIN     = (byte) 0x20;
    private static final byte INS_GET_SUBSCRIPTION = (byte) 0x30;
    private static final byte INS_UPDATE_SUBSCRIPTION = (byte) 0x40;

    // PIN
    private static final byte PIN_TRY_LIMIT = (byte) 3;
    private static final byte PIN_SIZE = (byte) 4;

    private OwnerPIN pin;

    // Abonnement (nombre de jours)
    private short abonnement;

    private boolean isAuthenticated = false;

    protected AbonnementApplet() {
        pin = new OwnerPIN(PIN_TRY_LIMIT, PIN_SIZE);

        byte[] defaultPin = { '1', '2', '3', '4' };
        pin.update(defaultPin, (short) 0, PIN_SIZE);

        abonnement = 30; // 30 jours par défaut
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new AbonnementApplet();
    }

    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        if (selectingApplet()) return;

        if (buffer[ISO7816.OFFSET_CLA] != CLA_ABONNEMENT)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);

        switch (buffer[ISO7816.OFFSET_INS]) {

            case INS_VERIFY_PIN:
                verifyPin(apdu);
                break;

            case INS_GET_SUBSCRIPTION:
                getSubscription(apdu);
                break;

            case INS_UPDATE_SUBSCRIPTION:
                updateSubscription(apdu);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void verifyPin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short length = apdu.setIncomingAndReceive();

        if (pin.check(buffer, ISO7816.OFFSET_CDATA, (byte) length)) {
            isAuthenticated = true;
        } else {
            isAuthenticated = false;
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
    }

    private void getSubscription(APDU apdu) {
        if (!isAuthenticated)
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

        byte[] buffer = apdu.getBuffer();
        buffer[0] = (byte) (abonnement >> 8);
        buffer[1] = (byte) abonnement;

        apdu.setOutgoingAndSend((short) 0, (short) 2);
    }

    private void updateSubscription(APDU apdu) {
        if (!isAuthenticated)
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);

        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();

        short newSub = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        abonnement = newSub;
    }
}