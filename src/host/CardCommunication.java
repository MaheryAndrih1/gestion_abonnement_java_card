package host;

import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.AID;
import javax.smartcardio.*;
import integration.APDUConstants;

/**
 * Communication avec la Java Card (Membre 3)
 * Gère l'envoi et la réception des APDU
 */
public class CardCommunication {

    private CardChannel channel;
    private boolean useSimulator;
    private CardSimulator simulator;

    // AID de l'applet
    private static final byte[] APPLET_AID = { 
        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00 
    };

    /**
     * Constructeur - mode simulateur
     */
    public CardCommunication(boolean useSimulator) {
        this.useSimulator = useSimulator;
    }

    /**
     * Connexion à la carte (physique ou simulateur)
     */
    public boolean connect() {
        try {
            if (useSimulator) {
                return connectSimulator();
            } else {
                return connectPhysicalCard();
            }
        } catch (Exception e) {
            System.err.println("Erreur connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * Connexion au simulateur jcardsim
     */
    private boolean connectSimulator() {
        try {
            // Créer le simulateur
            simulator = new CardSimulator();
            
            // Installer l'applet
            AID appletAID = AIDUtil.create(APPLET_AID);
            simulator.installApplet(appletAID, applet.AbonnementApplet.class);
            
            // Sélectionner l'applet
            simulator.selectApplet(appletAID);
            
            System.out.println("✓ Connecté au simulateur");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Erreur simulateur : " + e.getMessage());
            return false;
        }
    }

    /**
     * Connexion à une carte physique
     */
    private boolean connectPhysicalCard() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();
            
            if (terminals.list().isEmpty()) {
                System.err.println("✗ Aucun lecteur de carte détecté");
                return false;
            }

            CardTerminal terminal = terminals.list().get(0);
            
            if (!terminal.isCardPresent()) {
                System.err.println("✗ Aucune carte présente");
                return false;
            }

            Card card = terminal.connect("T=0");
            channel = card.getBasicChannel();

            // Sélection de l'applet
            CommandAPDU selectAPDU = new CommandAPDU(
                0x00, 0xA4, 0x04, 0x00, APPLET_AID
            );
            ResponseAPDU response = channel.transmit(selectAPDU);

            if (response.getSW() == 0x9000) {
                System.out.println("✓ Connecté à la carte physique");
                return true;
            } else {
                System.err.println("✗ Échec sélection applet");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur carte physique : " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérification du PIN
     */
    public boolean verifyPIN(byte[] pin) {
        try {
            CommandAPDU apdu = new CommandAPDU(
                APDUConstants.CLA_ABONNEMENT,
                APDUConstants.INS_VERIFY_PIN,
                0x00, 0x00,
                pin
            );

            ResponseAPDU response = transmit(apdu);
            
            if (response.getSW() == 0x9000) {
                System.out.println("✓ PIN correct");
                return true;
            } else {
                System.err.println("✗ PIN incorrect");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur vérification PIN : " + e.getMessage());
            return false;
        }
    }

    /**
     * Consultation du statut de l'abonnement
     */
    public byte getStatus() {
        try {
            CommandAPDU apdu = new CommandAPDU(
                APDUConstants.CLA_ABONNEMENT,
                APDUConstants.INS_GET_STATUS,
                0x00, 0x00,
                0x01  // Le = 1 octet attendu
            );

            ResponseAPDU response = transmit(apdu);

            if (response.getSW() == 0x9000) {
                byte[] data = response.getData();
                if (data.length > 0) {
                    return data[0];
                }
            } else if (response.getSW() == 0x6982) {
                System.err.println("✗ Authentification requise");
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur lecture statut : " + e.getMessage());
        }
        return -1;
    }

    /**
     * Mise à jour du statut de l'abonnement
     */
    public boolean updateStatus(byte newStatus) {
        try {
            CommandAPDU apdu = new CommandAPDU(
                APDUConstants.CLA_ABONNEMENT,
                APDUConstants.INS_UPDATE_STATUS,
                0x00, 0x00,
                new byte[] { newStatus }
            );

            ResponseAPDU response = transmit(apdu);

            if (response.getSW() == 0x9000) {
                System.out.println("✓ Statut mis à jour");
                return true;
            } else if (response.getSW() == 0x6982) {
                System.err.println("✗ Authentification requise");
            } else {
                System.err.println("✗ Erreur mise à jour : " + 
                    String.format("0x%04X", response.getSW()));
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur mise à jour : " + e.getMessage());
        }
        return false;
    }

    /**
     * Transmission d'une APDU (simulateur ou carte physique)
     */
    private ResponseAPDU transmit(CommandAPDU command) throws CardException {
        if (useSimulator) {
            return simulator.transmitCommand(command);
        } else {
            return channel.transmit(command);
        }
    }

    /**
     * Déconnexion
     */
    public void disconnect() {
        try {
            if (!useSimulator && channel != null) {
                channel.getCard().disconnect(false);
            }
            System.out.println("✓ Déconnecté");
        } catch (Exception e) {
            System.err.println("Erreur déconnexion : " + e.getMessage());
        }
    }

    /**
     * Conversion statut vers String
     */
    public static String statusToString(byte status) {
        if (status == APDUConstants.ETAT_ACTIF) {
            return "ACTIF";
        } else if (status == APDUConstants.ETAT_EXPIRE) {
            return "EXPIRÉ";
        } else {
            return "INCONNU";
        }
    }
}
