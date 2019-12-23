import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class InputThread extends Thread
{
    private Socket client;
    private String username;
    private OutputThread thread;
    public InputThread(Socket client,String username, OutputThread t)
    {
        this.client = client;
        this.username = username;
        this.thread = t;
    }
    public void run() {
        try {
            synchronized (client) {
                while (!client.isClosed()) {
                    DataInputStream toClient = new DataInputStream(client.getInputStream());
                    String inString = toClient.readUTF();
                    System.out.print(inString + "\n");
                }
            }
        }
        catch (EOFException e) {
            System.out.println("Disconnected");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}