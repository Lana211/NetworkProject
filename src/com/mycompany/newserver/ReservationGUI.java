package com.mycompany.newserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ReservationGUI extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String currentUser;
    
    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private BufferedImage backgroundImage;
    private BufferedImage reservationBackgroundImage;
    
    // Login/Register components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Reservation components
    private JComboBox<String> serviceCombo;
    private JComboBox<String> therapistCombo;
    private JComboBox<String> dateCombo;
    private JComboBox<String> timeCombo;
    private JTextArea confirmationArea;

    public ReservationGUI() {
        setTitle("Le Jené Spa - Reservation System");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load images
        loadImages();
        
        // Setup connection
        setupConnection();
        
        // Create UI
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createReservationPanel(), "RESERVATION");
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void loadImages() {
        try {
            // قراءة الصور من نفس المجلد مع الـ Java files
            backgroundImage = ImageIO.read(getClass().getResource("background.png"));
            reservationBackgroundImage = ImageIO.read(getClass().getResource("reservation_bg.png"));
            System.out.println("Images loaded successfully!");
        } catch (Exception e) {
            System.out.println("Could not load images: " + e.getMessage());
            e.printStackTrace();
            // البرنامج سيعمل بدون الصور
        }
    }
    
    private void setupConnection() {
        try {
            socket = new Socket("localhost", 9090);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Start listener thread
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        handleServerResponse(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to server. Please ensure the server is running.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new LoginBackgroundPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Login form container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(255, 255, 255, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));
        formPanel.setPreferredSize(new Dimension(490, 243));
        formPanel.setMaximumSize(new Dimension(490, 243));
        formPanel.setMinimumSize(new Dimension(490, 243));
        
        // Logo/Title
        JLabel titleLabel = new JLabel("Le Jené Spa", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("BEAUTY SPA", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(139, 69, 19));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(subtitleLabel);
        
        formPanel.add(Box.createVerticalStrut(20));
        
        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(userLabel);
        
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passLabel);
        
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton loginBtn = createStyledButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 30));
        loginBtn.addActionListener(e -> handleLogin());
        
        JButton registerBtn = createStyledButton("Register");
        registerBtn.setPreferredSize(new Dimension(100, 30));
        registerBtn.addActionListener(e -> handleRegister());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        formPanel.add(buttonPanel);
        
        // وضع الفورم على اليمين
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(75, 0, 0, 10);
        panel.add(formPanel, gbc);
        
        return panel;
    }
    
    private JPanel createReservationPanel() {
        JPanel panel = new ReservationBackgroundPanel();
        panel.setLayout(new BorderLayout());
        
        // Wrapper for header to center it
        JPanel headerWrapper = new JPanel(new GridBagLayout());
        headerWrapper.setOpaque(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setPreferredSize(new Dimension(1100, 100));
        headerPanel.setMaximumSize(new Dimension(1100, 100));
        
        JLabel headerLabel = new JLabel("Le Jené Spa - Book Your Treatment", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 32));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(180, 90, 30));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(150, 70, 20));
            }
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(180, 90, 30));
            }
        });
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 30));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        headerWrapper.add(headerPanel);
        panel.add(headerWrapper, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 250));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Service selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Select Service:"), gbc);
        
        gbc.gridx = 1;
        serviceCombo = new JComboBox<>(new String[]{"Massage", "Facial", "Sauna", "Yoga", "Hydrotherapy"});
        styleComboBox(serviceCombo);
        formPanel.add(serviceCombo, gbc);
        
        // Therapist selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Select Therapist:"), gbc);
        
        gbc.gridx = 1;
        therapistCombo = new JComboBox<>();
        styleComboBox(therapistCombo);
        formPanel.add(therapistCombo, gbc);
        
        // Update therapists when service changes
        serviceCombo.addActionListener(e -> updateTherapistList());
        
        // Date selection
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Select Date:"), gbc);
        
        gbc.gridx = 1;
        dateCombo = new JComboBox<>(new String[]{
            "January 15 2026", "January 16 2026", "January 17 2026",
            "January 18 2026", "January 19 2026", "January 20 2026",
            "January 21 2026", "January 22 2026", "January 23 2026",
            "January 24 2026"
        });
        styleComboBox(dateCombo);
        formPanel.add(dateCombo, gbc);
        
        // Check availability button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton checkBtn = createStyledButton("Check Available Times");
        checkBtn.addActionListener(e -> checkAvailability());
        formPanel.add(checkBtn, gbc);
        
        // Time selection
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(createLabel("Select Time:"), gbc);
        
        gbc.gridx = 1;
        timeCombo = new JComboBox<>();
        styleComboBox(timeCombo);
        timeCombo.setEnabled(false);
        formPanel.add(timeCombo, gbc);
        
        // Reserve button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton reserveBtn = createStyledButton("Make Reservation");
        reserveBtn.addActionListener(e -> makeReservation());
        formPanel.add(reserveBtn, gbc);
        
        // Confirmation area
        gbc.gridy = 6;
        confirmationArea = new JTextArea(5, 40);
        confirmationArea.setEditable(false);
        confirmationArea.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmationArea.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));
        JScrollPane scrollPane = new JScrollPane(confirmationArea);
        formPanel.add(scrollPane, gbc);
        
        GridBagConstraints mainGbc = new GridBagConstraints();
        contentPanel.add(formPanel, mainGbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Initialize therapists for first service
        updateTherapistList();
        
        return panel;
    }
    
    private void updateTherapistList() {
        String selectedService = (String) serviceCombo.getSelectedItem();
        therapistCombo.removeAllItems();
        
        String[] therapists = new String[0];
        
        switch (selectedService) {
            case "Massage":
                therapists = new String[]{"Emma Wilson", "James Brown", "Sophia Lee", "Michael Chen", "Olivia Davis"};
                break;
            case "Facial":
                therapists = new String[]{"Isabella Martinez", "William Taylor", "Mia Anderson", "Benjamin Thomas", "Charlotte Garcia"};
                break;
            case "Sauna":
                therapists = new String[]{"Lucas Rodriguez", "Amelia Hernandez", "Henry Lopez", "Evelyn Gonzalez", "Alexander Perez"};
                break;
            case "Yoga":
                therapists = new String[]{"Harper Scott", "Daniel King", "Ella Green", "Matthew Hall", "Sofia Adams"};
                break;
            case "Hydrotherapy":
                therapists = new String[]{"Jackson Baker", "Avery Rivera", "Sebastian Carter", "Scarlett Mitchell", "David Turner"};
                break;
        }
        
        for (String therapist : therapists) {
            therapistCombo.addItem(therapist);
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(139, 69, 19));
        return label;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(101, 50, 14));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(139, 69, 19));
            }
        });
        
        return button;
    }
    
    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(300, 35));
        combo.setBackground(Color.WHITE);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        
        out.println("LOGIN " + username + " " + password);
    }
    
    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        
        out.println("REGISTER " + username + " " + password);
    }
    
    private void checkAvailability() {
        String service = (String) serviceCombo.getSelectedItem();
        String therapist = ((String) therapistCombo.getSelectedItem()).replace(" ", "_");
        String date = ((String) dateCombo.getSelectedItem()).replace(" ", "_");
        
        out.println("GET_AVAILABLE_SLOTS " + service + " " + therapist + " " + date);
    }
    
    private void makeReservation() {
        if (timeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please check availability and select a time first");
            return;
        }
        
        String service = (String) serviceCombo.getSelectedItem();
        String therapist = ((String) therapistCombo.getSelectedItem()).replace(" ", "_");
        String date = ((String) dateCombo.getSelectedItem()).replace(" ", "_");
        String time = (String) timeCombo.getSelectedItem();
        
        out.println("RESERVE " + service + " " + therapist + " " + date + " " + time);
    }
    
    private void handleServerResponse(String response) {
        SwingUtilities.invokeLater(() -> {
            if (response.startsWith("LOGIN_SUCCESS") || response.startsWith("REGISTER_SUCCESS")) {
                String[] parts = response.split(" ");
                currentUser = parts[1];
                JOptionPane.showMessageDialog(this, "Welcome, " + currentUser + "!");
                cardLayout.show(mainPanel, "RESERVATION");
            } else if (response.startsWith("LOGIN_FAILED") || response.startsWith("REGISTER_FAILED")) {
                JOptionPane.showMessageDialog(this, response.substring(response.indexOf(" ") + 1));
            } else if (response.startsWith("AVAILABLE_SLOTS")) {
                timeCombo.removeAllItems();
                String slots = response.substring("AVAILABLE_SLOTS ".length());
                if (slots.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No available slots for this selection");
                    timeCombo.setEnabled(false);
                } else {
                    String[] times = slots.split(",");
                    for (String time : times) {
                        timeCombo.addItem(time);
                    }
                    timeCombo.setEnabled(true);
                }
            } else if (response.startsWith("RESERVE_CONFIRMED")) {
                String booking = response.substring("RESERVE_CONFIRMED ".length());
                confirmationArea.append("✓ Confirmed: " + booking + "\n");
                JOptionPane.showMessageDialog(this, "Reservation confirmed!\n" + booking);
            } else if (response.startsWith("RESERVE_FAILED")) {
                String message = response.substring("RESERVE_FAILED ".length());
                JOptionPane.showMessageDialog(this, message, "Reservation Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    // Custom panel with background image for Login page
    class LoginBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            if (backgroundImage != null) {
                int x = (getWidth() - 1000) / 2;
                int y = (getHeight() - 400) / 2;
                g2d.drawImage(backgroundImage, x, y, 1000, 400, null);
            } else {
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 245, 220),
                    0, getHeight(), new Color(222, 184, 135)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    // Custom panel with background image for Reservation page
    class ReservationBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            if (reservationBackgroundImage != null) {
                int imgWidth = 1100;
                int imgHeight = 700;
                int x = (getWidth() - imgWidth) / 2;
                int y = (getHeight() - imgHeight) / 2;
                g2d.drawImage(reservationBackgroundImage, x, y, imgWidth, imgHeight, null);
            } else {
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 245, 220),
                    0, getHeight(), new Color(222, 184, 135)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationGUI());
    }
}