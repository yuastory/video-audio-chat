package common;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("rawtypes")
public class CustomCellRenderer extends JLabel implements ListCellRenderer
{
    public CustomCellRenderer()
    {
    }

    private static final long serialVersionUID = 5693026846329516693L;
    final static ImageIcon    online         = new ImageIcon("res/icon.png");
    final static ImageIcon    calling        = new ImageIcon("res/user_icon.png");

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.
    public Component getListCellRendererComponent(JList list, // the list
            Object value, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // does the cell have focus
    {
        String s = value.toString();
        setText(s);
        setIcon((s.length() > 10) ? online : calling);
        setBorder(new EmptyBorder(5, 15, 5, 0));
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
