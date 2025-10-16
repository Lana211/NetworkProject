package com.mycompany.newserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ReservationGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private JTextField usernameField, loginUsernameField;
    private JPasswordField passwordField, loginPasswordField;
    private JComboBox<String> serviceComboBox, dateComboBox, timeComboBox;
    
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String currentUser;

    public ReservationGUI() {
        setTitle("Spa & Wellness Reservation System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createWelcomePanel();
        createRegisterPanel();
        createLoginPanel();
        createReservationPanel();

        add(mainPanel);
        connectToServer();
        
        cardLayout.show(mainPanel, "WELCOME");
    }

    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Spa & Wellness Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JButton newUserBtn = new JButton("New user");
        JButton existingUserBtn = new JButton("Existing user");
        
        newUserBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        existingUserBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        buttonPanel.add(newUserBtn);
        buttonPanel.add(existingUserBtn);
        
        welcomePanel.add(titleLabel, BorderLayout.NORTH);
        welcomePanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(welcomePanel, "WELCOME");
    }

    private void createRegisterPanel() {
        JPanel registerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Spa & Wellness Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel newUserLabel = new JLabel("New user");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        
        JButton registerBtn = new JButton("Connect & Register");
        JButton backBtn = new JButton("Back");
        
        formPanel.add(newUserLabel);
        formPanel.add(new JLabel());
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(backBtn);
        formPanel.add(registerBtn);
        
        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        
        registerPanel.add(titleLabel, BorderLayout.NORTH);
        registerPanel.add(formPanel, BorderLayout.CENTER);
        
        mainPanel.add(registerPanel, "REGISTER");
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Spa & Wellness Reservation System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel existingUserLabel = new JLabel("Existing user");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        
        loginUsernameField = new JTextField();
        loginPasswordField = new JPasswordField();
        
        JButton loginBtn = new JButton("Connect & Login");
        JButton backBtn = new JButton("Back");
        
        formPanel.add(existingUserLabel);
        formPanel.add(new JLabel());
        formPanel.add(usernameLabel);
        formPanel.add(loginUsernameField);
        formPanel.add(passwordLabel);
        formPanel.add(loginPasswordField);
        formPanel.add(backBtn);
        formPanel.add(loginBtn);
        
        loginBtn.addActionListener(e -> loginUser());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        
        loginPanel.add(titleLabel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        
        mainPanel.add(loginPanel, "LOGIN");
    }

    private void createReservationPanel() {
        JPanel reservationPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Make new reservation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel serviceLabel = new JLabel("Select Service:");
        JLabel dateLabel = new JLabel("Select Date:");
        JLabel timeLabel = new JLabel("Available Times:");
        
        String[] services = {"Massage", "Facial", "Sauna", "Yoga"};
        String[] dates = {"Monday 10 March", "Tuesday 11 March", "Wednesday 12 March", "Thursday 13 March", "Friday 14 March"};
        
        serviceComboBox = new JComboBox<>(services);
        dateComboBox = new JComboBox<>(dates);
        timeComboBox = new JComboBox<>();
        
        JButton showTimesBtn = new JButton("Show available times");
        JButton reserveBtn = new JButton("Make reservation");
        JButton logoutBtn = new JButton("Logout");
        
        formPanel.add(serviceLabel);
        formPanel.add(serviceComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateComboBox);
        formPanel.add(timeLabel);
        formPanel.add(timeComboBox);
        formPanel.add(showTimesBtn);
        formPanel.add(reserveBtn);
        formPanel.add(new JLabel());
        formPanel.add(logoutBtn);
        
        showTimesBtn.addActionListener(e -> showAvailableSlots());
        reserveBtn.addActionListener(e -> makeReservation());
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "WELCOME");
        });
        
        reservationPanel.add(titleLabel, BorderLayout.NORTH);
        reservationPanel.add(formPanel, BorderLayout.CENTER);
        
        mainPanel.add(reservationPanel, "RESERVATION");
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 9090);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread messageThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String response;
                        while ((response = in.readLine()) != null) {
                            final String msg = response;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    handleServerMessage(msg);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            messageThread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("REGISTER_SUCCESS")) {
            currentUser = message.split(" ")[1];
            JOptionPane.showMessageDialog(this, "You are connected and registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "RESERVATION");
        } else if (message.startsWith("LOGIN_SUCCESS")) {
            currentUser = message.split(" ")[1];
            JOptionPane.showMessageDialog(this, "You are connected and logged in successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "RESERVATION");
        } else if (message.startsWith("RESERVE_CONFIRMED")) {
            JOptionPane.showMessageDialog(this, "Your booking has been confirmed!\n" + message.substring(18), "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
        } else if (message.startsWith("AVAILABLE_SLOTS")) {
            String slotsStr = message.substring(16);
            String[] slots = slotsStr.split(",");
            
            timeComboBox.removeAllItems();
            if (slots.length == 0 || slots[0].isEmpty()) {
                timeComboBox.addItem("No available slots");
                JOptionPane.showMessageDialog(this, "No available time slots for selected service and date", "No Slots", JOptionPane.WARNING_MESSAGE);
            } else {
                for (String slot : slots) {
                    timeComboBox.addItem(slot);
                }
                JOptionPane.showMessageDialog(this, "Available times loaded successfully", "Slots Available", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (message.startsWith("REGISTER_FAILED")) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + message.substring(16), "Error", JOptionPane.ERROR_MESSAGE);
        } else if (message.startsWith("LOGIN_FAILED")) {
            JOptionPane.showMessageDialog(this, "Login failed: " + message.substring(13), "Error", JOptionPane.ERROR_MESSAGE);
        } else if (message.startsWith("RESERVE_FAILED")) {
            JOptionPane.showMessageDialog(this, "Reservation failed: " + message.substring(15), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        out.println("REGISTER " + username + " " + password);
    }

    private void loginUser() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        out.println("LOGIN " + username + " " + password);
    }

    private void showAvailableSlots() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedService = (String) serviceComboBox.getSelectedItem();
        String selectedDate = (String) dateComboBox.getSelectedItem();
        
        out.println("GET_AVAILABLE_SLOTS " + selectedService + " " + selectedDate);
    }

    private void makeReservation() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (timeComboBox.getItemCount() == 0 || timeComboBox.getSelectedItem().equals("No available slots")) {
            JOptionPane.showMessageDialog(this, "No available time slots selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedService = (String) serviceComboBox.getSelectedItem();
        String selectedDate = (String) dateComboBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();
        
        out.println("RESERVE " + selectedService + " " + selectedDate + " " + selectedTime);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ReservationGUI().setVisible(true);
            }
        });
    }
}