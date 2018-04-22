import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class Screen extends JFrame implements ActionListener {

    private JButton host;
    private JButton join;
    private JTextField nickname;

    public Screen(){

        super("Battleship");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Caption cap = new Caption();

        host = new JButton("Host");
        join = new JButton("Join");
        nickname = new JTextField(25);

        host.addActionListener(this);
        join.addActionListener(this);

        JPanel buttons = new JPanel(new GridLayout(3, 4));

        for(int i=0; i<9; i++)
            buttons.add(new JPanel());
        buttons.add(host);
        buttons.add(join);
        buttons.add(new JPanel());

        add(buttons, BorderLayout.NORTH);
        add(cap, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel("Nickname: ");
        JLabel ip = null;
        try {
            ip = new JLabel("IP: " + InetAddress.getLocalHost().getHostAddress());
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
        }

        JPanel bottom = new JPanel(new GridLayout(2, 4));
        bottom.add(new JPanel());
        bottom.add(nameLabel);
        bottom.add(nickname);
        bottom.add(new JPanel());

        bottom.add(new JPanel());
        bottom.add(ip);
        bottom.add(new JPanel());
        bottom.add(new JPanel());

        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(nickname.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Nickname field cannot be empty" );
            return;
        }

        Game.ME = nickname.getText();

        if(e.getActionCommand().equals("Host")){

            Game.TYPE = 0;
            this.dispose();
            Host host = new Host();
            host.setLocationRelativeTo(null);
            host.setVisible(true);

        }else if(e.getActionCommand().equals("Join")){

            Game.TYPE = 1;
            String ip;

            boolean b = true;
            do {
                ip = JOptionPane.showInputDialog("Opponent IP");
                if(ip != null)
                    b = Pattern.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}", ip);
            }while(!b);

            try {
                Game.IP = InetAddress.getByName(ip);
            }catch (UnknownHostException ex){
                System.out.println(ex.getMessage());
            }

            this.dispose();
            Join join = new Join();
            join.setLocationRelativeTo(null);
            join.setVisible(true);
        }
    }
}

class Caption extends JPanel{

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        g.drawString("Battle", 90, 200);
        g.setColor(Color.RED);
        g.drawString("Ship", 270, 200);
    }
}
