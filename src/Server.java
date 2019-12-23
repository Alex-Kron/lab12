import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
        HashMap<String,Socket> arrayOfSockets = new HashMap<>();
        while(true)
        {
            try
            {
                Socket client = server.accept();
                System.out.println("Connection accepted.");
                InputOutputForServer inOut = new InputOutputForServer(client, arrayOfSockets);
                inOut.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
