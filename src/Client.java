import java.io.IOException;
import java.net.Socket;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        Socket client = new Socket(args[1],Integer.parseInt(args[0]));
        OutputThread out = new OutputThread(client);
        out.start();
    }
}
