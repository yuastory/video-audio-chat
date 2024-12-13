package client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import javax.imageio.ImageIO;

import common.SharedPreferences;
import common.User;
import video.Frame;
import video.VideoCap;

public class VideoCallingThread extends Thread
{
    private final String         formatType = "jpg";
    private VideoCap             videoCap;
    private Map<String, Boolean> callings;
    private Socket               socket;
    private String               ip;
    int                          videoPort;
    User                         userMe;

    public VideoCallingThread(VideoCap videoCap, Socket socket, String ip, int videoPort, User userMe, Map<String, Boolean> callings)
    {
        this.videoCap = videoCap;
        this.socket = socket;
        this.videoPort = videoPort;
        this.ip = ip;
        this.userMe = userMe;
        this.callings = callings;
    }

    public void run()
    {
        BufferedImage bufferedImage = null;
        ObjectOutputStream oos = null;
        Frame f = null;
        try
        {
            System.out.println(SharedPreferences.getTime() + "Video Calling to " + ip + ":" + videoPort + "...");
            socket = new Socket(ip, videoPort);
            System.out.println(SharedPreferences.getTime() + "Connected to " + socket.getInetAddress().getHostAddress() + ":" + videoPort);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
        while (callings.get(userMe.getUserId()))
        {
            try
            {
                bufferedImage = videoCap.getOneFrame();
            }
            catch (Exception e)
            {
                SharedPreferences.printError(3010, VideoCallingThread.class.getName());
                continue;
            }
            try
            {
                ImageIO.write(bufferedImage, formatType, fbaos);
                f = new Frame(fbaos.toByteArray());
                oos.writeBoolean(true);
                 oos.flush();
                oos.writeObject(f);
                bufferedImage.flush();
                fbaos.reset();
                oos.reset();
            }

            catch (IOException e)
            {
                callings.put(userMe.getUserId(), false);
                e.printStackTrace();
                SharedPreferences.printError(3020, VideoCallingThread.class.getName());
            }
        }

        try
        {
            oos.flush();
            oos.writeBoolean(false);
            oos.flush();

            // 마지막 boolean이 다 전송될까지 기다림
            while (socket.getInputStream().read() != -1)
            {
                try
                {
                    Thread.sleep(30);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(SharedPreferences.getTime() + "Video calling to " + ip + ":" + videoPort + " has disconnected.");
    }

}
