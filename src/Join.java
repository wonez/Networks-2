import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Join extends JFrame{

    private Socket socket;
    private Scanner listener;
    private PrintStream sender;

    private static Socket endSocket1;
    public static Scanner endListener1;
    public static PrintStream endSender1;

    private String msg;

    public Join(){

        super("Join");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Waiting waiting = new Waiting();
        add(waiting, BorderLayout.CENTER);

        try {

            endSocket1 = new Socket(Game.IP, 8888);
            endSender1 = new PrintStream(endSocket1.getOutputStream());
            endListener1 = new Scanner(endSocket1.getInputStream());

            socket = new Socket(Game.IP, Game.PORT);
            sender = new PrintStream(socket.getOutputStream());
            listener = new Scanner(socket.getInputStream());

        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

        sendGameRequest();

        PackageThread pThread = new PackageThread(this) {
            @Override
            public void handleListening() throws Exception {
                msg = listener.nextLine();
            }

            @Override
            public void handleConnection() throws Exception {
                System.out.println(msg);
                colors.interrupt();
                Game.OPPONENT = msg.trim();
                parent.dispose();
                BattleGround bg = new BattleGround(listener, sender);
                bg.setLocationRelativeTo(null);
                bg.setVisible(true);
            }
        };

        pThread.createAndStartColorThread();
        pThread.createAndStartListenThread();
        pThread.createAndStartIsAliveThread();

    }

    private void sendGameRequest(){
            String msg = "I want to play-" + Game.ME;
            sender.println(msg);
    }

}
