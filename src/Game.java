import java.net.InetAddress;
import java.net.UnknownHostException;

public class Game{

    public static String ME = "";
    public static String OPPONENT = "";
    public static int TYPE = -1;
    public static int HITS = 0;
    public static InetAddress IP;
    public static int PORT = 8080;

    public static void main(String[] args){
        try {
            IP = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
        }
        Screen sc = new Screen();
        sc.setLocationRelativeTo(null);
        sc.setVisible(true);
    }

}
