package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import client.ClientService;
import common.CustomJFrame;
import common.Room;
import common.SharedPreferences;
import common.User;
import common.UserListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

public class InviteFrame extends CustomJFrame
{
    ClientService          clientService;
    DefaultListModel<User> listModelUser;
    JList<User>            listUser;
    JScrollPane            scrollPane;
    RoomFrame              roomFrame;
    private JLabel lblNewLabel;

    public InviteFrame(ClientService clientService, RoomFrame roomFrame)
    {
        super();
        this.clientService = clientService;
        this.roomFrame = roomFrame;
        setMinimumSize(new Dimension(300, 300));
        setSize(300, 300);
        setLocation(getLocation().x + roomFrame.getWidth() / 2 - getWidth() / 2, getLocation().y + roomFrame.getHeight() / 2 - getHeight() / 2);

        scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        listUser = new JList<>();
        listModelUser = new DefaultListModel<>();
        listUser.setModel(listModelUser);
        listUser.setCellRenderer(new UserListCellRenderer());
        scrollPane.setViewportView(listUser);

        JButton btnInvite = new JButton("초대하기");
        getContentPane().add(btnInvite, BorderLayout.SOUTH);
        
        lblNewLabel = new JLabel("온라인 유저 리스트");
        lblNewLabel.setFont(new Font("Adobe Caslon Pro", Font.PLAIN, 14));
        getContentPane().add(lblNewLabel, BorderLayout.NORTH);
        listUser.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    invite();
                }
            }
        });
        btnInvite.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                invite();
            }
        });
        initLists();
    }

    void invite()
    {
        if (listUser.isSelectionEmpty())
        {
            JOptionPane.showConfirmDialog(InviteFrame.this, "초대할 유저를 선택해주세요.", "", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        int dialogResult = JOptionPane.showConfirmDialog(null, "초대하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION)
        {
            User userSelected = listUser.getSelectedValue();
            System.out.println("selected user: " + userSelected.getUserId());
            new Runnable()
            {
                public void run()
                {
                    clientService.informServer(SharedPreferences.MESSAGE_ROOM_INVITE, userSelected, clientService.getRoomMe());
                }
            }.run();
        }

        dispose();
    }

    void initLists()
    {
        for (User user : clientService.getUsers().values())
        {
            // 본인인 경우 초대 리스트에서 제외
            if (user.getUserId().equals(clientService.getUserMe().getUserId()))
                continue;
            // 대기중인 인원만 표시
            if (!user.isCalling())
                listModelUser.addElement(user);
        }
    }
}
