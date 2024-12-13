package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageInvite extends Thread
{
    User userDidInvitation;
    User userToInvite;
    Room roomToInvite;

    public MessageInvite(User userToInvite, User userDidInvitation, Room roomToInvite)
    {
        this.userToInvite = userToInvite;
        this.userDidInvitation = userDidInvitation;
        this.roomToInvite = roomToInvite;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== INVITE TO ROOM ==========");
        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                oos.writeInt(SharedPreferences.MESSAGE_ROOM_INVITE);
                oos.flush();
                oos.writeObject(userToInvite);
                oos.writeObject(userDidInvitation);
                oos.writeObject(roomToInvite);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3341, MessageInvite.class.getName());
            }
        }

        System.out.println(SharedPreferences.getTime() + userToInvite.getUserId() + " has created a room");
        System.out.println(SharedPreferences.getTime() + "room title : " + roomToInvite.getTitle());
        System.out.println(SharedPreferences.getTime() + "room host : " + roomToInvite.getHost().getUserId());
        System.out.println(SharedPreferences.getTime() + "room recordable : " + roomToInvite.isRecording());
        System.out.println(SharedPreferences.getTime() + "room capability : " + roomToInvite.getMaxUserNum());
        System.out.println(SharedPreferences.getTime() + "room current users : " + roomToInvite.getCurrentUserNum());
        System.out.println(SharedPreferences.getTime() + "is user calling : " + userToInvite.isCalling());
        System.out.println(SharedPreferences.getTime() + "host position : " + userToInvite.getPosition());
        System.out.println("====================================\n");
    }
}
