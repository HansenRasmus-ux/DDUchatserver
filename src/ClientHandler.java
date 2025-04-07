import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Used to send messages to clients
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Used to receive messages from clients

            // Read the username from the client
            this.username = this.in.readLine();
            clientHandlers.add(this); // Add the client to the list
            broadcastMessage("Server: " + username + " has joined the chat!"); // Notify others
        } catch (IOException e) {
            closeEverything(socket, in, out); // Handle exceptions
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = in.readLine(); // Read messages from the client
                if (message != null) {
                    broadcastMessage(username + ": " + message); // Broadcast the message with the username
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
                break;
            }
        }
    }

    public void broadcastMessage(String send) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Send message to all clients except the sender
                if (!clientHandler.username.equals(username)) {
                    clientHandler.out.write(send);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public void leave() {
        // Remove the client and notify others
        clientHandlers.remove(this);
        broadcastMessage("Server: " + username + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        leave();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
