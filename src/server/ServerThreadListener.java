package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.message.MessageCreateRoom;
import server.message.MessageInvite;
import server.message.MessageJoinRoom;
import server.message.MessageLeftRoom;
import server.message.MessageLogout;
import server.message.MessageRemoveRoom;

public class ServerThreadListener extends Thread
{
    Socket       socketListener;
    ServerThread serverThread;

    public ServerThreadListener(Socket socketListener, ServerThread serverThread)
    {
        this.socketListener = socketListener;
        this.serverThread = serverThread;
        this.start();
    }

    public synchronized void run()
    {
        while (serverThread.listening)
        {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            int message = -1;
            User user = null;
            try
            {
                oos = new ObjectOutputStream(socketListener.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(socketListener.getInputStream());
            }
            catch (IOException e)
            {
                serverThread.listening = false;
                e.printStackTrace();
                continue;
            }
            try
            {
                message = ois.readInt();
                user = (User) ois.readObject();

            }
            catch (Exception e)
            {

                serverThread.listening = false;
                e.printStackTrace();
                continue;
            }
            if (message == SharedPreferences.MESSAGE_LOGOUT)
            {
                try
                {
                    serverThread.listening = ois.readBoolean();
                }
                catch (IOException e)
                {
                    e.printStackTrace();

                }
                new MessageLogout(user);
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_CREATTION)
            {
                try
                {
                    boolean recording = ois.readBoolean();
                    String title = ois.readUTF();
                    int capability = ois.readInt();
                    Room roomTobeCreated = new Room(title, recording, capability);
                    roomTobeCreated.addUser(user); // 방속에 첫번째 입장 = host
                    new MessageCreateRoom(user, roomTobeCreated);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_JOIN)
            {
                try
                {
                    Room roomToJoin = (Room) ois.readObject();
                    new MessageJoinRoom(user, roomToJoin);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_LEFT)
            {
                try
                {
                    Room roomToLeft = (Room) ois.readObject();
                    new MessageLeftRoom(user, roomToLeft);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_REMOVAL)
            {
                try
                {
                    Room roomToRemove = (Room) ois.readObject();
                    new MessageRemoveRoom(user, roomToRemove);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_INVITE)
            {
                try
                {
                    User userDidInvitation = (User) ois.readObject();
                    Room roomToInvite = (Room) ois.readObject();
                    new MessageInvite(user, userDidInvitation, roomToInvite);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            // ois.close();
            // oos.close();
            socketListener.close();
            serverThread.socketListener.close();
            serverThread.socketSender.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Room roomToRemove = Server.rooms.get(serverThread.userMe.getUserId());
        // 갑작스러운 종료로 방을 종료하지 못했을 때
        if (roomToRemove != null)
        {
            System.out.println(SharedPreferences.getTime() + "abruptly room closed.");
            new MessageRemoveRoom(serverThread.userMe, roomToRemove);
        }
        Server.serverThreads.remove(serverThread.userMe.getUserId());
        System.out.println(serverThread.userMe.getUserId() + "Listening 소켓 종료.");
        new MessageLogout(serverThread.userMe);

    }
}
