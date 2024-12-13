package server;

import java.io.IOException;
import java.net.Socket;

import common.Room;
import common.User;
import server.message.MessageLogin;

/**
 * 
 * @author BANA-LAPTOP
 * 서버가 클라이언트로부터 갱신을 받고
 * 클라이언트에 열려있는 브로드캐스팅용 소켓에  keep alive 메시지를 보냄
 */
public class ServerThread extends Thread
{
    User                 userMe;
    Room                 roomMe;
    ServerThreadListener serverThreadListener;
    Socket               socketListener;
    Socket               socketSender;
    Socket               socketLogin;
    boolean              listening;
    // Timer timerAlive;

    public ServerThread(Socket socketListener, Socket socketSender, Socket socketLogin, User userMe, Room roomMe)
    {
        this.socketListener = socketListener;
        this.socketSender = socketSender;
        this.socketLogin = socketLogin;
        this.userMe = userMe;
        this.roomMe = roomMe;
        listening = true;
        serverThreadListener = new ServerThreadListener(socketListener, this);
    }

    public void run()
    {
        new MessageLogin(userMe);
    }

    public void logout()
    {
        try
        {
            // 신호가 갈때까지 기다림
            while (!socketSender.isClosed() && socketSender.getInputStream().read() != -1)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            socketSender.close();
            socketLogin.close();
            listening = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public User getUserMe()
    {
        return userMe;
    }

    public void setUserMe(User userMe)
    {
        this.userMe = userMe;
    }

    public Room getRoomMe()
    {
        return roomMe;
    }

    public void setRoomMe(Room roomMe)
    {
        this.roomMe = roomMe;
    }

    public Socket getSocketListener()
    {
        return socketListener;
    }

    public void setSocketListener(Socket socketListener)
    {
        this.socketListener = socketListener;
    }

    public Socket getSocketSender()
    {
        return socketSender;
    }

    public void setSocketSender(Socket socketSender)
    {
        this.socketSender = socketSender;
    }

}
