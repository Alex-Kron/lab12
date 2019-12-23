import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimerTask;

public class ClientTimer extends TimerTask {
    private InputOutputForServer inputOutputForServer;

    public ClientTimer(InputOutputForServer io) {
        inputOutputForServer = io;
    }

    @Override
    public void run() {
        try {
            Socket client = new Socket();
            for (Map.Entry<String, Socket> tmp : inputOutputForServer.getArrayOfSockets().entrySet()) {
                if (tmp.getKey() == inputOutputForServer.getUsername()) {
                    client = tmp.getValue();
                }
            }
            DataOutputStream toUser = new DataOutputStream(client.getOutputStream());
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat();
            toUser.writeUTF("Wake up! Time is " + f.format(date));
        } catch (EOFException e) {
          System.out.println("Client is disconnected");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
