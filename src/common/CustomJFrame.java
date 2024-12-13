package common;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class CustomJFrame extends JFrame
{
    private static final long serialVersionUID = -8701114778553392066L;

    public CustomJFrame()
    {
        super();
        setIconImage(Toolkit.getDefaultToolkit().getImage("res/icon.png"));
        setTitle("Realtime Media Communication " + SharedPreferences.VERSION);
        setBackground(SharedPreferences.COLOR_WHITE);
        setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - SharedPreferences.WIDTH_MIN_CONTENTPANE / 2, screenSize.height / 2 - SharedPreferences.HEIGHT_MIN_CONTENTPANE / 2);
        setSize(new Dimension(SharedPreferences.WIDTH_MIN_CONTENTPANE, SharedPreferences.HEIGHT_MIN_CONTENTPANE));
        setPreferredSize(new Dimension(SharedPreferences.WIDTH_MIN_CONTENTPANE, SharedPreferences.HEIGHT_MIN_CONTENTPANE));
        setMinimumSize(new Dimension(SharedPreferences.WIDTH_MIN_CONTENTPANE, SharedPreferences.HEIGHT_MIN_CONTENTPANE));
        pack();
        // setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // 종료시
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                onDestroy();
            }
        });

    }

    // closing button
    public void onDestroy()
    {
        dispose();
    }
}
