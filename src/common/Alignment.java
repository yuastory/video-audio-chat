package common;

import java.awt.Insets;

import javax.swing.JFrame;

public class Alignment
{
    public static final int getCenterWidthPos(int frameWidth, int width)
    {
        JFrame temp = new JFrame();
        temp.pack();
        Insets insets = temp.getInsets();
        temp.dispose();
        return ((frameWidth - insets.left - insets.right) / 2) - (width / 2);
    }

    public static final int getCenterHeightPos(int frameHeight, int height)
    {
        JFrame temp = new JFrame();
        temp.pack();
        Insets insets = temp.getInsets();
        temp.dispose();
        return ((frameHeight - insets.top - insets.bottom) / 2) - (height / 2);
    }
}
