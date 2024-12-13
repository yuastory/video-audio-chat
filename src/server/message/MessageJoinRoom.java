package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageJoinRoom extends Thread
{
    User userToJoin;
    Room roomToJoin;

    public MessageJoinRoom(User userToJoin, Room roomToJoin)
    {
        this.userToJoin = userToJoin;
        this.roomToJoin = roomToJoin;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== JOINING ROOM ==========");
        int message = SharedPreferences.MESSAGE_ROOM_JOIN;
        if (roomToJoin.getCurrentUserNum() >= roomToJoin.getMaxUserNum())
            message = SharedPreferences.MESSAGE_ROOM_FULL;
        else if (roomToJoin.isBanned(userToJoin))
            message = SharedPreferences.MESSAGE_ROOM_BANNED;
        else
            roomToJoin.addUser(userToJoin);
        Server.rooms.put(roomToJoin.getHost().getUserId(), roomToJoin);

        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                oos.writeInt(SharedPreferences.MESSAGE_ROOM_JOIN);
                oos.flush();
                oos.writeObject(userToJoin);
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(roomToJoin);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3341, MessageJoinRoom.class.getName());
            }
        }

        System.out.println(SharedPreferences.getTime() + userToJoin.getUserId() + " has created a room");
        System.out.println(SharedPreferences.getTime() + "room title : " + roomToJoin.getTitle());
        System.out.println(SharedPreferences.getTime() + "room host : " + roomToJoin.getHost().getUserId());
        System.out.println(SharedPreferences.getTime() + "room recordable : " + roomToJoin.isRecording());
        System.out.println(SharedPreferences.getTime() + "room capability : " + roomToJoin.getMaxUserNum());
        System.out.println(SharedPreferences.getTime() + "room current users : " + roomToJoin.getCurrentUserNum());
        System.out.println(SharedPreferences.getTime() + "is user calling : " + userToJoin.isCalling());
        System.out.println(SharedPreferences.getTime() + "host position : " + userToJoin.getPosition());
        System.out.println("====================================\n");
    }
}
