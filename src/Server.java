import java.io.*;
import java.net.*;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // hold serveren kørende
    public void startServer() {
        try{
            while (!serverSocket.isClosed()) {

                //vent på at der er en client der deltager
                Socket socket = serverSocket.accept();
                System.out.println("Ny bruger er deltaget");

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();


            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void CloseServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //Bruges til at starte serveren
        ServerSocket serverSocket = new ServerSocket(1234); //producerer en serverport
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
