import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class InputOutputForServer extends Thread {
    private String username;
    private Socket client;
    private  HashMap<String,Socket> arrayOfSockets;

    public InputOutputForServer(Socket client, HashMap<String, Socket> arr) {
        this.client = client;
        this.arrayOfSockets = arr;
    }

    public String getUsername() {
        return username;
    }

    public Socket getClient() {
        return client;
    }

    public HashMap<String, Socket> getArrayOfSockets() {
        return arrayOfSockets;
    }

    public boolean command(String in) throws IOException {
        char[] mas = in.toCharArray();
        int i = 0;
        for(i = 0; mas[i] !='@'; i++);
        StringBuilder sb = new StringBuilder();
        for(;mas[i] !=' '; i++)
        {
            sb.append(mas[i]);
            if(i + 1 == in.length())
                break;
        }

        i++;
        System.out.println(sb.toString());

        if(sb.toString().equals("@send")) {
            sb.delete(0,sb.length());
            for(; mas[i] !=' '; i++) {
                sb.append(mas[i]);
                if(i + 1 == in.length())
                    break;
            }

            i++;
            if(i < in.length())
            {
                String receiver = sb.toString();
                sb.delete(0,sb.length());
                for(; i < in.length(); i++)
                {
                    sb.append(mas[i]);
                }
                String massage = sb.toString();
                for(i = 0; mas[i] != ':'; i++)
                    sb.append(mas[i]);
                for (Map.Entry<String, Socket> tmp : arrayOfSockets.entrySet())
                {
                    String key = tmp.getKey();
                    if(key.equals(receiver))
                    {
                        Socket curClient = tmp.getValue();
                        DataOutputStream toUser = new DataOutputStream(curClient.getOutputStream());
                        toUser.writeUTF("Message only for you from " + username + ": "+ massage);
                        return true;
                    }
                }
            }
        }

        if (sb.toString().equals("@timer")) {
            Timer timer = new Timer();
            String[] message = in.split("\\W");
            int hours = Integer.parseInt(message[4]);
            int min = Integer.parseInt(message[5]);
            GregorianCalendar c = new GregorianCalendar();
            if (c.get(Calendar.HOUR_OF_DAY) > hours || (c.get(Calendar.HOUR_OF_DAY) == hours && c.get(Calendar.MINUTE) > min)) {
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.HOUR_OF_DAY, hours - c.get(Calendar.HOUR_OF_DAY));
                c.add(Calendar.MINUTE, min - c.get(Calendar.MINUTE));
            } else {
                c.add(Calendar.HOUR_OF_DAY, hours - c.get(Calendar.HOUR_OF_DAY));
                c.add(Calendar.MINUTE, min - c.get(Calendar.MINUTE));
            }
            c.add(Calendar.SECOND, -c.get(Calendar.SECOND));
            Date date = new Date (c.getTimeInMillis());
            timer.schedule(new ClientTimer(this), date);
            System.out.println(username + " установил будильник на " + new SimpleDateFormat().format(date));
            return true;
        }
        return false;
    }

    public void run() {
        try {
            DataInputStream fromUser = new DataInputStream(client.getInputStream());
            this.username = fromUser.readUTF();
            arrayOfSockets.put(username, client);
            while(true) {
                String line = fromUser.readUTF();
                char[] str = line.toCharArray();
                boolean haveCommand = false;
                int i;
                for(i = 0; i < line.length(); i++) {
                    if(str[i] == '@') {
                        haveCommand = true;
                    }
                    if(haveCommand)
                        break;
                }
                if(haveCommand) {
                    if(this.command(line))
                        continue;
                } else {
                    char[] lettersOfName = new char[i + 1];
                    for (int j = 0; j < i; j++) {
                        lettersOfName[j] = str[j];
                    }
                    System.out.println(line);
                    for (Map.Entry<String, Socket> tmp : arrayOfSockets.entrySet()) {
                        String key = tmp.getKey();
                        if (!key.equals(username)) {
                            Socket curClient = tmp.getValue();
                            DataOutputStream toUser = new DataOutputStream(curClient.getOutputStream());
                            toUser.writeUTF(line);
                        }
                    }
                }
            }
        }
        catch (EOFException e) {
            System.out.println("Client " + username + " is disconnected");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}