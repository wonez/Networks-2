import javax.swing.*;
import java.net.DatagramSocket;
import java.net.Socket;

abstract public class PackageThread {

    public JFrame parent;

    public Thread listenHost;
    public Thread isAliveHost;
    public Thread colors;

    public PackageThread(JFrame parent){
        this.parent = parent;
    }

    public void createAndStartListenThread(){
        listenHost = new Thread( () -> {
            try {
                handleListening();
            }catch (Exception e){
                System.out.println("Something went wrong");
            }
        });
        listenHost.start();
    }

    public void createAndStartIsAliveThread(){
        isAliveHost = new Thread( () -> {
            try{
                while(true) {
                    if(!listenHost.isAlive()) {
                        break;
                    }
                }
                handleConnection();

            } catch (Exception e){
                e.printStackTrace();
            }
        });
        isAliveHost.start();
    }

    public void createAndStartColorThread(){
        colors = new Thread( () -> {
            try{
                while(true) {
                    Waiting.colorCount++;
                    parent.repaint();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e){}
        });
        colors.start();
    }

    public abstract void handleConnection() throws Exception;
    public abstract void handleListening() throws Exception;
}
