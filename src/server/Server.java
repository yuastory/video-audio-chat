package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import common.Room;
import common.SharedPreferences;
import common.User;

public class Server extends Thread
{
    public static Map<String, User>         users;
    public static Map<String, Room>         rooms;
    public static Map<String, ServerThread> serverThreads;
    Timer                                   timerKeepAlive;
    boolean                                 listening;

    public Server()
    {
        users = new ConcurrentHashMap<>();
        rooms = new ConcurrentHashMap<>();
        serverThreads = new ConcurrentHashMap<>();
        listening = true;
    }

    public synchronized void run()
    {
        ServerSocket loginServerSocket = null;
        ServerSocket senderListenerSocket = null;
        try
        {
            // 로그인 소켓 컨넥션
            loginServerSocket = new ServerSocket(SharedPreferences.PORT_SERVER_LOGIN);
            // 샌더 소켓 컨넥션
            senderListenerSocket = new ServerSocket(SharedPreferences.PORT_SERVER_LISTENER);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(SharedPreferences.getTime() + "Server Started.");
        while (listening)
        {
            Socket socketLogin = null;
            Socket socketListener = null;
            Socket socketSender = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try
            {
                System.out.println(SharedPreferences.getTime() + "Waiting for connections...");
                socketLogin = loginServerSocket.accept();
                System.out.println(SharedPreferences.getTime() + socketLogin.getInetAddress().getHostAddress() + " accepted.");
                oos = new ObjectOutputStream(socketLogin.getOutputStream());
                ois = new ObjectInputStream(socketLogin.getInputStream());
                // 1. 아이디 전송
                String userId = ois.readUTF().trim();
                System.out.println(SharedPreferences.getTime() + socketLogin.getInetAddress().getHostAddress() + " tried to connect as \"" + userId + "\"");
                // 중복검사
                if (users.containsKey(userId) || userId.length() == 0)
                {
                    System.err.println("\tduplicated.");
                    oos.writeInt(SharedPreferences.MESSAGE_LOGIN_DUPLICATED);
                    continue;
                }
                oos.writeInt(SharedPreferences.MESSAGE_LOGIN);
                oos.flush();
                // 2. 중간 로그인 소켓성립까지 완료.
                User userNew = new User(socketLogin.getInetAddress().getHostAddress(), userId);
                // listener소켓 컨넥션
                socketSender = new Socket(socketLogin.getInetAddress().getHostAddress(), SharedPreferences.PORT_SERVER_SENDER);
                socketSender.setKeepAlive(true);
                System.out.println(SharedPreferences.getTime() + "Sender Socket Connected.");
                // Sender소켓 컨넥션
                socketListener = senderListenerSocket.accept();
                socketListener.setKeepAlive(true);
                System.out.println(SharedPreferences.getTime() + "Listener Socket Connected.");
                users.put(userId, userNew);
                ServerThread serverThread = new ServerThread(socketListener, socketSender, socketLogin, userNew, null);
                serverThreads.put(userId, serverThread);
                serverThread.start();
            }
            catch (IOException e)
            {
                // 로그인중 하나라도 실패할 경우 소켓 다 닫아줌.
                try
                {
                    socketListener.close();
                    socketSender.close();
                    socketLogin.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    oos.close();
                    ois.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }

        try
        {
            loginServerSocket.close();
            senderListenerSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
        server.start();
    }
}
