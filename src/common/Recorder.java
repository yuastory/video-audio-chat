package common;

import com.xuggle.mediatool.IMediaWriter;

public class Recorder
{
    IMediaWriter writer;
    boolean      audioReady;
    boolean      videoReady;

    public Recorder(IMediaWriter writer)
    {
        this.writer = writer;
        audioReady = false;
        videoReady = false;
    }

    public IMediaWriter getWriter()
    {
        return writer;
    }

    public void setWriter(IMediaWriter writer)
    {
        this.writer = writer;
    }

    public boolean isAudioReady()
    {
        return audioReady;
    }

    public void setAudioReady(boolean audioReady)
    {
        this.audioReady = audioReady;
    }

    public boolean isVideoReady()
    {
        return videoReady;
    }

    public void setVideoReady(boolean videoReady)
    {
        this.videoReady = videoReady;
    }
}
