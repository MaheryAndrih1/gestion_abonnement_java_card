package gui;

import integration.AbonnementManager;
import java.awt.*;
import javax.swing.*;

/**
 * Interface graphique de gestion d'abonnement (Membre 4)
 */
public class AbonnementGUI extends JFrame {

    private AbonnementManager manager;
    
    // Composants graphiques
    private JLabel statusLabel;
    private JButton verifyButton;
    private JButton activateButton;
    private JButton expireButton;
    private JPasswordField pinField;
    private JButton loginButton;
    private JTextArea logArea;
    
    private boolean isAuthenticated = false;

    public AbonnementGUI() {
        manager = new AbonnementManager();
        initUI();
        
        // Connexion automatique au simulateur
        log("Connexion au simulateur...");
        if (manager.connect()) {
            log("✓ Connecté avec succès");
        } else {
            log("✗ Échec de connexion");
            JOptionPane.showMessageDialog(this, 
                "Impossible de se connecter au simulateur", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        setTitle("Gestion d'Abonnement - Java Card");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ========== PANEL AUTHENTIFICATION ==========
        JPanel authPanel = new JPanel();
        authPanel.setBorder(BorderFactory.createTitledBorder("Authentification"));
        authPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        authPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField(10);
        pinField.setText("1234"); // PIN par défaut
        authPanel.add(pinField);
        
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(e -> login());
        authPanel.add(loginButton);

        // ========== PANEL STATUT ==========
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("Statut de l'Abonnement"));
        statusPanel.setLayout(new BorderLayout(10, 10));
        
        statusLabel = new JLabel("État: Non vérifié", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setForeground(Color.GRAY);
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        verifyButton = new JButton("🔍 Vérifier Validité");
        verifyButton.setFont(new Font("Arial", Font.PLAIN, 16));
        verifyButton.setEnabled(false);
        verifyButton.addActionListener(e -> verifyStatus());
        statusPanel.add(verifyButton, BorderLayout.SOUTH);

        // ========== PANEL ACTIONS ==========
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setLayout(new FlowLayout());
        
        activateButton = new JButton("✓ Activer l'abonnement");
        activateButton.setEnabled(false);
        activateButton.addActionListener(e -> activate());
        actionsPanel.add(activateButton);
        
        expireButton = new JButton("✗ Expirer l'abonnement");
        expireButton.setEnabled(false);
        expireButton.addActionListener(e -> expire());
        actionsPanel.add(expireButton);

        // ========== PANEL LOG ==========
        JPanel logPanel = new JPanel();
        logPanel.setBorder(BorderFactory.createTitledBorder("Journal"));
        logPanel.setLayout(new BorderLayout());
        
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        // ========== ASSEMBLAGE ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(authPanel, BorderLayout.NORTH);
        topPanel.add(statusPanel, BorderLayout.CENTER);
        topPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Authentification avec PIN
     */
    private void login() {
        String pin = new String(pinField.getPassword());
        log("Tentative d'authentification...");
        
        if (manager.authenticate(pin)) {
            isAuthenticated = true;
            log("✓ Authentification réussie");
            
            // Activer les boutons
            verifyButton.setEnabled(true);
            activateButton.setEnabled(true);
            expireButton.setEnabled(true);
            loginButton.setEnabled(false);
            pinField.setEnabled(false);
            
            // Vérifier automatiquement le statut
            verifyStatus();
        } else {
            log("✗ Échec d'authentification - PIN incorrect");
            JOptionPane.showMessageDialog(this, 
                "PIN incorrect", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Vérifier le statut de l'abonnement
     */
    private void verifyStatus() {
        if (!isAuthenticated) {
            log("✗ Authentification requise");
            return;
        }

        log("Consultation du statut...");
        String status = manager.getStatus();
        
        if (status != null) {
            log("✓ Statut: " + status);
            updateStatusDisplay(status);
        } else {
            log("✗ Erreur de lecture du statut");
        }
    }

    /**
     * Activer l'abonnement
     */
    private void activate() {
        if (!isAuthenticated) {
            log("✗ Authentification requise");
            return;
        }

        log("Activation de l'abonnement...");
        if (manager.activateSubscription()) {
            log("✓ Abonnement activé");
            verifyStatus(); // Rafraîchir l'affichage
        } else {
            log("✗ Échec activation");
        }
    }

    /**
     * Expirer l'abonnement
     */
    private void expire() {
        if (!isAuthenticated) {
            log("✗ Authentification requise");
            return;
        }

        log("Expiration de l'abonnement...");
        if (manager.expireSubscription()) {
            log("✓ Abonnement expiré");
            verifyStatus(); // Rafraîchir l'affichage
        } else {
            log("✗ Échec expiration");
        }
    }

    /**
     * Mettre à jour l'affichage du statut
     */
    private void updateStatusDisplay(String status) {
        if ("ACTIF".equals(status)) {
            statusLabel.setText("✓ Abonnement ACTIF");
            statusLabel.setForeground(new Color(0, 150, 0));
        } else if ("EXPIRÉ".equals(status)) {
            statusLabel.setText("✗ Abonnement EXPIRÉ");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("État: " + status);
            statusLabel.setForeground(Color.GRAY);
        }
    }

    /**
     * Ajouter un message au journal
     */
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Fermeture de l'application
     */
    @Override
    public void dispose() {
        if (manager != null) {
            manager.disconnect();
        }
        super.dispose();
    }

    /**
     * Point d'entrée
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AbonnementGUI gui = new AbonnementGUI();
            gui.setVisible(true);
        });
    }
}
