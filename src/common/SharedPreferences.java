package common;

import java.awt.Color;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class SharedPreferences
{
    // IP
    public static final String VERSION = "v1.0";
    public static final String SERVER_IP        = "127.0.0.1";

    // Port
    public static final int    PORT_SERVER_LOGIN             = 15100;
    public static final int    PORT_CLIENT_SENDER            = 15101;
    public static final int    PORT_SERVER_LISTENER          = 15101;
    public static final int    PORT_SERVER_SENDER            = 15102;
    public static final int    PORT_CLIENT_LISTENER          = 15102;
    public static final int    PORT_VIDEO_DEFAULT            = 15110;
    public static final int    PORT_AUDIO_DEFAULT            = 15140;

    // Info From Users to Server Meesage
    public static final int    MESSAGE_ALIVE                 = 100;
    public static final int    MESSAGE_LOGIN                 = 110;
    public static final int    MESSAGE_LOGIN_DUPLICATED      = 112;
    public static final int    MESSAGE_LOGIN_TIMEOUT         = 114;
    public static final int    MESSAGE_LOGOUT                = 120;
    public static final int    MESSAGE_USER_UPDATE           = 130;
    public static final int    MESSAGE_USERS_UPDATE          = 131;
    public static final int    MESSAGE_ROOM_CREATTION        = 140;
    public static final int    MESSAGE_ROOM_CREATTION_NOTICE = 141;
    public static final int    MESSAGE_ROOM_JOIN             = 142;
    public static final int    MESSAGE_ROOM_JOIN_NOTICE      = 143;
    public static final int    MESSAGE_ROOM_FULL             = 145;
    public static final int    MESSAGE_ROOM_BANNED           = 150;
    public static final int    MESSAGE_ROOM_LEFT             = 155;
    public static final int    MESSAGE_ROOM_UPDATE           = 160;
    public static final int    MESSAGE_ROOM_REMOVAL          = 165;
    public static final int    MESSAGE_ROOM_INVITE           = 170;
    public static final int    MESSAGE_FINISH                = 190;

    // �끃�솕 �궗�씠利�
    public static final int    RECORDER_WIDTH                = 640;
    public static final int    RECORDER_HEIGHT               = 480;
    public static final String FRAME_FORMAT                  = "jpeg";

    // UI
    public static final int    WIDTH_MIN_CONTENTPANE         = 730;
    public static final int    HEIGHT_MIN_CONTENTPANE        = 520;
    // Color
    public static final Color  COLOR_GREEN                   = new Color(155, 187, 89);
    public static final Color  COLOR_GRAY                    = new Color(185, 185, 185);
    public static final Color  COLOR_SKY_BORDER              = new Color(216, 229, 239);
    public static final Color  COLOR_WHITE                   = new Color(255, 255, 255);
    public static final Color  COLOR_SKY_BACKGROUND          = new Color(240, 244, 248);
    public static final Color  COLOR_DARKGRAY_BACKGROUND     = new Color(38, 38, 38);

    public static final Border BORDER_FRAME                  = new LineBorder(COLOR_SKY_BORDER);
    public static final Border BORDER_TEXT_FIELD             = new MatteBorder(0, 0, 1, 0, COLOR_SKY_BORDER);

    // Announcement Signal
    public static final int    ANNOUNCE_NEW_USER             = 555;

    // Time
    public static final String getTime()
    {
        SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
        return f.format(new Date()) + " ";
    }

    public static final void setLibPath()
    {
        String path = System.getProperty("user.dir") + "lib\\opencv\\x" + ((System.getProperty("sun.arch.data.model").equals("32") ? "86" : "64"));
         System.out.println(path);
        System.setProperty("java.library.path", path);
        Field fieldSysPath;
        try
        {
            fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public static final void printError(int errNum, String className)
    {
        System.err.println(getTime() + "Error #" + errNum + " Occurred in " + className);
    }
}
