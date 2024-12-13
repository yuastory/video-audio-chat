package client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import common.Recorder;
import common.SharedPreferences;
import common.User;
import video.Frame;

public class VideoListeningThread extends Thread
{
    private ServerSocket videoListeningSocket;
    int                  videoPort;
    private Socket       socket;
    private JPanel       panel;
    private boolean      recording = false;
    private Recorder     recorder  = null;
    Map<String, Boolean> listenings;
    User                 user;

    public VideoListeningThread(ServerSocket videoListeningSocket, int videoPort, JPanel panel, boolean recording, Recorder recorder, User user, Map<String, Boolean> listenings)
    {
        this.videoListeningSocket = videoListeningSocket;
        this.videoPort = videoPort;
        this.panel = panel;
        this.recording = recording;
        this.recorder = recorder;
        this.user = user;
        this.listenings = listenings;
    }

    public void run()
    {
        ObjectInputStream ois = null;
        ByteArrayInputStream inputImage = null;
        BufferedImage bufferedImage = null;
        try
        {
            System.out.println(SharedPreferences.getTime() + "Opening Listening port(" + videoPort + ")...");
            videoListeningSocket = new ServerSocket(videoPort);
            System.out.println(SharedPreferences.getTime() + "Listening Server Port(" + videoPort + ") has opened.");
            socket = videoListeningSocket.accept();
            socket.setSoTimeout(10000);
            System.out.println(SharedPreferences.getTime() + socket.getInetAddress().getHostAddress() + ":" + videoPort + " has connected.");
            ois = new ObjectInputStream(socket.getInputStream());
            bufferedImage = null;
            inputImage = null;
            Frame f = null;
            long startTime = System.nanoTime();
            while (ois.readBoolean())
            {
                try
                {
                    f = (Frame) ois.readObject();
                }
                catch (ClassNotFoundException e)
                {
                    // e.printStackTrace();
                    SharedPreferences.printError(3100, VideoListeningThread.class.getName());
                }
                inputImage = new ByteArrayInputStream(f.bytes);
                bufferedImage = ImageIO.read(inputImage);
                panel.getGraphics().drawImage(bufferedImage, 0, 0, panel.getWidth(), panel.getHeight(), null);
                if (recording)
                {
                    if (!recorder.isVideoReady())
                        recorder.setVideoReady(true);
                    if (recorder.isAudioReady() && recorder.getWriter().isOpen())
                        recorder.getWriter().encodeVideo(0, bufferedImage, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                }
                bufferedImage.flush();
                inputImage.close();
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
            SharedPreferences.printError(3101, VideoListeningThread.class.getName());
        }
        finally
        {
            if (recording)
                recorder.getWriter().close();
            bufferedImage.flush();
            try
            {
                inputImage.close();
                ois.close();
                videoListeningSocket.close();
                while (!socket.isClosed() && socket.getInputStream().read() != -1)
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
                if (panel != null)
                {
                    panel.getGraphics().setColor(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
                    panel.getGraphics().fillRect(panel.getLocation().x, panel.getLocation().y, panel.getWidth(), panel.getHeight());
                }
                System.out.println(SharedPreferences.getTime() + "Video listening to " + videoPort + " has disconnected.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3102, VideoListeningThread.class.getName());
            }
        }

    }

    public ServerSocket getVideoListeningSocket()
    {
        return videoListeningSocket;
    }

    public void setVideoListeningSocket(ServerSocket videoListeningSocket)
    {
        this.videoListeningSocket = videoListeningSocket;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public void setPanel(JPanel panel)
    {
        this.panel = panel;
    }
}
