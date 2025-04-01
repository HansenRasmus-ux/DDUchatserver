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
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // bruges til at modtage og læse beskeder.
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // bruges til at skrive og sende beskeder ud.
            this.username = username; // Brugerens brugernavn
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
                //display en brugers besked.
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
        //Lukker hele programmet // bruges til nogle af IOexceptions
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
        Socket socket = new Socket("localhost", 1234); // linker serverporten med serveren //Localhost betyder at serveren ligger på samme enhed som de andre clienter, så man har ikke brug for en ip-adresse

        Clients clients = new Clients(socket,username);
        clients.receive();
        clients.send();
    }
}
