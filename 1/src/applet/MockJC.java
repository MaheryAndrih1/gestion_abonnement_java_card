package applet;

// Simule juste les constantes et exceptions Java Card pour tester
public class MockJC {
    public static class ISOException extends RuntimeException {
        public static void throwIt(short sw) {
            throw new RuntimeException("ISOException: " + sw);
        }
    }

    public static class ISO7816 {
        public static final short SW_NO_ERROR = (short)0x9000;
        public static final short SW_WRONG_DATA = (short)0x6A80;
        public static final short SW_INS_NOT_SUPPORTED = (short)0x6D00;
        public static final short OFFSET_INS = 1;
        public static final short OFFSET_CDATA = 5;
    }

    public static class APDU {
        public byte[] buffer = new byte[256];

        public byte[] getBuffer() { return buffer; }
        public void setOutgoingAndSend(short off, short len) {
            System.out.println("APDU outgoing: " + buffer[off]);
        }
    }

    public static class Applet {
        public void register() {}
        public boolean selectingApplet() { return false; }
    }
}