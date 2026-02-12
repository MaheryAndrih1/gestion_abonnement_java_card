import applet.AbonnementApplet;


public class TestAbonnementMock {
    public static void main(String[] args) {
        try {
            AbonnementApplet applet = new AbonnementApplet();

            byte ACTIF = 1;
            byte EXPIRE = 0;

            System.out.println("=== Test logique AbonnementApplet ===");

            // État initial
            byte etat = applet.consulterStatutMock();
            System.out.println("État initial : " + (etat == ACTIF ? "ACTIF" : "EXPIRE"));

            // Mise à jour vers ACTIF
            applet.mettreAJourEtatMock(ACTIF);
            etat = applet.consulterStatutMock();
            System.out.println("Après mise à jour : " + (etat == ACTIF ? "ACTIF" : "EXPIRE"));

            // Mise à jour vers EXPIRE
            applet.mettreAJourEtatMock(EXPIRE);
            etat = applet.consulterStatutMock();
            System.out.println("Après mise à jour : " + (etat == ACTIF ? "ACTIF" : "EXPIRE"));

        } catch (Exception e) { // <-- générique
            e.printStackTrace();
        }
    }
}