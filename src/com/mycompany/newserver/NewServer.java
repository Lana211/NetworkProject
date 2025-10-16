package com.mycompany.newserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NewServer {
    private static List<NewClient> clients = new ArrayList<>();
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, String> reservations = new HashMap<>();
    private static Map<String, List<String>> userBookings = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started on port 9090");

        while (true) {
            System.out.println("Waiting for client connection...");
            Socket client = serverSocket.accept();
            System.out.println("Client connected");

            NewClient clientThread = new NewClient(client, clients, users, reservations, userBookings);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}

class NewClient implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private List<NewClient> clients;
    private Map<String, String> users;
    private Map<String, String> reservations;
    private Map<String, List<String>> userBookings;
    private String currentUser;

    public NewClient(Socket c, List<NewClient> clients, Map<String, String> users, 
                    Map<String, String> reservations, Map<String, List<String>> userBookings) throws IOException {
        this.client = c;
        this.clients = clients;
        this.users = users;
        this.reservations = reservations;
        this.userBookings = userBookings;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request == null) break;

                if (request.startsWith("REGISTER")) {
                    handleRegister(request);
                } else if (request.startsWith("LOGIN")) {
                    handleLogin(request);
                } else if (request.startsWith("GET_AVAILABLE_SLOTS")) {
                    handleGetAvailableSlots(request);
                } else if (request.startsWith("RESERVE")) {
                    handleReserve(request);
                }
            }
        } catch (IOException e) {
            System.err.println("IO exception in NewClient");
        } finally {
            try {
                in.close();
                out.close();
                client.close();
                clients.remove(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRegister(String request) {
        String[] parts = request.split(" ");
        if (parts.length >= 3) {
            String username = parts[1];
            String password = parts[2];
            if (!users.containsKey(username)) {
                users.put(username, password);
                userBookings.put(username, new ArrayList<>());
                currentUser = username;
                out.println("REGISTER_SUCCESS " + username);
            } else {
                out.println("REGISTER_FAILED Username already exists");
            }
        }
    }

    private void handleLogin(String request) {
        String[] parts = request.split(" ");
        if (parts.length >= 3) {
            String username = parts[1];
            String password = parts[2];
            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUser = username;
                out.println("LOGIN_SUCCESS " + username);
            } else {
                out.println("LOGIN_FAILED Invalid username or password");
            }
        }
    }

    private void handleGetAvailableSlots(String request) {
    // Fix: Handle dates with spaces properly
    String[] parts = request.split(" ", 3); // Split into max 3 parts
    if (parts.length >= 3) {
        String service = parts[1];
        String date = parts[2]; // The rest is the date
        
        // Available times for each service
        Map<String, String[]> serviceSlots = new HashMap<>();
        serviceSlots.put("Massage", new String[]{"9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"});
        serviceSlots.put("Facial", new String[]{"10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"});
        serviceSlots.put("Sauna", new String[]{"9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"});
        serviceSlots.put("Yoga", new String[]{"8:00 AM", "9:00 AM", "4:00 PM", "5:00 PM"});
        
        String[] allSlots = serviceSlots.getOrDefault(service, new String[]{});
        List<String> availableSlots = new ArrayList<>();
        
        // Filter out booked slots
        for (String time : allSlots) {
            String slotKey = service + "_" + date + "_" + time;
            
            // DEBUG: Check each slot
            boolean isBooked = reservations.containsKey(slotKey);
            System.out.println("Checking slot: " + slotKey + " -> Booked: " + isBooked);
            
            if (!isBooked) {
                availableSlots.add(time);
            }
        }
        
        // Debug output
        System.out.println("=== FINAL AVAILABLE SLOTS ===");
        System.out.println("Service: " + service + ", Date: " + date);
        System.out.println("Available: " + availableSlots);
        System.out.println("All Reservations: " + reservations);
        System.out.println("=============================");
        
        if (availableSlots.isEmpty()) {
            out.println("AVAILABLE_SLOTS ");
        } else {
            out.println("AVAILABLE_SLOTS " + String.join(",", availableSlots));
        }
    }
}

private void handleReserve(String request) {
    if (currentUser == null) {
        out.println("RESERVE_FAILED Please login first");
        return;
    }
    
    // Fix: Use proper parsing for dates with spaces
    String[] parts = request.split(" ", 4); // Split into max 4 parts
    if (parts.length >= 4) {
        String service = parts[1];
        // The date is everything after service until the last space before time
        String dateAndTime = parts[2] + " " + parts[3];
        String[] dateTimeParts = dateAndTime.split(" ");
        
        // Reconstruct date (all parts except the last 2 which are time)
        StringBuilder dateBuilder = new StringBuilder();
        for (int i = 0; i < dateTimeParts.length - 2; i++) {
            if (i > 0) dateBuilder.append(" ");
            dateBuilder.append(dateTimeParts[i]);
        }
        String date = dateBuilder.toString();
        
        // Time is the last 2 parts
        String time = dateTimeParts[dateTimeParts.length - 2] + " " + dateTimeParts[dateTimeParts.length - 1];
        
        String slotKey = service + "_" + date + "_" + time;
        
        // DEBUG: Before reservation
        System.out.println("=== BEFORE RESERVATION ===");
        System.out.println("Attempting to reserve: " + slotKey);
        System.out.println("Current reservations: " + reservations);
        
        if (!reservations.containsKey(slotKey)) {
            reservations.put(slotKey, currentUser);
            String booking = service + " on " + date + " at " + time;
            userBookings.get(currentUser).add(booking);
            out.println("RESERVE_CONFIRMED " + booking);
            
            // Debug output
            System.out.println("=== AFTER RESERVATION ===");
            System.out.println("Successfully reserved: " + slotKey + " for user: " + currentUser);
            System.out.println("Updated reservations: " + reservations);
            System.out.println("=========================");
        } else {
            out.println("RESERVE_FAILED " + time + " is already booked for " + service);
            System.out.println("RESERVATION FAILED - Already booked: " + slotKey);
        }
    } else {
        out.println("RESERVE_FAILED Invalid command format");
    }

}
}