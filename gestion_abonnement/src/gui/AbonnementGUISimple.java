package gui;

import integration.AbonnementManagerSimple;
import java.awt.*;
import javax.swing.*;

/**
 * Interface graphique de gestion d'abonnement (Membre 4) - Version Simplifiée
 */
public class AbonnementGUISimple extends JFrame {

    private final AbonnementManagerSimple manager;
    
    // Composants graphiques
    private JLabel statusLabel;
    private JButton verifyButton;
    private JButton activateButton;
    private JButton expireButton;
    private JPasswordField pinField;
    private JButton loginButton;
    private JTextArea logArea;
    
    private boolean isAuthenticated = false;

    public AbonnementGUISimple() {
        manager = new AbonnementManagerSimple();
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
        setSize(650, 550);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ========== PANEL TITRE ==========
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("🎫 GESTION D'ABONNEMENT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel);

        // ========== PANEL AUTHENTIFICATION ==========
        JPanel authPanel = new JPanel();
        authPanel.setBorder(BorderFactory.createTitledBorder("🔐 Authentification"));
        authPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        authPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField(10);
        pinField.setText("1234"); // PIN par défaut
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        authPanel.add(pinField);
        
        loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> login());
        authPanel.add(loginButton);

        // ========== PANEL STATUT ==========
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("📊 Statut de l'Abonnement"));
        statusPanel.setLayout(new BorderLayout(10, 10));
        statusPanel.setPreferredSize(new Dimension(600, 120));
        
        statusLabel = new JLabel("Etat: Non verifie", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 28));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        verifyButton = new JButton("🔍 Vérifier Validité");
        verifyButton.setFont(new Font("Arial", Font.BOLD, 14));
        verifyButton.setEnabled(false);
        verifyButton.setBackground(new Color(52, 152, 219));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFocusPainted(false);
        verifyButton.addActionListener(e -> verifyStatus());
        statusPanel.add(verifyButton, BorderLayout.SOUTH);

        // ========== PANEL ACTIONS ==========
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBorder(BorderFactory.createTitledBorder("⚙️ Actions"));
        actionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        activateButton = new JButton("✓ Activer l'abonnement");
        activateButton.setFont(new Font("Arial", Font.BOLD, 12));
        activateButton.setEnabled(false);
        activateButton.setBackground(new Color(46, 204, 113));
        activateButton.setForeground(Color.WHITE);
        activateButton.setFocusPainted(false);
        activateButton.addActionListener(e -> activate());
        actionsPanel.add(activateButton);
        
        expireButton = new JButton("✗ Expirer l'abonnement");
        expireButton.setFont(new Font("Arial", Font.BOLD, 12));
        expireButton.setEnabled(false);
        expireButton.setBackground(new Color(231, 76, 60));
        expireButton.setForeground(Color.WHITE);
        expireButton.setFocusPainted(false);
        expireButton.addActionListener(e -> expire());
        actionsPanel.add(expireButton);

        // ========== PANEL LOG ==========
        JPanel logPanel = new JPanel();
        logPanel.setBorder(BorderFactory.createTitledBorder("📝 Journal d'événements"));
        logPanel.setLayout(new BorderLayout());
        
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        // ========== ASSEMBLAGE ==========
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(authPanel, BorderLayout.NORTH);
        topPanel.add(statusPanel, BorderLayout.CENTER);
        topPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Authentification avec PIN
     */
    private void login() {
        String pin = new String(pinField.getPassword());
        log("🔑 Tentative d'authentification...");
        
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
                "PIN incorrect!\nPIN par défaut: 1234", 
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

        log("📊 Consultation du statut...");
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

        log("⬆️ Activation de l'abonnement...");
        if (manager.activateSubscription()) {
            log("✓ Abonnement activé avec succès");
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

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Voulez-vous vraiment expirer l'abonnement?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            log("⬇️ Expiration de l'abonnement...");
            if (manager.expireSubscription()) {
                log("✓ Abonnement expiré");
                verifyStatus(); // Rafraîchir l'affichage
            } else {
                log("✗ Échec expiration");
            }
        }
    }

    /**
     * Mettre à jour l'affichage du statut
     */
    private void updateStatusDisplay(String status) {
        if ("ACTIF".equals(status)) {
            statusLabel.setText("ABONNEMENT ACTIF");
            statusLabel.setForeground(Color.BLACK);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(144, 238, 144)); // Light green
        } else if ("EXPIRE".equals(status)) {
            statusLabel.setText("ABONNEMENT EXPIRE");
            statusLabel.setForeground(Color.BLACK);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(255, 182, 193)); // Light pink
        } else {
            statusLabel.setText("Etat: " + status);
            statusLabel.setForeground(Color.BLACK);
            statusLabel.setOpaque(true);
            statusLabel.setBackground(Color.LIGHT_GRAY);
        }
    }

    /**
     * Ajouter un message au journal
     */
    private void log(String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
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
        // Définir le look and feel système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorer si échec
        }

        SwingUtilities.invokeLater(() -> {
            AbonnementGUISimple gui = new AbonnementGUISimple();
            gui.setVisible(true);
        });
    }
}
