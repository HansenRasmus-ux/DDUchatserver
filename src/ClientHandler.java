import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //Bruges til at sende beskeder ud ting andre.
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Bruges til at modtage og læse beskeder.
            this.username = this.in.readLine(); // Displayer personens brugernavn, før personens besked.
            clientHandlers.add(this); // Tilfører brugerene til gruppen.
            broadcastMessage("Server: " + username + "er deltaget i gruppen!");
        } catch (IOException e){
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run() {
        String message;

        while(socket.isConnected()){
            try {
                message = in.readLine();
                broadcastMessage(message);
            } catch (IOException e){
                closeEverything(socket, in, out);
                break;
            }
        }
    }
    public void broadcastMessage(String send){
        for(ClientHandler clientHandler : clientHandlers){

        }
    }
}
