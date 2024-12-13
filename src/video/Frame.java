package video;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Frame implements Serializable
{
    private static final long serialVersionUID = 5501L;
    public String             ip;
    public byte[]             bytes;

    public Frame(byte[] bytes)
    {
        try
        {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        this.bytes = bytes;
    }

    public int size()
    {
        return bytes.length;
    }

}
