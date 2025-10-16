package com.mycompany.newserver;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.HashMap;

class NewClient implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private LinkedList<NewClient> clients;
    private HashMap<String, String> reservations;
    private HashMap<String, String> users;
    private String username;

    public NewClient(Socket c, LinkedList<NewClient> clients, 
                    HashMap<String, String> reservations, 
                    HashMap<String, String> users) throws IOException {
        this.client = c;
        this.clients = clients;
        this.reservations = reservations;
        this.users = users;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            out.println("WELCOME Welcome to Spa & Wellness Reservation System!");

            while (true) {
                String request = in.readLine();
                if (request == null) break;

                if (request.startsWith("REGISTER")) {
                    String[] parts = request.split(" ");
                    if (parts.length >= 3) {
                        username = parts[1];
                        String password = parts[2];
                        if (!users.containsKey(username)) {
                            users.put(username, password);
                            out.println("REGISTER_SUCCESS " + username);
                        } else {
                            out.println("REGISTER_FAILED Username already exists");
                        }
                    }
                } else if (request.startsWith("CHECK_AVAILABILITY")) {
                    String date = request.substring(18);
                    StringBuilder availableSlots = new StringBuilder("AVAILABLE_SLOTS ");
                    availableSlots.append("9:00 AM,10:00 AM,11:00 AM,2:00 PM,3:00 PM,4:00 PM");
                    out.println(availableSlots.toString());
                } else if (request.startsWith("RESERVE")) {
                    String[] parts = request.split(" ", 3);
                    if (parts.length >= 3) {
                        String date = parts[1];
                        String time = parts[2];
                        String slotKey = date + "_" + time;
                        
                        if (!reservations.containsKey(slotKey)) {
                            reservations.put(slotKey, username);
                            out.println("RESERVE_CONFIRMED " + date + " at " + time);
                        } else {
                            out.println("RESERVE_FAILED " + time + " is already booked");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IO exception in NewClient");
        } finally {
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}