import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // Declaring Components
    private JLabel heading = new JLabel("ClientArea");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();

    private Font font = new Font("Roboto", Font.BOLD, 20);

    // Constructor...
    public Client() {
        try {

            System.out.println("Sending Request To Server");
            socket = new Socket("127.0.0.1", 1227);
            System.out.println("Connection Done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
            // startWriting();

        } catch (Exception e) {

        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Key released" + e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    // System.out.println("You have pressed enter button");

                    String contentToSend = messageInput.getText();
                    messageArea.append("Me :" + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

        });
    }

    private void createGUI() {

        this.setTitle("Client Messanger");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Coding For Componets
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("logo2.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER); // To Write msg from center

        // Frame Layout
        this.setLayout(new BorderLayout());

        // Adding the compontes of the frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);

    }

    public void startReading() {

        Runnable r1 = () -> {
            System.out.println("reader started..");

            try {

                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Server has Closed");
                        JOptionPane.showMessageDialog(this, "Server Terminated Text");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }

                    // System.out.println("Server : " + msg);
                    messageArea.append("Server : " + msg + "\n");

                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection Closed");
            }

        };

        new Thread(r1).start();
    }

    public void startWriting() {

        Runnable r2 = () -> {
            System.out.println("Writer Started...");

            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                // System.out.println("Connection Closed");
            }

        };

        new Thread(r2).start();
    }

    public static void main(String[] args) {

        System.out.println("this is client ....");
        new Client();
    }
}
