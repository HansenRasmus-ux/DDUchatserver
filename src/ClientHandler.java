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
            this.username = this.in.readLine(); // Displayer personens brugernavn, før personens besked. // venter på at beskeden bliver sendt ud, før den reagere.
            clientHandlers.add(this); // Tilfører brugerne til gruppen.
            broadcastMessage("Server: " + username + " er deltaget i gruppen!"); // sender besked ud til andre brugere, at en ny bruger er deltaget og hvad de hedder.
        } catch (IOException e){
            closeEverything(socket, in, out); // exception, som skal laves til alle socket programmer.
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
            try{
                // hvis brugernavnet ikke er brugerens, så modtages beskeden til dem.
                if(!clientHandler.username.equals(username))
                {
                    clientHandler.out.write(send);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e){
                closeEverything(socket, in, out);
            }
        }
    }

    public void leave (){
        //Bruges til at fjerne og fortælle de andre brugere at en bruger har forladt
        clientHandlers.remove(this);
        broadcastMessage("Server: " + username + " forlod gruppen!");
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out){
        //Lukker hele applikationen.
        leave();
        try {
            if (in != null){
                in.close();
            }
            if (out != null){
                out.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
