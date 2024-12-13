package common;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RoomListCellRenderer extends RoomListView implements ListCellRenderer<Room>
{
    
    final static ImageIcon    recording        = new ImageIcon("res/btn_recording.png");
    final static ImageIcon    calling        = new ImageIcon("res/btn_calling.png");

    // This is the only method defined by ListCellRenderer.
    // We just reconfigure the JLabel each time we're called.
    public Component getListCellRendererComponent(JList<? extends Room> list, // the list
            Room room, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // does the cell have focus
    {
        lblRecording.setIcon(room.isRecording() ? recording : calling);
        lblHostId.setText(room.getHost().getUserId());
        lblTitle.setText(room.getTitle());
        lblTitle.setMaximumSize(new Dimension(290, 36));
        lblNumOfUsers.setText(room.getCurrentUserNum() + "/" + room.getMaxUserNum());
//        setBorder(new EmptyBorder(5, 15, 5, 0));
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
