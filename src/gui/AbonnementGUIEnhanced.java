package gui;

import integration.AbonnementManagerSimple;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Interface graphique amelioree de gestion d'abonnement (Membre 4)
 */
public class AbonnementGUIEnhanced extends JFrame {

    private final AbonnementManagerSimple manager;
    
    // Composants graphiques
    private JLabel mainStatusLabel;
    private JLabel connectionStatusLabel;
    private JLabel authStatusLabel;
    private JPanel statusCardPanel;
    
    private JPasswordField pinField;
    private JButton loginButton;
    private JButton logoutButton;
    private JButton reconnectButton;
    
    private JButton verifyButton;
    private JButton activateButton;
    private JButton expireButton;
    private JButton refreshButton;
    private JButton resetButton;
    private JButton clearLogButton;
    private JButton exportLogButton;
    
    private JTextArea logArea;
    private JProgressBar operationProgress;
    private JLabel timestampLabel;
    private JLabel opsCountLabel;
    
    private boolean isAuthenticated = false;
    private int operationCount = 0;

    public AbonnementGUIEnhanced() {
        manager = new AbonnementManagerSimple();
        initUI();
        connectToSimulator();
        startClock();
    }

    private void initUI() {
        setTitle("Systeme de Gestion d'Abonnement - Java Card");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        // Look and feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorer
        }

        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 245));

        // ========== HEADER ==========
        JPanel headerPanel = createHeaderPanel();
        
        // ========== GAUCHE: Controles (avec scroll) ==========
        JPanel leftPanel = createLeftPanel();
        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // ========== DROITE: Log ==========
        JPanel rightPanel = createRightPanel();
        
        // ========== BAS: Status bar ==========
        JPanel statusBar = createStatusBar();
        
        // Assemblage avec scroll sur le panneau gauche
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightPanel);
        splitPane.setDividerLocation(550);
        splitPane.setOneTouchExpandable(true);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("GESTION D'ABONNEMENT JAVA CARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Systeme securise avec authentification PIN");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        return header;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(240, 240, 245));
        
        // Panel de connexion
        JPanel connectionPanel = createConnectionPanel();
        
        // Panel de statut
        JPanel statusPanel = createStatusPanel();
        
        // Panel d'actions
        JPanel actionsPanel = createActionsPanel();
        
        // Panel de statistiques
        JPanel statsPanel = createStatsPanel();
        
        // Assemblage vertical
        JPanel topSection = new JPanel(new GridLayout(2, 1, 10, 10));
        topSection.setOpaque(false);
        topSection.add(connectionPanel);
        topSection.add(statusPanel);
        
        leftPanel.add(topSection, BorderLayout.NORTH);
        leftPanel.add(actionsPanel, BorderLayout.CENTER);
        leftPanel.add(statsPanel, BorderLayout.SOUTH);
        
        return leftPanel;
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(createStyledBorder("AUTHENTIFICATION SECURISEE"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(500, 280));
        
        // Status de connexion avec icones
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        connectionStatusLabel = new JLabel("[ ] SIMULATEUR: DECONNECTE");
        connectionStatusLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        connectionStatusLabel.setForeground(Color.RED);
        statusPanel.add(connectionStatusLabel);
        
        authStatusLabel = new JLabel("[ ] AUTHENTIFICATION: NON AUTORISE");
        authStatusLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        authStatusLabel.setForeground(new Color(200, 0, 0));
        statusPanel.add(authStatusLabel);
        
        // Section PIN professionnelle avec validation
        JPanel pinSection = new JPanel(new BorderLayout(12, 12));
        pinSection.setOpaque(false);
        pinSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pinSection.setBackground(new Color(236, 240, 241));
        
        // Label et instructions
        JPanel pinLabelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        pinLabelPanel.setOpaque(false);
        JLabel pinLabel = new JLabel("CODE PIN D'ACCES");
        pinLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pinLabel.setForeground(new Color(52, 73, 94));
        JLabel pinInstruction = new JLabel("Entrez votre code PIN a 4 chiffres (PIN par defaut: 1234)");
        pinInstruction.setFont(new Font("Arial", Font.ITALIC, 11));
        pinInstruction.setForeground(new Color(127, 140, 141));
        pinLabelPanel.add(pinLabel);
        pinLabelPanel.add(pinInstruction);
        
        // Champ PIN ameliore - TRES VISIBLE
        JPanel pinInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        pinInputPanel.setOpaque(false);
        
        JLabel lockIcon = new JLabel("🔐");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        
        pinField = new JPasswordField(4);
        pinField.setFont(new Font("Consolas", Font.BOLD, 28));
        pinField.setHorizontalAlignment(JTextField.CENTER);
        pinField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 3),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        pinField.setPreferredSize(new Dimension(180, 60));
        pinField.setBackground(Color.WHITE);
        pinField.setForeground(Color.BLACK);
        pinField.setEchoChar('●');
        pinField.setCaretColor(new Color(52, 152, 219));
        
        // Validation du PIN (4 chiffres seulement)
        pinField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) || pinField.getPassword().length >= 4) {
                    evt.consume();
                }
            }
        });
        
        // Enter pour se connecter
        pinField.addActionListener(e -> login());
        
        pinInputPanel.add(lockIcon);
        pinInputPanel.add(pinField);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        buttonPanel.setOpaque(false);
        
        loginButton = createStyledButton("SE CONNECTER", new Color(46, 204, 113));
        loginButton.setPreferredSize(new Dimension(155, 45));
        loginButton.addActionListener(e -> login());
        
        logoutButton = createStyledButton("DECONNECTER", new Color(231, 76, 60));
        logoutButton.setPreferredSize(new Dimension(155, 45));
        logoutButton.setEnabled(false);
        logoutButton.addActionListener(e -> logout());
        
        reconnectButton = createStyledButton("RECONNECTER", new Color(52, 152, 219));
        reconnectButton.setPreferredSize(new Dimension(155, 45));
        reconnectButton.addActionListener(e -> reconnect());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(reconnectButton);
        
        // Assemblage
        JPanel pinTopSection = new JPanel(new BorderLayout(8, 12));
        pinTopSection.setOpaque(false);
        pinTopSection.add(pinLabelPanel, BorderLayout.NORTH);
        pinTopSection.add(pinInputPanel, BorderLayout.CENTER);
        
        pinSection.add(pinTopSection, BorderLayout.CENTER);
        pinSection.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(pinSection, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createStyledBorder("Statut de l'Abonnement"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(500, 130));
        
        // Carte de statut avec icone
        statusCardPanel = new JPanel(new BorderLayout(12, 12));
        statusCardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        statusCardPanel.setBackground(new Color(230, 230, 230));
        
        mainStatusLabel = new JLabel("NON VERIFIE", SwingConstants.CENTER);
        mainStatusLabel.setFont(new Font("Arial", Font.BOLD, 28));
        mainStatusLabel.setForeground(Color.BLACK);
        
        statusCardPanel.add(mainStatusLabel, BorderLayout.CENTER);
        
        // Bouton de verification
        verifyButton = createStyledButton("Verifier Validite", new Color(52, 152, 219));
        verifyButton.setFont(new Font("Arial", Font.BOLD, 13));
        verifyButton.setPreferredSize(new Dimension(150, 40));
        verifyButton.setEnabled(false);
        verifyButton.addActionListener(e -> verifyStatus());
        
        panel.add(statusCardPanel, BorderLayout.CENTER);
        panel.add(verifyButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createStyledBorder("Actions sur l'Abonnement"));
        panel.setBackground(Color.WHITE);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        activateButton = createActionButton("Activer", new Color(46, 204, 113), "Activer l'abonnement");
        activateButton.addActionListener(e -> activate());
        
        expireButton = createActionButton("Expirer", new Color(231, 76, 60), "Expirer l'abonnement");
        expireButton.addActionListener(e -> expire());
        
        refreshButton = createActionButton("Rafraichir", new Color(52, 152, 219), "Rafraichir le statut");
        refreshButton.addActionListener(e -> verifyStatus());
        
        resetButton = createActionButton("Reinitialiser", new Color(149, 165, 166), "Reinitialiser la session");
        resetButton.addActionListener(e -> reset());
        
        JButton aboutButton = createActionButton("A propos", new Color(155, 89, 182), "Information systeme");
        aboutButton.addActionListener(e -> showAbout());
        
        JButton helpButton = createActionButton("Aide", new Color(241, 196, 15), "Afficher l'aide");
        helpButton.addActionListener(e -> showHelp());
        
        buttonsPanel.add(activateButton);
        buttonsPanel.add(expireButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(aboutButton);
        buttonsPanel.add(helpButton);
        
        // Desactiver par defaut
        activateButton.setEnabled(false);
        expireButton.setEnabled(false);
        refreshButton.setEnabled(false);
        resetButton.setEnabled(false);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 5));
        panel.setBorder(createStyledBorder("Statistiques"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 80));
        
        opsCountLabel = new JLabel("<html><b>Operations:</b><br>0</html>", SwingConstants.CENTER);
        opsCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel pinLabel = new JLabel("<html><b>PIN:</b><br>****</html>", SwingConstants.CENTER);
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel modeLabel = new JLabel("<html><b>Mode:</b><br>Simulateur</html>", SwingConstants.CENTER);
        modeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(opsCountLabel);
        panel.add(pinLabel);
        panel.add(modeLabel);
        
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createStyledBorder("Journal d'Evenements"));
        panel.setBackground(Color.WHITE);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Boutons de controle du log
        JPanel logControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logControlPanel.setOpaque(false);
        
        clearLogButton = createSmallButton("Effacer", new Color(231, 76, 60));
        clearLogButton.addActionListener(e -> clearLog());
        
        exportLogButton = createSmallButton("Exporter", new Color(52, 152, 219));
        exportLogButton.addActionListener(e -> exportLog());
        
        logControlPanel.add(clearLogButton);
        logControlPanel.add(exportLogButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(logControlPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        timestampLabel = new JLabel("Pret");
        timestampLabel.setForeground(Color.WHITE);
        timestampLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        operationProgress = new JProgressBar();
        operationProgress.setPreferredSize(new Dimension(200, 15));
        operationProgress.setVisible(false);
        
        statusBar.add(timestampLabel, BorderLayout.WEST);
        statusBar.add(operationProgress, BorderLayout.EAST);
        
        return statusBar;
    }

    private TitledBorder createStyledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            title
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 12));
        border.setTitleColor(new Color(52, 73, 94));
        return border;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JButton createActionButton(String text, Color color, String tooltip) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(220, 70));
        
        // Hover effect with shadow
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.darker(), 3),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                    ));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.darker(), 2),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });
        
        return button;
    }

    private JButton createSmallButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 25));
        return button;
    }

    // ========== ACTIONS ==========

    private void connectToSimulator() {
        log("[SYSTEM] ======================================");
        log("[SYSTEM] INITIALISATION DU SYSTEME...");
        log("[SYSTEM] Java Card Simulator v3.0.5");
        showProgress(true);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    Thread.sleep(1000); // Simule l'initialisation
                    return manager.connect();
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        log("[SYSTEM] *** CONNEXION REUSSIE ***");
                        log("[SYSTEM] Simulateur Java Card operationnel");
                        log("[SYSTEM] En attente d'authentification...");
                        log("[SYSTEM] ======================================");
                        
                        connectionStatusLabel.setText("[✓] SIMULATEUR: CONNECTE");
                        connectionStatusLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        log("[ERREUR] *** ECHEC DE CONNEXION ***");
                        log("[ERREUR] Impossible de se connecter au simulateur");
                        log("[SYSTEM] ======================================");
                        
                        JOptionPane.showMessageDialog(AbonnementGUIEnhanced.this,
                            "ERREUR CRITIQUE!\n\n" +
                            "Impossible de se connecter au simulateur.\n" +
                            "Veuillez redemarrer l'application.",
                            "ERREUR DE CONNEXION", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    log("[ERREUR] Exception: " + e.getMessage());
                }
                showProgress(false);
            }
        };
        worker.execute();
    }

    private void login() {
        String pin = new String(pinField.getPassword());
        
        // Validation stricte du PIN
        if (pin.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "ERREUR: Le code PIN est requis!\n\n" +
                "Veuillez entrer un code PIN a 4 chiffres.",
                "PIN REQUIS", JOptionPane.WARNING_MESSAGE);
            pinField.requestFocus();
            return;
        }
        
        if (pin.length() != 4) {
            JOptionPane.showMessageDialog(this,
                "ERREUR: Le PIN doit contenir exactement 4 chiffres!\n\n" +
                "PIN actuel: " + pin.length() + " chiffre(s)\n" +
                "Attendu: 4 chiffres",
                "FORMAT PIN INVALIDE", JOptionPane.ERROR_MESSAGE);
            pinField.setText("");
            pinField.requestFocus();
            return;
        }
        
        // Verification que ce sont des chiffres
        if (!pin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                "ERREUR: Le PIN doit contenir uniquement des chiffres!\n\n" +
                "Caracteres autorises: 0-9",
                "FORMAT PIN INVALIDE", JOptionPane.ERROR_MESSAGE);
            pinField.setText("");
            pinField.requestFocus();
            return;
        }
        
        log("[AUTH] ======================================");
        log("[AUTH] TENTATIVE D'AUTHENTIFICATION");
        log("[AUTH] PIN saisi: " + pin.charAt(0) + "***");
        log("[AUTH] Longueur: " + pin.length() + " caracteres");
        log("[AUTH] ======================================");
        showProgress(true);
        loginButton.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    Thread.sleep(800); // Simule verification securisee
                    return manager.authenticate(pin);
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        isAuthenticated = true;
                        log("[AUTH] *** AUTHENTIFICATION REUSSIE ***");
                        log("[AUTH] PIN accepte: " + pin);
                        log("[AUTH] Acces autorise au systeme");
                        
                        connectionStatusLabel.setText("[✓] SIMULATEUR: CONNECTE");
                        connectionStatusLabel.setForeground(new Color(46, 204, 113));
                        
                        authStatusLabel.setText("[✓] AUTHENTIFICATION: AUTORISEE");
                        authStatusLabel.setForeground(new Color(46, 204, 113));
                        
                        // Vider le champ PIN pour la securite
                        pinField.setText("");
                        
                        // Activer les boutons
                        enableAuthenticatedMode(true);
                        
                        // Message de succes
                        JOptionPane.showMessageDialog(AbonnementGUIEnhanced.this,
                            "ACCES AUTORISE!\n\n" +
                            "Authentification reussie avec le PIN: " + pin + "\n" +
                            "Vous avez maintenant acces a toutes les fonctionnalites.",
                            "AUTHENTIFICATION REUSSIE", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Verifier automatiquement
                        verifyStatus();
                    } else {
                        log("[AUTH] *** ECHEC D'AUTHENTIFICATION ***");
                        log("[AUTH] PIN refuse: " + pin);
                        log("[AUTH] Acces refuse!");
                        
                        pinField.setText("");
                        
                        JOptionPane.showMessageDialog(AbonnementGUIEnhanced.this,
                            "ACCES REFUSE!\n\n" +
                            "Le code PIN \"" + pin + "\" est INCORRECT.\n\n" +
                            "PIN par defaut du systeme: 1234\n" +
                            "Veuillez reessayer.",
                            "ERREUR D'AUTHENTIFICATION", JOptionPane.ERROR_MESSAGE);
                        
                        pinField.requestFocus();
                    }
                } catch (Exception e) {
                    log("[ERREUR] Exception: " + e.getMessage());
                }
                showProgress(false);
                loginButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void logout() {
        log("[AUTH] ======================================");
        log("[AUTH] DECONNEXION EN COURS...");
        isAuthenticated = false;
        
        connectionStatusLabel.setText("[ ] SIMULATEUR: DECONNECTE");
        connectionStatusLabel.setForeground(Color.RED);
        
        authStatusLabel.setText("[ ] AUTHENTIFICATION: NON AUTORISE");
        authStatusLabel.setForeground(new Color(200, 0, 0));
        
        enableAuthenticatedMode(false);
        updateStatusDisplay("NON VERIFIE", Color.LIGHT_GRAY);
        
        log("[AUTH] Utilisateur deconnecte");
        log("[AUTH] Acces systeme revoque");
        log("[AUTH] ======================================");
    }

    private void reconnect() {
        log("[SYSTEM] Reconnexion au simulateur...");
        manager.disconnect();
        connectToSimulator();
    }

    private void verifyStatus() {
        if (!isAuthenticated) {
            log("[ERREUR] Authentification requise");
            return;
        }

        log("[ACTION] Verification du statut...");
        operationCount++;
        showProgress(true);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    Thread.sleep(200);
                    return manager.getStatus();
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    String status = get();
                    if (status != null) {
                        log("[OK] Statut: " + status);
                        if ("ACTIF".equals(status)) {
                            updateStatusDisplay("ACTIF", new Color(144, 238, 144));
                        } else {
                            updateStatusDisplay("EXPIRE", new Color(255, 182, 193));
                        }
                    } else {
                        log("[ERREUR] Impossible de lire le statut");
                    }
                } catch (Exception e) {
                    log("[ERREUR] " + e.getMessage());
                }
                showProgress(false);
                updateStats();
            }
        };
        worker.execute();
    }

    private void activate() {
        if (!isAuthenticated) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Activer l'abonnement?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            log("[ACTION] Activation de l'abonnement...");
            operationCount++;
            showProgress(true);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        Thread.sleep(300);
                        return manager.activateSubscription();
                    } catch (Exception e) {
                        return false;
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            log("[OK] Abonnement active avec succes");
                            verifyStatus();
                        } else {
                            log("[ERREUR] Echec de l'activation");
                        }
                    } catch (Exception e) {
                        log("[ERREUR] " + e.getMessage());
                    }
                    showProgress(false);
                    updateStats();
                }
            };
            worker.execute();
        }
    }

    private void expire() {
        if (!isAuthenticated) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Expirer l'abonnement?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            log("[ACTION] Expiration de l'abonnement...");
            operationCount++;
            showProgress(true);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        Thread.sleep(300);
                        return manager.expireSubscription();
                    } catch (Exception e) {
                        return false;
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            log("[OK] Abonnement expire");
                            verifyStatus();
                        } else {
                            log("[ERREUR] Echec de l'expiration");
                        }
                    } catch (Exception e) {
                        log("[ERREUR] " + e.getMessage());
                    }
                    showProgress(false);
                    updateStats();
                }
            };
            worker.execute();
        }
    }

    private void reset() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reinitialiser la session?\n(Deconnexion + Reconnexion)",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            logout();
            reconnect();
            operationCount = 0;
            updateStats();
        }
    }

    private void showAbout() {
        String message = "SYSTEME DE GESTION D'ABONNEMENT\n" +
                        "Version 1.0 - Fevrier 2026\n\n" +
                        "Equipe de developpement:\n" +
                        "- Membre 1: Logique metier\n" +
                        "- Membre 2: Securite PIN\n" +
                        "- Membre 3: Communication APDU\n" +
                        "- Membre 4: Interface graphique\n" +
                        "- Membre 5: Integration globale\n\n" +
                        "Technologie: Java Card 3.0.5\n" +
                        "PIN par defaut: 1234";
        
        JOptionPane.showMessageDialog(this, message,
            "A propos", JOptionPane.INFORMATION_MESSAGE);
        log("[INFO] Fenetre 'A propos' affichee");
    }

    private void showHelp() {
        String message = "GUIDE D'UTILISATION:\n\n" +
                        "1. CONNEXION\n" +
                        "   - Entrez le PIN (defaut: 1234)\n" +
                        "   - Cliquez sur 'Se connecter'\n\n" +
                        "2. VERIFICATION\n" +
                        "   - Cliquez sur 'Verifier Validite'\n" +
                        "   - Le statut s'affiche (ACTIF/EXPIRE)\n\n" +
                        "3. ACTIONS\n" +
                        "   - Activer: Active l'abonnement\n" +
                        "   - Expirer: Desactive l'abonnement\n" +
                        "   - Rafraichir: Relire le statut\n\n" +
                        "4. AUTRES\n" +
                        "   - Reinitialiser: Reset complet\n" +
                        "   - Effacer: Vider le journal\n" +
                        "   - Exporter: Sauvegarder le journal";
        
        JOptionPane.showMessageDialog(this, message,
            "Aide", JOptionPane.INFORMATION_MESSAGE);
        log("[INFO] Fenetre 'Aide' affichee");
    }

    private void clearLog() {
        logArea.setText("");
        log("[SYSTEM] Journal efface");
    }

    private void exportLog() {
        String logContent = logArea.getText();
        if (logContent.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le journal est vide",
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("log_abonnement.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.writeString(
                    fileChooser.getSelectedFile().toPath(),
                    logContent
                );
                log("[OK] Journal exporte: " + fileChooser.getSelectedFile().getName());
                JOptionPane.showMessageDialog(this,
                    "Journal exporte avec succes!",
                    "Succes", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                log("[ERREUR] Impossible d'exporter: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'export",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ========== UTILITAIRES ==========

    private void enableAuthenticatedMode(boolean enabled) {
        verifyButton.setEnabled(enabled);
        activateButton.setEnabled(enabled);
        expireButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        resetButton.setEnabled(enabled); // Reset disponible quand authentifie
        loginButton.setEnabled(!enabled);
        logoutButton.setEnabled(enabled);
        pinField.setEnabled(!enabled);
    }

    private void updateStatusDisplay(String text, Color bgColor) {
        mainStatusLabel.setText(text);
        statusCardPanel.setBackground(bgColor);
    }

    private void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showProgress(boolean show) {
        operationProgress.setVisible(show);
        operationProgress.setIndeterminate(show);
    }

    private void updateStats() {
        if (opsCountLabel != null) {
            opsCountLabel.setText("<html><b>Operations:</b><br>" + operationCount + "</html>");
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            timestampLabel.setText("Systeme actif | " + time);
        });
        timer.start();
    }

    @Override
    public void dispose() {
        if (manager != null) {
            manager.disconnect();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AbonnementGUIEnhanced gui = new AbonnementGUIEnhanced();
            gui.setVisible(true);
        });
    }
}
