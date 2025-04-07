import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Clients extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    private JTextField textField1; // Input field for messages
    private JButton sendButton; // Button to send messages
    public JTextArea textArea1; // Area to display messages

    public Clients(Socket socket, String username) {
        this.socket = socket;
        this.username = username; // User's username
        initializeGUI(); // Initialize the GUI components
        setupNetworking(); // Setup networking
    }

    private void initializeGUI() {
        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Set layout

        // Create components
        textArea1 = new JTextArea(20, 50);
        textArea1.setEditable(false); // Make the text area read-only
        JScrollPane scrollPane = new JScrollPane(textArea1); // Add scroll pane for text area

        textField1 = new JTextField(50); // Input field for messages
        sendButton = new JButton("Send"); // Button to send messages

        // Add components to the main panel
        mainPanel.add(scrollPane);
        mainPanel.add(textField1);
        mainPanel.add(sendButton);

        // Set the content pane
        setContentPane(mainPanel);
        setTitle("Chat App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Adjusts the frame to fit the components
        setLocationRelativeTo(null); // Center the frame
        setVisible(true); // Make the frame visible

        // Action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Key listener for the text field to send messages on Enter key press
        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private void setupNetworking() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Send the username as the first message
            out.write(username);
            out.newLine();
            out.flush();

            receive(); // Start receiving messages
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    private void sendMessage() {
        try {
            String message = textField1.getText();
            if (!message.isEmpty()) {
                out.write(message); // Send the message to the server
                out.newLine();
                out.flush();
                textField1.setText(""); // Clear the text field after sending
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void receive() {
        new Thread(new Runnable() {
            String msgFromMember;

            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        msgFromMember = in.readLine();
                        textArea1.append(msgFromMember + "\n"); // Append the message to the text area
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
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

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:"); // Get the username from a dialog
        if (username != null && !username.trim().isEmpty()) { // Check if username is valid
            try {
                Socket socket = new Socket("localhost", 1234); // Connect to the server
                new Clients(socket, username); // Create a new Clients instance
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}