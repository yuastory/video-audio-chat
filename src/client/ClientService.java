package client;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import common.Room;
import common.SharedPreferences;
import common.User;
import ui.LoginFrame;
import ui.MainFrame;
import ui.RoomFrame;

/**
 * 
 * @author BANA-LAPTOP 클라이언트가 브로드캐스팅 메시지를 듣는 곳
 */
public class ClientService extends Thread
{
    Socket            socketListener = null;
    Socket            socketSender   = null;
    Socket            socketLogin    = null;

    Map<String, User> users;
    Map<String, Room> rooms;
    User              userMe         = null;
    Room              roomMe         = null;
    MainFrame         mainFrame      = null;
    RoomFrame         roomFrame      = null;
    LoginFrame        loginFrame;

    public ClientService(LoginFrame loginFrame)
    {
        users = new ConcurrentHashMap<>();
        rooms = new ConcurrentHashMap<>();
        this.loginFrame = loginFrame;
    }

    public int login(String userId, Point p)
    {
        ServerSocket socketServerListener = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        int message = -1;
        try
        {
            socketServerListener = new ServerSocket(SharedPreferences.PORT_CLIENT_LISTENER);
            socketLogin = new Socket(SharedPreferences.SERVER_IP, SharedPreferences.PORT_SERVER_LOGIN);
            socketLogin.setSoTimeout(3000);
            oos = new ObjectOutputStream(socketLogin.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socketLogin.getInputStream());
            oos.writeUTF(userId);
            oos.flush();
            message = ois.readInt();
            if (message == SharedPreferences.MESSAGE_LOGIN_DUPLICATED)
            {
                socketLogin.close();
                return message;
            }

            // 리스닝 소켓 연결
            socketListener = socketServerListener.accept();
            socketListener.setKeepAlive(true);
            socketSender = new Socket(SharedPreferences.SERVER_IP, SharedPreferences.PORT_CLIENT_SENDER);
            socketSender.setKeepAlive(true);
            System.out.println(SharedPreferences.getTime() + "Login Successful");
            setMainFrame(new MainFrame(p, this));
            getMainFrame().setVisible(true);
            start();
        }
        catch (IOException e)
        {
            message = SharedPreferences.MESSAGE_LOGIN_TIMEOUT;
            SharedPreferences.printError(1129, ClientService.class.getName());
        }
        finally
        {
            try
            {
                socketServerListener.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return message;
    }

    public void informServer(int message, User userParam, Room roomParam)
    {
        System.out.println("\n-------------------------------");
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try
        {
            oos = new ObjectOutputStream(socketSender.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socketSender.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (message == SharedPreferences.MESSAGE_ROOM_INVITE)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: Inviting Room");
                // 데이터 전송.
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userParam);
                oos.writeObject(userMe);
                oos.writeObject(roomParam);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(1101, ClientService.class.getName());
            }
        }
        else if (message == SharedPreferences.MESSAGE_ROOM_REMOVAL)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: Removing Room");
                // 데이터 전송.
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userParam);
                oos.writeObject(roomParam);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(1101, ClientService.class.getName());
            }
        }
        else if (message == SharedPreferences.MESSAGE_ROOM_LEFT)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: Lefting Room");
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userMe);
                oos.writeObject(roomParam);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(1101, ClientService.class.getName());
            }
        }
        else if (message == SharedPreferences.MESSAGE_ROOM_JOIN)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: Entering Room");
                // 데이터 전송.
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userMe);
                oos.writeObject(roomParam);
            }
            catch (IOException e)
            {
                // e.printStackTrace();
                SharedPreferences.printError(1103, ClientService.class.getName());
            }
        }
        else if (message == SharedPreferences.MESSAGE_ROOM_CREATTION)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: Creating Room");
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userParam);

                boolean recording = mainFrame.getChkRecording().isSelected();
                String title = mainFrame.getTfTitle().getText();
                int maxNum = (Integer) mainFrame.getCmbMaxUser().getSelectedItem();
                // 서버에게 방을 만들어달라고 요청
                oos.writeBoolean(recording);
                oos.flush();
                oos.writeUTF(title);
                oos.flush();
                oos.writeInt(maxNum);
                oos.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(1107, ClientService.class.getName());
            }
        }
        else if (message == SharedPreferences.MESSAGE_LOGOUT)
        {
            try
            {
                System.out.println(SharedPreferences.getTime() + "Inform :: LOGOUT");
                oos.writeInt(message);
                oos.flush();
                oos.writeObject(userParam);
                oos.writeBoolean(false);
                oos.flush();

            }
            catch (IOException e)
            {
                // e.printStackTrace();
                SharedPreferences.printError(1109, ClientService.class.getName());
            }
        }

        System.out.println("-------------------------------\n");
    }

    /**
     * receiver!! Listener!!
     */
    public synchronized void run()
    {
        boolean listening = true;
        while (listening)
        {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            int message = -1;
            User user = null;
            // 브로드캐스팅 소켓을 듣고있음
            try
            {
                oos = new ObjectOutputStream(socketListener.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(socketListener.getInputStream());
                message = ois.readInt();
                user = (User) ois.readObject();
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(1113, ClientService.class.getName());
                listening = false;
            }
            System.out.println("\n===============================");
            // 브로드캐스팅 메시지를 읽어옴
            if (message == SharedPreferences.MESSAGE_ALIVE)
                System.out.println(SharedPreferences.getTime() + "Message :: Alive");
            // 로그인
            else if (message == SharedPreferences.MESSAGE_LOGIN)
            {
                try
                {
                    System.out.println(SharedPreferences.getTime() + "Message :: Login");
                    // 로그인 시도한 사람일경우
                    if (userMe == null || userMe.getUserId().equals(user.getUserId()))
                    {
                        userMe = user;
                        users = (ConcurrentHashMap<String, User>) ois.readObject();
                        rooms = (ConcurrentHashMap<String, Room>) ois.readObject();
                        mainFrame.getLblUserMe().setText(userMe.getUserId());
                        mainFrame.initLists();
                    }
                    // 로그인 되어있는 사람일 경우
                    else
                    {
                        users.put(user.getUserId(), user);
                        mainFrame.updateLists(user, null);
                    }

                    System.out.println(SharedPreferences.getTime() + user.getUserId() + " has Logged in.");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            // 로그아웃
            else if (message == SharedPreferences.MESSAGE_LOGOUT)
            {
                System.out.println(SharedPreferences.getTime() + "Message :: Logout");
                // 내가 로그아웃 주체일 경우
                if (user.getUserId().equals(userMe.getUserId()))
                {
                    try
                    {
                        listening = ois.readBoolean();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    loginFrame.setVisible(true);
                }
                users.remove(user.getUserId());
                // list 새로고침 코드
                for (int i = 0; i < mainFrame.getListModelUser().size(); i++)
                {
                    User userTemp = mainFrame.getListModelUser().get(i);
                    if (userTemp.getUserId().equals(user.getUserId()))
                        mainFrame.getListModelUser().removeElement(userTemp);
                }

                System.out.println(SharedPreferences.getTime() + user.getUserId() + " has Logged out.");
            }
            // 방 생성
            else if (message == SharedPreferences.MESSAGE_ROOM_CREATTION)
            {
                try
                {
                    Room roomTobeCreated = (Room) ois.readObject();
                    rooms.put(user.getUserId(), roomTobeCreated);
                    users.put(user.getUserId(), user);
                    System.out.println("host: " + roomTobeCreated.getHost().getUserId());
                    // 호스트인 경우
                    if (userMe.getUserId().equals(user.getUserId()))
                    {
                        userMe = user;
                        roomMe = roomTobeCreated;
                        mainFrame.getTfTitle().setText("");
                        mainFrame.getChkRecording().setSelected(false);
                        mainFrame.getCmbMaxUser().setSelectedIndex(0);
                        mainFrame.setVisible(false);
                        roomFrame = new RoomFrame(mainFrame.getLocation(), ClientService.this);
                        roomFrame.setVisible(true);
                    }
                    mainFrame.updateLists(user, roomTobeCreated);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                System.out.println(SharedPreferences.getTime() + user.getUserId() + " has created a room.");
            }
            else if (message == SharedPreferences.MESSAGE_ROOM_JOIN)
            {
                try
                {
                    System.out.println(SharedPreferences.getTime() + "Message :: Room User Join");
                    int message1 = ois.readInt();
                    Room roomToJoin = (Room) ois.readObject();
                    if (message1 == SharedPreferences.MESSAGE_ROOM_FULL)
                    {
                        // 접속시도한 본인의 경우 방이 꽉찼다고 알림창.
                        if (userMe.getUserId().equals(user.getUserId()))
                            mainFrame.alertFull();
                        System.out.println(SharedPreferences.getTime() + user + " has tried to connect the room full.");
                        continue;
                    }
                    else if (message1 == SharedPreferences.MESSAGE_ROOM_BANNED)
                    {
                        // 접속시도한 본인의 경우 강퇴됐던 방이라고 알림창.
                        if (userMe.getUserId().equals(user.getUserId()))
                            mainFrame.alertBanned();
                        System.out.println(SharedPreferences.getTime() + user + " has tried to connect the room banned.");
                        continue;
                    }

                    rooms.put(roomToJoin.getHost().getUserId(), roomToJoin);
                    users.put(user.getUserId(), user);
                    mainFrame.updateLists(user, roomToJoin);

                    // 방에 참여하고 잇는 사람들한테 웹캠 포트 열으라 알려줌
                    if (roomToJoin.participateIn(userMe.getUserId()))
                    {
                        roomMe = roomToJoin;
                        // 입장하는 본인의 경우는 자신의 포트만 열어주면 되서 무 상 관.
                        if (user.getUserId().equals(userMe.getUserId()))
                        {
                            userMe = user;
                            mainFrame.setVisible(false);
                            new Runnable()
                            {
                                public void run()
                                {
                                    roomFrame = new RoomFrame(mainFrame.getLocation(), ClientService.this);
                                    roomFrame.setVisible(true);
                                }
                            }.run();
                        }
                        else
                            roomFrame.joinUser(user);
                    }

                    System.out.println(SharedPreferences.getTime() + user.getUserId() + " has joined the room.");
                }
                catch (IOException | ClassNotFoundException e)
                {
                    // e.printStackTrace();
                    SharedPreferences.printError(1117, ClientService.class.getName());
                }
            }
            // 방에 인원이 나간 경우
            else if (message == SharedPreferences.MESSAGE_ROOM_LEFT)
            {
                try
                {
                    System.out.println(SharedPreferences.getTime() + "Message :: Room User Left");
                    User userToLeft = user;
                    Room roomToLeft = (Room) ois.readObject();
                    users.put(user.getUserId(), user);
                    rooms.put(roomToLeft.getHost().getUserId(), roomToLeft);
                    new Runnable()
                    {
                        public void run()
                        {
                            // 본인일 경우
                            if (userToLeft.getUserId().equals(userMe.getUserId()))
                            {
                                System.out.println("You are going out of the room.");
                                userMe = userToLeft;
                                roomMe = null;
                                mainFrame.setLocation(roomFrame.getLocation());
                                mainFrame.setVisible(true);
                                roomFrame.onDestroy();
                                roomFrame.dispose();
                                roomFrame = null;
                            }
                            // 방에 참여하고 있는 사람일 경우
                            if (roomToLeft.participateIn(userMe.getUserId()))
                            {
                                roomMe = roomToLeft;
                                System.out.println("You're currently in the room, so closing " + userToLeft.getUserId() + "'s Video & Audio socket has been invoked.");
                                // 다른 사람일 경우
                                roomFrame.removeUser(userToLeft);
                            }
                            mainFrame.updateLists(userToLeft, roomToLeft);
                            System.out.println(SharedPreferences.getTime() + userToLeft.getUserId() + " has left the room.");
                        }
                    }.run();
                }
                catch (IOException | ClassNotFoundException e)
                {
                    SharedPreferences.printError(1119, ClientService.class.getName());
                }
            }
            // 방 삭제
            else if (message == SharedPreferences.MESSAGE_ROOM_REMOVAL)
            {
                try
                {
                    User userr = user;
                    Room roomToRemove = (Room) ois.readObject();
                    // 방에 있는 사람들의 상태가 변경되어 벡터값으로 받아옴
                    Vector<User> usersToUpdate = (Vector<User>) ois.readObject();
                    // 받아온 백터속에 유저들 상태를 업데이트함

                    Room roomRemoved = rooms.remove(user.getUserId());
                    for (User userToUpdate : usersToUpdate)
                    {
                        if (userToUpdate.getUserId().equals(userMe.getUserId()))
                            userMe = userToUpdate;
                        users.put(userToUpdate.getUserId(), userToUpdate);
                        // 리스트를 최신화해줌.
                        mainFrame.updateLists(userToUpdate, null);
                    }
                    // 방을 리스트에서 없애줌.
                    mainFrame.removeRoomFromList(roomToRemove);

                    // 방에 참여하고 있는 사람의 경우에는 컨넥션을 다 끊어주고 방에서 나감.
                    if (roomRemoved.participateIn(userMe.getUserId()))
                    {
                        roomMe = null;
                        // 방장의 경우는 알림을 안받아도 됨.
                        if (userMe.getUserId().equals(roomRemoved.getHost().getUserId()))
                        {
                            System.out.println(SharedPreferences.getTime() + "I'm the host.");
                            userMe = userr;
                        }
                        else
                        {
                            mainFrame.alertRoomRemoved();
                            roomFrame.onDestroy();
                        }
                        // 방을 나가고 연결을 전부 끊음.
                        new Runnable()
                        {
                            public void run()
                            {
                                mainFrame.setLocation(roomFrame.getLocation());
                                roomFrame.dispose();
                                mainFrame.setVisible(true);
                            }
                        }.run();
                    }
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
                    User userToInvite = user;
                    User userDidInvitation = (User) ois.readObject();
                    Room roomToInvite = (Room) ois.readObject();
                    // 초대받을 대상일 경우
                    if (userToInvite.getUserId().equals(userMe.getUserId()))
                    {
                        new Runnable()
                        {
                            public void run()
                            {
                                int dialogResult = JOptionPane.showConfirmDialog(null, userDidInvitation.getUserId() + "님께서\n방[" + roomToInvite.getTitle() + "]에 초대하셨습니다.\n입장하시겠습니가?\n", "", JOptionPane.YES_NO_OPTION);
                                if (dialogResult == JOptionPane.YES_OPTION)
                                    informServer(SharedPreferences.MESSAGE_ROOM_JOIN, userMe, roomToInvite);
                            }
                        }.run();
                    }

                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            System.out.println("===============================\n");
        }

        try
        {
            // 로그아웃 모듈이 다 전송될때까지 기다림
            System.out.println("waiting for closing socketListener...");
            socketListener.setSoTimeout(3000);
            while (!socketListener.isClosed() && socketListener.getInputStream().read() != -1)
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
            System.out.println("socketListener has closed.");
            socketSender.close();
            socketListener.close();
            socketLogin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        finish();
        System.out.println(SharedPreferences.getTime() + "Client Broadcaster port closed.");

    }

    // 종료...
    public void finish()
    {
        if (!loginFrame.isVisible())
        {
            loginFrame.setLocation(mainFrame.getLocation());
            loginFrame.setVisible(true);
            mainFrame.dispose();
            users.clear();
            rooms.clear();
            userMe = null;
            roomMe = null;
            mainFrame = null;
            roomFrame = null;
        }
    }

    public MainFrame getMainFrame()
    {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame)
    {
        this.mainFrame = mainFrame;
    }

    public RoomFrame getRoomFrame()
    {
        return roomFrame;
    }

    public void setRoomFrame(RoomFrame roomFrame)
    {
        this.roomFrame = roomFrame;
    }

    public Socket getListenerSocket()
    {
        return socketListener;
    }

    public void setListenerSocket(Socket listenerSocket)
    {
        this.socketListener = listenerSocket;
    }

    public Map<String, User> getUsers()
    {
        return users;
    }

    public void setUsers(Map<String, User> users)
    {
        this.users = users;
    }

    public Map<String, Room> getRooms()
    {
        return rooms;
    }

    public void setRooms(Map<String, Room> rooms)
    {
        this.rooms = rooms;
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

}
