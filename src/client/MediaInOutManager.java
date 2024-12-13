package client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JPanel;

import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import common.Recorder;
import common.SharedPreferences;
import common.User;
import video.VideoCap;

public class MediaInOutManager
{
    Map<String, VideoCallingThread>   videoCallingThreads;
    Map<String, VideoListeningThread> videoListeningThreads;
    Map<String, AudioCallingThread>   audioCallingThreads;
    Map<String, Recorder>             recorders;
    Map<String, Boolean>              callings;
    AudioListeningThread              audioListeningThread;
    AudioFormat                       format;
    TargetDataLine                    mic;
    SourceDataLine                    speaker;
    DatagramSocket                    audioSocket;
    Boolean                           recording;
    boolean                           audioListening = true;
    float                             frameRate;
    int                               bufSize;

    public MediaInOutManager(boolean recording)
    {
        videoCallingThreads = new ConcurrentHashMap<>();
        videoListeningThreads = new ConcurrentHashMap<>();
        audioCallingThreads = new ConcurrentHashMap<>();
        recorders = new ConcurrentHashMap<>();
        callings = new ConcurrentHashMap<>();
        this.recording = recording;
        setAudio();
    }

    public void openAudioListener(int audioListeningPort)
    {
        try
        {
            audioSocket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        bufSize = (int) frameRate / 2;
        // bufSize = (int) frameRate;
        audioListeningThread = new AudioListeningThread(speaker, format, audioListeningPort, bufSize, true, recording, recorders);
        audioListeningThread.start();
    }

    public void openMediaSocket(User user, VideoCap videoCap, int videoCallingPort, int videoListeningPort, int audioCallingPort, JPanel panel)
    {
        InetAddress inetAddress = null;
        Recorder recorder = null;
        callings.put(user.getUserId(), true);
        try
        {
            inetAddress = InetAddress.getByName(user.getIp());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        AudioCallingThread audioCallingThread = new AudioCallingThread(audioSocket, mic, inetAddress, audioCallingPort, bufSize, user, callings);
        audioCallingThread.start();
        VideoCallingThread videoCallingThread = new VideoCallingThread(videoCap, null, user.getIp(), videoCallingPort, user, callings);
        videoCallingThread.start();

        // 녹음할 경우 셋팅
        if (recording)
        {
            recorder = new Recorder(ToolFactory.makeWriter(user.getUserId() + "_" + user.getPosition() + ".mp4"));
            recorder.getWriter().addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, SharedPreferences.RECORDER_WIDTH, SharedPreferences.RECORDER_HEIGHT);
            recorder.getWriter().addAudioStream(1, 0, ICodec.ID.CODEC_ID_AAC, 1, (int) frameRate);
            recorders.put(user.getUserId(), recorder);
        }

        VideoListeningThread videoListeningThread = new VideoListeningThread(null, videoListeningPort, panel, recording, recorder, user, callings);
        videoListeningThread.start();
        audioCallingThreads.put(user.getUserId(), audioCallingThread);
        videoCallingThreads.put(user.getUserId(), videoCallingThread);
        videoListeningThreads.put(user.getUserId(), videoListeningThread);
    }

    public void closeVideoSocket(String userId)
    {
        callings.put(userId, false);
        videoCallingThreads.remove(userId);
        videoListeningThreads.remove(userId);
        audioCallingThreads.remove(userId);
        if (recording)
        {
            Recorder recorder = recorders.get(userId);
            recorder.setAudioReady(false);
            recorder.setVideoReady(false);
            recorders.remove(userId);
        }
    }

    public void closeAllVideoSocket()
    {
        for (String userId : videoCallingThreads.keySet())
            closeVideoSocket(userId);
        System.out.println(SharedPreferences.getTime() + "Closing video sockets done.");
        audioListeningThread.setListening(false);
        // 오디오 소켓은 각각 1채널씩만 쓰기 때문에 방이 없어질때만 close함.
        audioListeningThread.getServerSocket().close();
        audioSocket.close();
        if (mic.isOpen())
            mic.close();
        if (speaker.isOpen())
            speaker.close();
    }

    void setAudio()
    {
//        frameRate = 8000.0F;
        frameRate = 44100.0F;
        int bitPerSample = 16;
        int channels = 1;
        boolean bigEndian = false;
        try
        {
            format = new AudioFormat(frameRate, bitPerSample, channels, true, bigEndian);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info))
            {
                System.out.println("not support");
                System.exit(0);
            }
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);
            mic.start();

            info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info))
            {
                System.out.println("not support");
                System.exit(0);
            }
            speaker = (SourceDataLine) AudioSystem.getLine(info);
            speaker.open(format);
            speaker.start();
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isAudioListening()
    {
        return audioListening;
    }

    public void setAudioListening(boolean audioListening)
    {
        this.audioListening = audioListening;
    }

}
