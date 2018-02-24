
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Federico Doria
 */
public class ServerPizzeria {

    public static void main(String[] args) throws Exception {
        System.out.println("La pizzeria è aperta");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                // crea il thread e lo lancia
                new Capitalizer(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Capitalizer extends Thread {

        private Socket socket;
        private int clientNumber;
        public String input;
        public static String domanda;
        Pizzeria p = new Pizzeria();
        int dom = 0;

        public Capitalizer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            myLog("New connection with client #" + clientNumber + " at " + socket);
        }

        public void run() {
            try {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line; 
                // return them capitalized
                while (true) {
                    domanda = p.getDomanda(dom);
                    input = in.readLine();
                    if (input == null || input.equals(".")) {
                        out.println("Close connection for client #" + clientNumber + ".");
                        socket.close();
                        break;
                    }
                    out.println(input.toUpperCase());
                }
            } catch (IOException e) {
                myLog("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // log("Couldn't close a socket, what's going on?");
                }
                myLog("Connection with client# " + clientNumber + " closed");
            }
        }

        public void risposta() {
            System.out.println(input);
        }

        private void myLog(String message) {
            System.out.println(message);
        }
    }
}
