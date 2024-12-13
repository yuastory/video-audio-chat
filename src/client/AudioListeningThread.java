package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

import common.Recorder;
import common.SharedPreferences;
import common.User;

public class AudioListeningThread extends Thread
{
    private DatagramSocket serverSocket;
    int                    audioPort;
    AudioFormat            format;
    SourceDataLine         speaker;
    boolean                listening = true;
    boolean                recording = false;
    int                    sampleRate;
    byte[]                 buf;
    Map<String, Recorder>  recorders = null;

    public void stopListening()
    {
        listening = false;
    }

    DatagramPacket incoming;

    public AudioListeningThread(SourceDataLine speaker, AudioFormat format, int audioPort, int bufSize, boolean listening, boolean recording, Map<String, Recorder> recorders)
    {
        this.speaker = speaker;
        this.format = format;
        this.listening = listening;
        this.audioPort = audioPort;
        this.recording = recording;
        this.recorders = recorders;
        this.sampleRate = bufSize * 2;
        buf = new byte[bufSize];
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new DatagramSocket(audioPort);
        }
        catch (SocketException e)
        {
            // e.printStackTrace();
            SharedPreferences.printError(3300, AudioListeningThread.class.getName());
        }
        System.out.println("Audio Listen to " + audioPort);
        incoming = new DatagramPacket(buf, buf.length);
        while (listening)
        {
            try
            {
                serverSocket.receive(incoming);
            }
            catch (IOException e)
            {
                // e.printStackTrace();
                SharedPreferences.printError(3301, AudioListeningThread.class.getName());
                listening = false;
            }
            buf = incoming.getData();
            int numBytesRead = speaker.write(buf, 0, buf.length);
            if (recording)
            {
                int numSamplesRead = numBytesRead / 2;
                short[] audioSamples = new short[numSamplesRead];
                ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioSamples);
                for (Recorder recorder : recorders.values())
                {
                    if (!recorder.isAudioReady())
                        recorder.setAudioReady(true);
                    if (recorder.isVideoReady() && recorder.getWriter().isOpen())
                        recorder.getWriter().encodeAudio(1, audioSamples);
                }
            }
        }

        if (recording)
        {
            for (Recorder recorder : recorders.values())
                if (recorder.getWriter().isOpen())
                    recorder.getWriter().close();

            System.out.println(SharedPreferences.getTime() + "Recording Writers have all closed.");
        }
        speaker.close();
        speaker.drain();
        if (!serverSocket.isClosed())
            serverSocket.close();
        System.out.println(SharedPreferences.getTime() + "Audio listening to " + audioPort + " has disconnected.");
    }

    public boolean isListening()
    {
        return listening;
    }

    public void setListening(boolean listening)
    {
        this.listening = listening;
    }

    public DatagramSocket getServerSocket()
    {
        return serverSocket;
    }

    public void setServerSocket(DatagramSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

}
