package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageLogin extends Thread
{
    User userToJoin;

    public MessageLogin(User userToJoin)
    {
        this.userToJoin = userToJoin;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== LOGIN ==========");
        System.out.println(SharedPreferences.getTime() + userToJoin.getUserId() + " has logged in.");
        for (ServerThread s : Server.serverThreads.values())
        {
            Socket socketSender = null;
            ObjectOutputStream oos = null;
            try
            {
                socketSender = s.getSocketSender();
                oos = new ObjectOutputStream(socketSender.getOutputStream());
                System.out.println(SharedPreferences.getTime() + "Annouce Message :: LOGIN");
                System.out.println(SharedPreferences.getTime() + "Announce to " + s.getUserMe().getUserId());
                oos.writeInt(SharedPreferences.MESSAGE_LOGIN);
                oos.flush();
                oos.writeObject(userToJoin); // 새로운 유저
                // 새로운 유저일 경우 리스트도 보냄.
                if (userToJoin.getUserId().equals(s.getUserMe().getUserId()))
                {
                    oos.writeObject(Server.users); // 서버가 가지고 있는 유저 리스트
                    oos.writeObject(Server.rooms); // 서버가 가지고 있는 방 리스트
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            System.out.println(SharedPreferences.getTime() + "Online User : " + Server.serverThreads.size());
            System.out.println("====================================\n");
        }
    }
}
