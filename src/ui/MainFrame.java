package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import client.ClientService;
import common.CustomJFrame;
import common.Room;
import common.RoomListCellRenderer;
import common.SharedPreferences;
import common.User;
import common.UserListCellRenderer;

public class MainFrame extends CustomJFrame
{
    private static final long serialVersionUID = -5988513125942516733L;;
    ClientService             clientService;
    // Components
    JButton                   btnLogout;
    JLabel                    lblUserMe;
    JLabel                    lblIcon;

    JTextField                tfTitle;
    JCheckBox                 chkRecording;
    JComboBox<Integer>        cmbMaxUser;
    JButton                   btnCreateRoom;
    JList<User>               listUser;
    JList<Room>               listRoom;
    DefaultListModel<User>    listModelUser;
    DefaultListModel<Room>    listModelRoom;

    public MainFrame(Point p, ClientService clientService)
    {
        super();
        this.clientService = clientService;
        setLocation(p);
        init();
    }

    public void initLists()
    {
        for (User user : clientService.getUsers().values())
        {
            // 본인은 리스트에서 제외함.
            if (user.getUserId().equals(clientService.getUserMe().getUserId()))
                continue;
            listModelUser.addElement(user);
        }
        for (Room room : clientService.getRooms().values())
            listModelRoom.addElement(room);
    }

    public void removeRoomFromList(Room roomToRemove)
    {
        for (int i = 0; i < listModelRoom.size(); i++)
        {
            Room roomToCompare = listModelRoom.get(i);
            if (roomToCompare.getHost().getUserId().equals(roomToRemove.getHost().getUserId()))
                listModelRoom.removeElementAt(i);
        }
    }

    public void updateRoomLists(Room roomTobeUpdated)
    {
        boolean isUpdated = false;
        for (int i = 0; i < listModelRoom.size(); i++)
        {
            Room roomToCompare = listModelRoom.get(i);
            if (roomToCompare.getHost().getUserId().equals(roomTobeUpdated.getHost().getUserId()))
            {
                listModelRoom.set(i, roomTobeUpdated);
                isUpdated = true;
            }
        }
        if (!isUpdated)
            listModelRoom.addElement(roomTobeUpdated);
    }

    public void updateLists(User userTobeUpdated, Room roomTobeUpdated)
    {
        // 본인일경우
        if (userTobeUpdated.getUserId().equals(clientService.getUserMe().getUserId()))
        {
            if (userTobeUpdated.isCalling())
                lblIcon.setIcon(UserListCellRenderer.calling);
            else
                lblIcon.setIcon(UserListCellRenderer.online);
            return;
        }
        boolean isUpdated = false;
        for (int i = 0; i < listModelUser.size(); i++)
        {
            User userToCompare = listModelUser.get(i);
            if (userToCompare.getUserId().equals(userTobeUpdated.getUserId()))
            {
                listModelUser.set(i, userTobeUpdated);
                isUpdated = true;
            }
        }
        if (!isUpdated)
            listModelUser.addElement(userTobeUpdated);
        if (roomTobeUpdated != null)
            updateRoomLists(roomTobeUpdated);
    }

