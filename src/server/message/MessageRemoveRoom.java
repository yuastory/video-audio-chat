package server.message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import common.Room;
import common.SharedPreferences;
import common.User;
import server.Server;
import server.ServerThread;

public class MessageRemoveRoom extends Thread
{
    User hostToRemove;
    Room roomToRemove;

    public MessageRemoveRoom(User userToRemove, Room roomToRemove)
    {
        this.hostToRemove = userToRemove;
        this.roomToRemove = roomToRemove;
        this.start();
    }

    public void run()
    {
        System.out.println("\n========== SERVER MESSAGE ==========");
        System.out.println(SharedPreferences.getTime() + hostToRemove.getUserId() + " Has Removed The Room.");
        // 방장일 경우, 방 삭제
        // if (userToLeft.getUserId().equals(roomToLeft.getHost().getUserId()))
        // Server.rooms.remove(userToLeft.getUserId());
        // 단순히 방에 참여하고 인원일 경우에는 방 변경 정보를 업데이트
        // else
        Vector<User> usersToUpdate = new Vector<>();
        // 방에 접속해 있는 모든 인원들에 대해 방을 나가게 하고 업데이트 함
        for (String s : roomToRemove.getUsers())
        {
            if (s == null)
                continue;
            User userToUpdate = Server.users.get(s);
            if (userToUpdate != null && hostToRemove.getUserId().equals(userToUpdate.getUserId()))
                hostToRemove = userToUpdate;
            roomToRemove.removeUser(userToUpdate);
            Server.users.put(userToUpdate.getUserId(), userToUpdate);
            usersToUpdate.addElement(userToUpdate);
        }

        System.out.println("users in the room : " + usersToUpdate.size());

        Server.rooms.remove(hostToRemove.getUserId());
        for (ServerThread s : Server.serverThreads.values())
        {
            try
            {
                Socket socketSender = s.getSocketSender();
                ObjectOutputStream oos = new ObjectOutputStream(socketSender.getOutputStream());
                oos.writeInt(SharedPreferences.MESSAGE_ROOM_REMOVAL);
                oos.flush();
                oos.writeObject(hostToRemove);
                System.out.println("user To Update : " + hostToRemove.getUserId() + ", is Calling: " + hostToRemove.isCalling());
                oos.writeObject(roomToRemove);
                oos.writeObject(usersToUpdate);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3341, MessageRemoveRoom.class.getName());
            }
        }
        System.out.println(SharedPreferences.getTime() + "current room num : " + Server.rooms.size());
        System.out.println("====================================\n");
    }
}
