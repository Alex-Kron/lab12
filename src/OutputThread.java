import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class OutputThread extends Thread {
    private Socket client;
    private String username;

    public OutputThread(Socket client) throws IOException {
        this.client = client;
        System.out.print("What's your name? : ");
        username = new Scanner(System.in).nextLine();
        new DataOutputStream(client.getOutputStream()).writeUTF(username);
    }

    private boolean isClose (String s) throws IOException {
            if (s.equals("@quit")) {
                client.shutdownInput();
                client.shutdownOutput();
                client.close();
                return true;
            }
            return false;
    }

    public void run() {
        try {
            DataOutputStream toServer = new DataOutputStream(client.getOutputStream());
            InputThread input = new InputThread(client, username, this);
            input.start();
            String s = "";
                while (!isClose(s)) {
                    System.out.println("Your message:  ");
                    s = new Scanner(System.in).nextLine();
                    toServer.writeUTF(username + ": " + s);

                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}