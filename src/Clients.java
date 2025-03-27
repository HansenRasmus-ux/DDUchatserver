import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Clients {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public Clients(Socket socket, String username) {
        try{
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket,in,out);
        }
    }

    public void send() {
        try {
            out.write(username);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                String msg = scanner.nextLine();
                out.write(username + ": " + msg);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverything(socket,in,out);
        }
    }

    public void receive() {
        new Thread(new Runnable() {

            String msgfrommember;

            @Override
            public void run() {
                while(socket.isConnected()) {
                    try {
                        msgfrommember = in.readLine();
                        System.out.println(msgfrommember);
                    } catch (IOException e) {
                        closeEverything(socket,in,out);
                    }
                }

            }
        }).start();

    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("indtast dit brugernavn: ");
        String username = scanner.nextLine(); //Få fat i brugerens brugernavn
        Socket socket = new Socket("localhost", 1234);

        Clients clients = new Clients(socket,username);
        clients.receive();
        clients.send();
    }
}
