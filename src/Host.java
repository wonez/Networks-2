import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Host extends JFrame{

    private ServerSocket serverSocket;
    private Socket socket;
    private Scanner listener;
    private PrintStream sender;

    private ServerSocket endServerSocket;
    public static Socket endSocket0;
    public static Scanner endListener0;
    public static PrintStream endSender0;

    public Host() {

        super("Host");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            serverSocket = new ServerSocket(Game.PORT);
            endServerSocket = new ServerSocket(8888);
        } catch (IOException e) {
            System.out.println("Socket already created");
        }


        Waiting waiting = new Waiting();
        add(waiting, BorderLayout.CENTER);

        PackageThread pThread = new PackageThread(this) {
            @Override
            public void handleListening() throws Exception {

                socket = serverSocket.accept();
                listener = new Scanner(socket.getInputStream());
                sender = new PrintStream(socket.getOutputStream());

                endSocket0 = endServerSocket.accept();
                endListener0 = new Scanner(endSocket0.getInputStream());
                endSender0 = new PrintStream(endSocket0.getOutputStream());
            }

            @Override
            public void handleConnection() throws Exception{

                String msg = listener.nextLine();

                if(msg.contains("I want to play")){

                    Game.OPPONENT = msg.substring(msg.indexOf('-') + 1).trim();

                    BattleGround bg = new BattleGround(listener, sender);

                    colors.interrupt();
                    parent.dispose();
                    bg.setLocationRelativeTo(null);
                    bg.setVisible(true);

                    sender.println(Game.ME);
                }
            }
        };

        pThread.createAndStartColorThread();
        pThread.createAndStartListenThread();
        pThread.createAndStartIsAliveThread();

    }
}

class Waiting extends JPanel{

    public static int colorCount = 0;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Waiting for opponent", 70, 220);

        if (colorCount % 2 == 0) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.RED);
        }
        g.drawString(".", 400, 220);
        g.drawString(".", 420, 220);

        if (colorCount % 2 == 0) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.drawString(".", 410, 220);
    }
}
