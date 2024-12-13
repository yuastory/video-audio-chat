package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageCreateRoom extends Thread
{
    User host;
    Room roomTobeCreated;

    public MessageCreateRoom(User host, Room roomToCreate)
    {
        this.host = host;
        this.roomTobeCreated = roomToCreate;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== CREATING ROOM ==========");
        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                oos.writeInt(SharedPreferences.MESSAGE_ROOM_CREATTION);
                oos.flush();
                oos.writeObject(host);
                oos.writeObject(roomTobeCreated);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3341, MessageCreateRoom.class.getName());
            }
        }
        Server.users.put(host.getUserId(), host); // 서버 유저관리 리스트에 수정된 호스트
        Server.rooms.put(host.getUserId(), roomTobeCreated);
        System.out.println(SharedPreferences.getTime() + "Online Users: " + Server.serverThreads.size());
        System.out.println(SharedPreferences.getTime() + host.getUserId() + " has created a room");
        System.out.println(SharedPreferences.getTime() + "room title : " + roomTobeCreated.getTitle());
        System.out.println(SharedPreferences.getTime() + "room host : " + roomTobeCreated.getHost().getUserId());
        System.out.println(SharedPreferences.getTime() + "room recordable : " + roomTobeCreated.isRecording());
        System.out.println(SharedPreferences.getTime() + "room capability : " + roomTobeCreated.getMaxUserNum());
        System.out.println(SharedPreferences.getTime() + "is user calling : " + host.isCalling());
        System.out.println(SharedPreferences.getTime() + "host position : " + host.getPosition());
        System.out.println("====================================\n");
    }
}
