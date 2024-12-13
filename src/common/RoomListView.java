package common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class RoomListView extends JPanel
{
    protected JLabel lblRecording;
    protected JLabel lblTitle;
    protected JLabel lblNumOfUsers;
    protected JLabel lblHostId;

    public RoomListView()
    {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]  { 40, 290, 20, 80 };
        gridBagLayout.rowHeights = new int[] { 36, 0 };
        gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        lblRecording = new JLabel("");
        GridBagConstraints gbc_lblRecording = new GridBagConstraints();
        gbc_lblRecording.insets = new Insets(0, 0, 0, 5);
        gbc_lblRecording.gridx = 0;
        gbc_lblRecording.gridy = 0;
        add(lblRecording, gbc_lblRecording);

        lblTitle = new JLabel("title");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.insets = new Insets(0, 0, 0, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 0;
        add(lblTitle, gbc_lblTitle);

        lblNumOfUsers = new JLabel("1/ 6");
        lblNumOfUsers.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.gridx = 2;
        gbc_label.gridy = 0;
        add(lblNumOfUsers, gbc_label);

        lblHostId = new JLabel("Bana");
        lblHostId.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblBana = new GridBagConstraints();
        gbc_lblBana.gridx = 3;
        gbc_lblBana.gridy = 0;
        add(lblHostId, gbc_lblBana);
    }
}
