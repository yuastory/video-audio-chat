package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import javax.sound.sampled.TargetDataLine;

import common.SharedPreferences;
import common.User;

public class AudioCallingThread extends Thread
{
    DatagramSocket       audioSocket;
    InetAddress          serverIp;
    int                  audioPort;
    TargetDataLine       mic;
    byte[]               buf;
    Map<String, Boolean> callings;
    User                 userMe;

    public AudioCallingThread(DatagramSocket audioSocket, TargetDataLine mic, InetAddress serverIp, int audioPort, int bufSize, User userMe, Map<String, Boolean> callings)
    {
        this.audioSocket = audioSocket;
        this.mic = mic;
        this.serverIp = serverIp;
        this.audioPort = audioPort;
        this.callings = callings;
        this.userMe = userMe;
        buf = new byte[bufSize];
    }

    @Override
    public void run()
    {
        System.out.println("Audio call to " + audioPort);
        while (callings.get(userMe.getUserId()))
        {
            try
            {
                mic.read(buf, 0, buf.length);
                DatagramPacket data = new DatagramPacket(buf, buf.length, serverIp, audioPort);
                audioSocket.send(data);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                SharedPreferences.printError(3350, AudioCallingThread.class.getName());
            }
        }

        System.out.println(SharedPreferences.getTime() + "Audi calling to " + audioPort + " has disconnected.");
    }
}
