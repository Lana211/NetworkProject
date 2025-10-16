package com.mycompany.newserver;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Connected to server. Type your commands:");

        new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (true) {
                String command = keyboard.readLine();
                if (command.equals("quit")) break;
                out.println(command);
            }
        } finally {
            socket.close();
        }
    }
}