    public void alertBanned()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JOptionPane.showConfirmDialog(MainFrame.this, "강퇴 당하셨습니다.", "", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public void alertFull()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JOptionPane.showConfirmDialog(MainFrame.this, "정원이 초과하였습니다.", "", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public void alertRoomRemoved()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JOptionPane.showConfirmDialog(MainFrame.this, "방장이 나가서 방이 종료되었습니다.", "", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    void init()
    {
        getContentPane().setLayout(null);
        JPanel panel = new JPanel();
        panel.setBackground(SharedPreferences.COLOR_SKY_BACKGROUND);
        panel.setBorder(SharedPreferences.BORDER_FRAME);
        panel.setBounds(0, 0, 280, 115);
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel label = new JLabel("접속자");
        label.setForeground(SharedPreferences.COLOR_GREEN);
        label.setFont(new Font("굴림", Font.PLAIN, 14));
        label.setBounds(12, 90, 57, 15);
        panel.add(label);

        lblIcon = new JLabel("이미지");
        lblIcon.setIcon(new ImageIcon("res/user_icon.png"));
        lblIcon.setBounds(12, 20, 40, 40);
        panel.add(lblIcon);

        lblUserMe = new JLabel("bana");
        lblUserMe.setFont(new Font("굴림", Font.PLAIN, 14));
        lblUserMe.setBounds(71, 33, 57, 15);
        panel.add(lblUserMe);
        lblIcon.requestFocusInWindow();

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(SharedPreferences.COLOR_WHITE);
        panel_2.setBorder(SharedPreferences.BORDER_FRAME);
        panel_2.setBounds(279, 0, 445, 107);
        getContentPane().add(panel_2);
        panel_2.setLayout(null);

        JLabel label_1 = new JLabel("개설된 방");
        label_1.setForeground(new Color(155, 187, 89));
        label_1.setFont(new Font("굴림", Font.PLAIN, 14));
        label_1.setBounds(12, 46, 67, 15);
        panel_2.add(label_1);

        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setBorder(SharedPreferences.BORDER_FRAME);
        lblNewLabel_1.setBounds(0, 71, 451, 1);
        panel_2.add(lblNewLabel_1);

        btnLogout = new JButton("로그아웃");
        btnLogout.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onDestroy();
            }
        });
        btnLogout.setHorizontalAlignment(SwingConstants.RIGHT);
        btnLogout.setBounds(328, 43, 110, 23);
        btnLogout.setFont(new Font("굴림", Font.PLAIN, 13));
        btnLogout.setIcon(new ImageIcon("res/btn_logout.png"));
        btnLogout.setPressedIcon(new ImageIcon("res/btn_logout_over.png"));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        panel_2.add(btnLogout);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel_1.setBounds(0, 71, 445, 36);
        panel_2.add(panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
        gbl_panel_1.columnWidths = new int[] { 35, 290, 20, 80 };
        gbl_panel_1.rowHeights = new int[] { 0, 0 };
        gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        JLabel lblRecording = new JLabel("녹화");
        GridBagConstraints gbc_lblRecording = new GridBagConstraints();
        gbc_lblRecording.insets = new Insets(0, 0, 0, 5);
        gbc_lblRecording.gridx = 0;
        gbc_lblRecording.gridy = 0;
        panel_1.add(lblRecording, gbc_lblRecording);

        JLabel lblTitle = new JLabel("제목");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.insets = new Insets(0, 0, 0, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 0;
        panel_1.add(lblTitle, gbc_lblTitle);

        JLabel lblNumOfUsers = new JLabel("인원");
        lblNumOfUsers.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblNumOfUsers = new GridBagConstraints();
        gbc_lblNumOfUsers.insets = new Insets(0, 0, 0, 5);
        gbc_lblNumOfUsers.gridx = 2;
        gbc_lblNumOfUsers.gridy = 0;
        panel_1.add(lblNumOfUsers, gbc_lblNumOfUsers);

        JLabel lblHostId = new JLabel("방장");
        lblHostId.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_lblHostId = new GridBagConstraints();
        gbc_lblHostId.gridx = 3;
        gbc_lblHostId.gridy = 0;
        panel_1.add(lblHostId, gbc_lblHostId);

        JScrollPane scrollPaneRoom = new JScrollPane();
        scrollPaneRoom.setBorder(SharedPreferences.BORDER_FRAME);
        listRoom = new JList<>();
        listModelRoom = new DefaultListModel<>();
        listRoom.setModel(listModelRoom);
        listRoom.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    Room room = listRoom.getSelectedValue();
                    clientService.informServer(SharedPreferences.MESSAGE_ROOM_JOIN, null, room);
                }
            }
        });
        listRoom.setCellRenderer(new RoomListCellRenderer());
        scrollPaneRoom.setViewportView(listRoom);
        scrollPaneRoom.setBounds(279, 106, 445, 293);
        getContentPane().add(scrollPaneRoom);

        JPanel panel_4 = new JPanel();
        panel_4.setBackground(SharedPreferences.COLOR_WHITE);
        panel_4.setBorder(SharedPreferences.BORDER_FRAME);
        panel_4.setBounds(279, 398, 445, 93);
        getContentPane().add(panel_4);
        panel_4.setLayout(null);

        JLabel label_4 = new JLabel("제목");
        label_4.setForeground(new Color(155, 187, 89));
        label_4.setFont(new Font("굴림", Font.PLAIN, 13));
        label_4.setBounds(12, 24, 33, 15);
        panel_4.add(label_4);

        JLabel label_5 = new JLabel("영상 저장");
        label_5.setForeground(new Color(155, 187, 89));
        label_5.setFont(new Font("굴림", Font.PLAIN, 13));
        label_5.setBounds(12, 49, 64, 15);
        panel_4.add(label_5);

        tfTitle = new JTextField();
        tfTitle.setBorder(SharedPreferences.BORDER_TEXT_FIELD);
        tfTitle.setBounds(57, 21, 207, 21);
        panel_4.add(tfTitle);
        tfTitle.setColumns(10);

        JLabel label_6 = new JLabel("최대 인원");
        label_6.setForeground(new Color(155, 187, 89));
        label_6.setFont(new Font("굴림", Font.PLAIN, 13));
        label_6.setBounds(147, 49, 64, 15);
        panel_4.add(label_6);

        chkRecording = new JCheckBox("");
        chkRecording.setFont(new Font("굴림", Font.PLAIN, 13));
        chkRecording.setBackground(new Color(255, 255, 255));
        chkRecording.setBounds(78, 45, 31, 23);
        panel_4.add(chkRecording);

        Integer[] menu = { 2, 3, 4, 5, 6 };
        // cmbMaxUser = new JComboBox<>(menu);
        cmbMaxUser = new JComboBox(menu);
        cmbMaxUser.setFont(new Font("굴림", Font.PLAIN, 13));
        cmbMaxUser.setBackground(new Color(255, 255, 255));
        cmbMaxUser.setBounds(223, 46, 41, 21);
        panel_4.add(cmbMaxUser);

        btnCreateRoom = new JButton("");
        btnCreateRoom.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (tfTitle.getText().length() == 0)
                {
                    JOptionPane.showConfirmDialog(null, "제목을 입력해주세요.", "waveware", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                clientService.informServer(SharedPreferences.MESSAGE_ROOM_CREATTION, clientService.getUserMe(), null);
            }
        });
        btnCreateRoom.setIcon(new ImageIcon("res/btn_create_room.png"));
        btnCreateRoom.setBounds(318, 24, 93, 36);
        btnCreateRoom.setFont(new Font("굴림", Font.PLAIN, 14));
        btnCreateRoom.setPressedIcon(new ImageIcon("res/btn_create_room_over.png"));
        btnCreateRoom.setBorderPainted(false);
        btnCreateRoom.setFocusPainted(false);
        btnCreateRoom.setContentAreaFilled(false);
        panel_4.add(btnCreateRoom);

        JScrollPane scrollPaneUser = new JScrollPane();
        scrollPaneUser.setBorder(SharedPreferences.BORDER_FRAME);
        scrollPaneUser.setBounds(0, 114, 280, 377);
        getContentPane().add(scrollPaneUser);

        listUser = new JList<>();
        listModelUser = new DefaultListModel<>();
        listUser.setModel(listModelUser);
        listUser.setCellRenderer(new UserListCellRenderer());
        scrollPaneUser.setViewportView(listUser);
    }

    public void onDestroy()
    {
        clientService.informServer(SharedPreferences.MESSAGE_LOGOUT, clientService.getUserMe(), null);
        System.out.println(SharedPreferences.getTime() + "Logged out.");
        dispose();
    }

    public JTextField getTfTitle()
    {
        return tfTitle;
    }

    public void setTfTitle(JTextField tfTitle)
    {
        this.tfTitle = tfTitle;
    }

    public JCheckBox getChkRecording()
    {
        return chkRecording;
    }

    public void setChkRecording(JCheckBox chkRecording)
    {
        this.chkRecording = chkRecording;
    }

    public JComboBox getCmbMaxUser()
    {
        return cmbMaxUser;
    }

    public void setCmbMaxUser(JComboBox cmbMaxUser)
    {
        this.cmbMaxUser = cmbMaxUser;
    }

    public DefaultListModel<Room> getListModelRoom()
    {
        return listModelRoom;
    }

    public void setListModelRoom(DefaultListModel<Room> listModelRoom)
    {
        this.listModelRoom = listModelRoom;
    }

    public DefaultListModel<User> getListModelUser()
    {
        return listModelUser;
    }

    public void setListModelUser(DefaultListModel<User> listModelUser)
    {
        this.listModelUser = listModelUser;
    }

    public JLabel getLblUserMe()
    {
        return lblUserMe;
    }

    public void setLblUserMe(JLabel lblUserMe)
    {
        this.lblUserMe = lblUserMe;
    }
}
