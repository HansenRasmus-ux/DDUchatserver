import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.net.*;


public class Clients extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    private JLabel AESCHATAPP;
    private JTextField textField1;
    public JTextArea textArea1;
    private JPanel MainPanel;


    public Clients(Socket socket, String username) {

        this.socket = socket;
        this.username = username; // Brugerens brugernavn

        StartGUI();
        Network();



    }

    public void StartGUI() {
        JPanel MainPanel = new JPanel();
        MainPanel.setLayout(new BoxLayout(MainPanel, BoxLayout.Y_AXIS));

        textArea1 = new JTextArea(25,50);
        textArea1.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea1);

        textField1 = new JTextField(50);

        MainPanel.add(scrollPane);
        MainPanel.add(textField1);


        setContentPane(MainPanel);
        setTitle("AES Chatapp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);

        setLocationRelativeTo(null);
        setVisible(true);



        textField1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

    }

    private void Network() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // bruges til at modtage og læse beskeder.
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // bruges til at skrive og sende beskeder ud.

            out.write(username);
            out.newLine();
            out.flush();

            receive();

        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    private void sendMessage() {
        try {
            String message = textField1.getText();
            out.write(message);
            out.newLine();
            out.flush();
            textField1.setText("");
        }catch (IOException e) {
            closeEverything(socket, in, out);
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
                        textArea1.append(msgfrommember+"\n");
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }

            }
        }).start();

    }


    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
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
        String username = JOptionPane.showInputDialog("Indsæt dit brugernavn");
        if (username != null && !username.trim().isEmpty()) {
            try {
                Socket socket = new Socket("localhost", 50000);
                new Clients(socket, username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Brugernavn kan ikke være efterladt blankt", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
