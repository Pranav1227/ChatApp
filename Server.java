import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // Decalring Components
    private JLabel heading = new JLabel("ServerArea");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();

    private Font font = new Font("Roboto", Font.BOLD, 20);

    // Constructor...
    public Server() {

        try {
            server = new ServerSocket(1227);
            System.out.println("Server is Ready to accept connection");
            System.out.println("waiting...");
            socket = server.accept();

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
                if (e.getKeyCode() == 10) {
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

        this.setTitle("Server Messanger");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Coding for Components
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("logo2.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // Frame Layouts
        this.setLayout(new BorderLayout());

        // Adding the components of the frame
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
                        System.out.println("Client has closed chat");
                        socket.close();
                        break;
                    }

                    // System.out.println("Client : " + msg);
                    messageArea.append("Client : " + msg + "\n");

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
                // e.printStackTrace();
                // System.out.println("Connection Closed");
            }

        };

        new Thread(r2).start();

    }

    public static void main(String[] args) {
        System.out.println("This is server..going to start server");
        new Server();
    }
}