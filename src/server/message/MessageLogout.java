package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageLogout extends Thread
{
    User userToLogout;

    public MessageLogout(User userToLogout)
    {
        this.userToLogout = userToLogout;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== LOGOUT ==========");
        System.out.println(SharedPreferences.getTime() + userToLogout.getUserId() + " has logged out.");
        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                // 연결 종료...
                System.out.println(SharedPreferences.getTime() + "Annouce Message :: LOGOUT");
                System.out.println(SharedPreferences.getTime() + "Announce to " + s.getUserMe().getUserId());
                oos.writeInt(SharedPreferences.MESSAGE_LOGOUT);
                oos.flush();
                oos.writeObject(userToLogout);
                if (s.getUserMe().getUserId().equals(userToLogout.getUserId()))
                {
                    oos.writeBoolean(false);
                    oos.flush();
                    s.logout();
                    continue;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Server.serverThreads.remove(userToLogout.getUserId());
        Server.users.remove(userToLogout.getUserId());
        System.out.println(SharedPreferences.getTime() + "Online Users: " + Server.serverThreads.size());
        System.out.println("====================================\n");
    }
}
