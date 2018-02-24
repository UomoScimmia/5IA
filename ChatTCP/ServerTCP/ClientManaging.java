package servertcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jimmy.giana
 */
public class ClientManaging implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int idUtente;
    private String user;

    public ClientManaging(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    @Override
    public void run() {
        try {
            boolean continua = true;
            while (continua) {
                String op = in.readLine();
                if (op.equals("login")) {
                    String username = in.readLine();
                    String password = in.readLine();
                    if (SQLHelper.esistonoCredenziali(username, password) && !Server.isUtenteConnesso(SQLHelper.idUtente(username))) {
                        out.println("");
                        user = username;
                        idUtente = SQLHelper.idUtente(username);
                        gestisciVistaUtente();
                    } else {
                        out.println("Errore");
                    }
                } else if (op.equals("registrazione")) {
                    String username = in.readLine();
                    String password = in.readLine();
                    if (!SQLHelper.esisteUsername(username) && !SQLHelper.esistePassword(password)) {
                        SQLHelper.inserisciUtente(username, password);
                        SQLHelper.creaChatNuovoUtente(idUtente);
                        out.println("");
                        user = username;
                        idUtente = SQLHelper.idUtente(username);
                        gestisciVistaUtente();
                    } else {
                        out.println("Errore");
                    }
                } else {
                    continua = false;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientManaging.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gestisciVistaUtente() throws IOException {
        Server.addUtente(idUtente, socket);
        boolean continua = true;
        while (continua) {
            String op = in.readLine();
            if (op.equals("Carica")) {
                out.println(user);
                ArrayList<Chat> chat = SQLHelper.getChat(idUtente);
                out.println(chat.size());
                for (Chat c : chat) {
                    out.println(c.getNomeChat());
                    out.println(c.getIdChat());
                }
            } else if (op.equals("Chat")) {
                out.println("Chat");
                int idChat = Integer.parseInt(in.readLine());
                out.println(idChat);
                gestisciVistaChat(idChat);
            } else {
                out.println("");
                continua = false;
            }
        }
        Server.removeUtente(idUtente);
    }
    
    public void gestisciVistaChat(int idChat) throws IOException {
        boolean continua = true;
        while (continua) {
            String op = in.readLine();
            if (op.equals("Invio")) {
                String testo = in.readLine();
                testo = user + ":" + testo;
                int idDestinatario[] = SQLHelper.getDestinatario(idChat);
                if (idDestinatario[0] == idUtente) {
                    Server.inviaMessaggio(idDestinatario[1], testo, idChat);
                } else {
                    Server.inviaMessaggio(idDestinatario[0], testo, idChat);
                }
            } else {
                out.println("");
                continua = false;
            }
        }
    }
}
