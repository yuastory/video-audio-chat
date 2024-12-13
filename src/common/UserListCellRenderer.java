package common;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class UserListCellRenderer extends JLabel implements ListCellRenderer<User>
{
    public UserListCellRenderer()
    {
    }

    private static final long     serialVersionUID = 5693026846329516693L;
    public final static ImageIcon online           = new ImageIcon("res/user_icon.png");
    public final static ImageIcon calling          = new ImageIcon("res/user_icon_calling.png");

    public Component getListCellRendererComponent(JList<? extends User> list, // the list
            User user, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // does the cell have focus
    {
        String userId = user.getUserId();
        setText(userId);
        setIcon(user.isCalling() ? calling : online);
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
