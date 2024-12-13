package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageLeftRoom extends Thread
{
    User userToLeft;
    Room roomToLeft;

    public MessageLeftRoom(User userToLeft, Room roomToLeft)
    {
        this.userToLeft = userToLeft;
        this.roomToLeft = roomToLeft;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== LEFTING ROOM ==========");
        System.out.println(SharedPreferences.getTime() + userToLeft.getUserId() + " Has Left The Room.");
        roomToLeft.removeUser(userToLeft);
        // 방이 지워질때 요청하는 요청이면 방을 수정하면 추가되므로 안됨.
        if (Server.rooms.get(roomToLeft.getHost().getUserId()) != null)
            Server.rooms.put(userToLeft.getUserId(), roomToLeft);
        Server.users.put(userToLeft.getUserId(), userToLeft); // 유저정보 업데이트는 공통 사항
        System.out.println("user To Remove: " + userToLeft.getUserId() + ", is Calling: " + userToLeft.isCalling());

        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                oos.writeInt(SharedPreferences.MESSAGE_ROOM_LEFT);
                oos.flush();
                oos.writeObject(userToLeft);
                oos.writeObject(roomToLeft);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3341, MessageLeftRoom.class.getName());
            }
        }
        System.out.println("====================================\n");
    }
}
