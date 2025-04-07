import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Clients extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    private JLabel AESCHATAPP;
    private JTextField textField1;
    private JButton sendButton;
    public JTextArea textArea1;
    private JPanel MainPanel;
    private JButton Choosefile;
    private JLabel Username;
    private JTextField textField2;

    public Clients(Socket socket, String username) {
        try{
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // bruges til at modtage og læse beskeder.
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // bruges til at skrive og sende beskeder ud.
            this.username = username; // Brugerens brugernavn
            GUI();
        } catch (IOException e) {
            closeEverything(socket,in,out);
        }
    }

    public void GUI() {
        this.setContentPane(MainPanel);
        this.setTitle("AES Chatapp");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(750, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == 10) {
                    String message = textField1.getText();


                }
            }
        });
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
                        textField1.getText();
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
        GUI GUI = new GUI();
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine(); //Få fat i brugerens brugernavn
        Socket socket = new Socket("localhost", 1234); // linker serverporten med serveren
        // Localhost betyder at serveren ligger på samme enhed som de andre clienter, så man har ikke brug for en ip-adresse

        Clients clients = new Clients(socket,username);
        clients.receive();
        clients.send();
    }
}
