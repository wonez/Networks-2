import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class BattleField extends JFrame implements ActionListener{

    private JButton[] fields;
    private JButton[] clickFields;

    private String msg;
    private String msg2;

    private JLabel textLabel;
    private String text;

    private Scanner listener;
    private PrintStream sender;

    private Socket endSocket;
    private Scanner endListener;
    private PrintStream endSender;

    public BattleField(JButton[] fields,  Scanner listener, PrintStream sender) {

        super("BattleShip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);

        this.fields = fields;
        this.listener = listener;
        this.sender = sender;
        text = "";

        clickFields = new JButton[20];

        JPanel bricks = new JPanel(new GridLayout(1,20));
        for(int i = 0 ;i < 20 ; i++){
            JButton btn = new JButton(i+"");
            btn.setFont(new Font("Arial",Font.PLAIN,-1));
            btn.setBackground(Color.CYAN);
            btn.addActionListener(this);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            clickFields[i] = btn;
            bricks.add(btn);
        }

        JPanel panel = new JPanel(new GridLayout(13,1));
        for(int i = 0; i<13;i++){
            if(i==6){
                panel.add(bricks);
            }else if(i == 2){
                textLabel = new JLabel(text);
                JPanel textPanel = new JPanel();
                textPanel.add(textLabel);
                panel.add(textPanel);
            }else{
                panel.add(new JPanel());
            }
        }

        try {
            endSocket = new Socket(Game.IP, 8888);
            endSender = new PrintStream(endSocket.getOutputStream());
            endListener = new Scanner(endSocket.getInputStream());
        }catch (IOException e){
            System.out.println("Socket already created");
        }

        PackageThread pt = new PackageThread(this) {
            @Override
            public void handleListening() throws Exception {
                msg = endListener.nextLine();
            }

            @Override
            public void handleConnection() throws Exception {
                if(msg.contains("Lost")){
                    parent.dispose();
                    End end = new End("Lost");
                    end.setLocationRelativeTo(null);
                    end.setVisible(true);
                    //end of the game
                }
            }
        };
        pt.createAndStartListenThread();
        pt.createAndStartIsAliveThread();

        handleTurns();
        this.add(panel,BorderLayout.CENTER);
    }

    public void handleTurns(){

        if(Game.TYPE % 2 == 1){
            text = "Opponents Turn";
            textLabel.setText(text);
            for(JButton jb : clickFields) {
                jb.setEnabled(false);
            }
            PackageThread pThread = new PackageThread(this) {
                @Override
                public void handleListening() throws Exception {
                    msg = listener.nextLine();
                }

                @Override
                public void handleConnection() throws Exception {

                    int pos = 0;

                    try {
                         pos = Integer.parseInt(msg);
                    }catch(Exception e ){
                        System.out.println(e.getMessage());
                    }

                    String hit = "false";

                    if(fields[pos].getText().contains("S")){
                        hit = "true";
                    }

                    sender.println(hit);

                    Game.TYPE++;
                    handleTurns();
                }
            };
            pThread.createAndStartListenThread();
            pThread.createAndStartIsAliveThread();
        } else {
            text = "Your Turn";
            textLabel.setText(text);
            for(JButton jb : clickFields) {
                if(jb.getBackground().equals(Color.CYAN))
                    jb.setEnabled(true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String pos = e.getActionCommand();
        sender.println(pos);

        PackageThread pt = new PackageThread(this) {
            @Override
            public void handleListening() throws Exception {
                msg2 = listener.nextLine();
            }

            @Override
            public void handleConnection() throws Exception {

                int pos = Integer.parseInt(e.getActionCommand());

                if(msg2.contains("true")){
                    clickFields[pos].setBackground(Color.RED);
                    Game.HITS++;
                    if(handleWin(parent))
                        return;

                } else {
                    clickFields[pos].setBackground(Color.BLUE);
                }
                //end of turn
                Game.TYPE++;
                handleTurns();
            }
        };
        pt.createAndStartListenThread();
        pt.createAndStartIsAliveThread();
    }

    private boolean handleWin(JFrame parent){

        if(Game.HITS == 10){
            parent.dispose();

            End end = new End("Won");
            end.setLocationRelativeTo(null);
            end.setVisible(true);

            String msg = "You Lost";
            endSender.println(msg);
            return true;
        }
        return false;
    }
}
