package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import client.ClientService;
import client.MediaInOutManager;
import common.CustomJFrame;
import common.SharedPreferences;
import common.User;
import video.VideoCap;

public class RoomFrame extends CustomJFrame
{
    private static final long serialVersionUID = 2333228960066179698L;
    VideoCap                  videoCap;
    MediaInOutManager         mediaInOutManager;
    ClientService             clientService;
    boolean                   fullScreen       = false;

    // Components
    JLabel                    lblTitle, lblRecording, lblTitleBarUnderLine;
    JPanel                    panelGrid, panelBtn;
    JPanel[]                  panels;
    JPanel[]                  panelWebcams;
    JLabel[]                  lblUserIds;
    MousePopupListener[]      mousePopupListeners;
    InviteFrame               inviteFrame      = null;

    public RoomFrame(Point p, ClientService clientService)
    {
        super();
        this.clientService = clientService;
        setLocation(p);
        mediaInOutManager = new MediaInOutManager(clientService.getUserMe().equals(clientService.getRoomMe().getHost()) && clientService.getRoomMe().isRecording());
        init();
        setRoom();
        videoCap = new VideoCap();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                mediaInOutManager.openAudioListener(SharedPreferences.PORT_AUDIO_DEFAULT);
                openWebcam();
            }
        });
    }

    /**
     * @param user  방에 입장한 유저
     */
    public void joinUser(User user)
    {
        if (user.getUserId().equals(clientService.getUserMe().getUserId()))
            openWebcam();
        else
            addUser(user);
    }

    /**
     * @param user  나간 유저와 소켓을 끊음
     */
    public void removeUser(User user)
    {
        clientService.getRoomMe().removeUser(user);
        mediaInOutManager.closeVideoSocket(user.getUserId());
        panels[user.getPosition()].getGraphics().setColor(panels[user.getPosition()].getBackground());
        // panelWebcams[user.getPosition()].setVisible(false);
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                panels[user.getPosition()].setVisible(false);
            }
        });
    }

    /**
     * 추가되는 인원에 대한 웹캠을 열어줌
     */
    void addUser(User user)
    {
        int position = user.getPosition();
        lblUserIds[position].setText(user.getUserId());
        lblUserIds[position].revalidate();
        if (user.getUserId().equals(clientService.getUserMe().getUserId()))// 현재 유저인 경우 리턴
            return;
        System.out.println(SharedPreferences.getTime() + "Getting " + user.getUserId() + "'s webcam...");

        // webcam 통신 추가
        int videoListeningPort = SharedPreferences.PORT_VIDEO_DEFAULT + position;
        int videoCallingPort = SharedPreferences.PORT_VIDEO_DEFAULT + clientService.getUserMe().getPosition();
        int audioCallingPort = SharedPreferences.PORT_AUDIO_DEFAULT;
        mediaInOutManager.openMediaSocket(user, videoCap, videoCallingPort, videoListeningPort, audioCallingPort, panelWebcams[position]);
        // mousePopupListeners[position].active = true;
        panels[position].setVisible(true);
        System.out.println(SharedPreferences.getTime() + user.getUserId() + "'s webcam opened.");
    }

    /**
     * 방 제목이나 녹화여부에 대한 ui표시를 해줌
     */
    void setRoom()
    {
        lblTitle.setText(clientService.getRoomMe().getTitle());
        lblRecording.setText(clientService.getRoomMe().isRecording() ? "녹화중" : "");
    }

    /**
     * 최초 시작시 접속해 있는 인원에 대한 웹캠 포트를 열어줌 ( 처음 입장할 경우 자신의 화면만 보임 )
     */
    void openWebcam()
    {
        System.out.println(SharedPreferences.getTime() + "Start open webcams from other users");
        for (String userId : clientService.getRoomMe().getUsers())
        {
            new Runnable()
            {
                public void run()
                {
                    if (userId == null)
                        return;
                    User tempUser = clientService.getUsers().get(userId);
                    int position = tempUser.getPosition();
                    if (userId.equals(clientService.getUserMe().getUserId()))
                    {
                        System.out.println(SharedPreferences.getTime() + "host webcam setting.");
                        panels[position].remove(panelWebcams[position]);

                        panelWebcams[position] = new JPanel()
                        {
                            @Override
                            public void paint(Graphics g)
                            {
                                super.paint(g);
                                try
                                {
                                    BufferedImage img = videoCap.getOneFrame();
                                    g.drawImage(img, 0, 0, panelWebcams[position].getWidth(), panelWebcams[position].getHeight(), this);
                                }
                                catch (Exception e)
                                {
                                    // SharedPreferences.printError(4100, RoomFrame.class.getName());
                                }
                            }
                        };
                        panels[position].add(panelWebcams[position]);

                        // 웹캠을 지속적으로 그려줌
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                while (clientService.getUserMe().isCalling())
                                {
                                    panelWebcams[clientService.getUserMe().getPosition()].repaint();
                                    try
                                    {
                                        Thread.sleep(30);
                                    }
                                    catch (InterruptedException e)
                                    {
                                    }
                                }
                            }

                        }).start();
                    }
                    addUser(tempUser);
                    panels[position].revalidate();
                    panels[position].setVisible(true);

                }
            }.run();
        }
    }

    /**
     * 화면을 그려줌
     */
    void init()
    {
        setResizable(true);
        // 전체화면시
        this.addWindowStateListener(new WindowStateListener()
        {
            @Override
            public void windowStateChanged(WindowEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        fullScreen = !fullScreen;
                        System.out.println("full screen");
                         Dimension winSize = Toolkit.getDefaultToolkit().getScreenSize();
                         panelGrid.setSize(winSize.width - (lblTitle.getX() * 2), winSize.height - panelGrid.getY() - panelBtn.getHeight());
//                        panelGrid.setSize(getContentPane().getWidth() - (lblTitle.getX() * 2), getContentPane().getHeight() - panelGrid.getY() - panelBtn.getHeight());
                    }
                });
            }
        });

        // 종료시
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                onClose();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
        getContentPane().add(panel);
        lblTitleBarUnderLine = new JLabel("");
        lblTitleBarUnderLine.setBounds(0, 0, SharedPreferences.WIDTH_MIN_CONTENTPANE, 1);
        lblTitleBarUnderLine.setBorder(SharedPreferences.BORDER_FRAME);
        panel.add(lblTitleBarUnderLine);

        lblTitle = new JLabel("방 제목");
        lblTitle.setForeground(SharedPreferences.COLOR_WHITE);
        lblTitle.setFont(new Font("굴림", Font.PLAIN, 18));
        lblTitle.setBounds(10, 10, 390, 28);
        panel.add(lblTitle);

        lblRecording = new JLabel("녹화중");
        lblRecording.setForeground(Color.RED);
        lblRecording.setFont(new Font("굴림", Font.PLAIN, 14));
        lblRecording.setBounds(lblTitle.getX(), lblTitle.getY() + lblTitle.getHeight(), 226, 28);
        panel.add(lblRecording);

        panelBtn = new JPanel();
        panelBtn.setBackground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
        panelBtn.setSize(160, 60);
        panelBtn.setLocation(getContentPane().getWidth() / 2 - panelBtn.getWidth() / 2, getContentPane().getHeight() - panelBtn.getHeight());
        panel.add(panelBtn);

        JButton btnInvite = new JButton();
        btnInvite.setIcon(new ImageIcon("res/btn_plus.png"));
        btnInvite.setPressedIcon(new ImageIcon("res/btn_plus_over.png"));
        btnInvite.setBorderPainted(false);
        btnInvite.setFocusPainted(false);
        btnInvite.setContentAreaFilled(false);
        btnInvite.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (inviteFrame != null)
                    inviteFrame.dispose();
                inviteFrame = new InviteFrame(clientService, RoomFrame.this);
                inviteFrame.setVisible(true);
            }
        });
        panelBtn.add(btnInvite);

        JButton btnHangup = new JButton();
        btnHangup.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        onClose();
                    }
                });
            }
        });
        btnHangup.setIcon(new ImageIcon("res/btn_hangup.png"));
        btnHangup.setPressedIcon(new ImageIcon("res/btn_hangup_over.png"));
        btnHangup.setBorderPainted(false);
        btnHangup.setFocusPainted(false);
        btnHangup.setContentAreaFilled(false);
        panelBtn.add(btnHangup);

        panelGrid = new JPanel();
        panelGrid.setBackground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
        // 인원수에 맞게 행렬 갯수 재지정
        int row = clientService.getRoomMe().getMaxUserNum() % 3 == 0 ? clientService.getRoomMe().getMaxUserNum() / 3 : clientService.getRoomMe().getMaxUserNum() / 3 + 1;
        int col = clientService.getRoomMe().getMaxUserNum() % 2 == 1 ? 3 : 2;

        panelGrid.setLayout(new GridLayout(row, col, 10, 10));
        panelGrid.setLocation(lblTitle.getX(), lblRecording.getY() + lblRecording.getHeight());
        panelGrid.setSize(getContentPane().getWidth() - (lblTitle.getX() * 2), getContentPane().getHeight() - panelGrid.getY() - panelBtn.getHeight());
        panels = new JPanel[clientService.getRoomMe().getMaxUserNum()];
        panelWebcams = new JPanel[clientService.getRoomMe().getMaxUserNum()];
        lblUserIds = new JLabel[clientService.getRoomMe().getMaxUserNum()];
        mousePopupListeners = new MousePopupListener[clientService.getRoomMe().getMaxUserNum()];

        // 웹캠 패널 6개
        for (int i = 0; i < clientService.getRoomMe().getMaxUserNum(); i++)
        {
            panels[i] = new JPanel();
            panels[i].setOpaque(false);
            panels[i].setForeground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
            panels[i].setLayout(new BorderLayout(0, 0));
            mousePopupListeners[i] = new MousePopupListener(i, false, clientService.getRoomMe().getHost().getUserId().equals(clientService.getUserMe().getUserId()));
            panels[i].addMouseListener(mousePopupListeners[i]);
            panels[i].setVisible(false);

            panelWebcams[i] = new JPanel();
            panelWebcams[i].setLayout(null);
            panelWebcams[i].setBackground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
            panels[i].add(panelWebcams[i], BorderLayout.CENTER);

            lblUserIds[i] = new JLabel("bana" + i);
            lblUserIds[i].setFont(new Font("굴림", Font.PLAIN, 18));
            lblUserIds[i].setForeground(SharedPreferences.COLOR_WHITE);
            lblUserIds[i].setHorizontalAlignment(SwingConstants.CENTER);
            lblUserIds[i].setBackground(SharedPreferences.COLOR_DARKGRAY_BACKGROUND);
            panels[i].add(lblUserIds[i], BorderLayout.SOUTH);
            panelGrid.add(panels[i]);
        }

        panel.add(panelGrid);
    }

    class MousePopupListener extends MouseAdapter
    {
        int     position;
        boolean active;
        boolean host;

        public MousePopupListener(int position, boolean active, boolean host)
        {
            this.position = position;
            this.active = active;
            this.host = host;
        }

        public void mousePressed(MouseEvent e)
        {
            checkPopup(e);
        }

        public void mouseClicked(MouseEvent e)
        {
            checkPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e)
        {
            if (active && host)
            {
                PopUp menu = new PopUp();
                // menu.show(RoomFrame.this, e.getXOnScreen(), e.getYOnScreen());
                menu.show(RoomFrame.this, e.getX(), e.getY());
            }
        }
    }

    class PopUp extends JPopupMenu
    {
        int position;

        public PopUp(int position)
        {
            this.position = position;
        }

        JMenuItem anItem;

        public PopUp()
        {
            anItem = new JMenuItem("추방");
            ActionListener menuListener = new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "강퇴하시겠습니까?", "", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION)
                    {
                        String id = clientService.getRoomMe().getUsers()[position];
                        System.out.println("id : " + id);
                    }

                }
            };
            anItem.addActionListener(menuListener);
            add(anItem);
        }
    }

    // An inner class to show when popup events occur
    class PopupPrintListener implements PopupMenuListener
    {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
            System.out.println("Popup menu will be visible!");
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
        {
            System.out.println("Popup menu will be invisible!");
        }

        public void popupMenuCanceled(PopupMenuEvent e)
        {
            System.out.println("Popup menu is hidden!");
        }
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        if (!fullScreen)
        {
            /* 리사이징 */
            lblTitleBarUnderLine.setSize(new Dimension(getContentPane().getWidth(), lblTitleBarUnderLine.getHeight()));
            panelBtn.setLocation(getContentPane().getWidth() / 2 - panelBtn.getWidth() / 2, getContentPane().getHeight() - panelBtn.getHeight());
            panelGrid.setSize(getContentPane().getWidth() - (lblTitle.getX() * 2), getContentPane().getHeight() - panelGrid.getY() - panelBtn.getHeight());
            /* */
        }
    }

    public void onClose()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                onDestroy();
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        new Runnable()
        {
            public void run()
            {
                // 호스트일경우 방 삭제
                if (clientService.getUserMe().getUserId().equals(clientService.getRoomMe().getHost().getUserId()))
                    clientService.informServer(SharedPreferences.MESSAGE_ROOM_REMOVAL, clientService.getUserMe(), clientService.getRoomMe());
                // 유저일경우 방 종료
                else
                    clientService.informServer(SharedPreferences.MESSAGE_ROOM_LEFT, clientService.getUserMe(), clientService.getRoomMe());
            }
        }.run();

    }

    /**
     * 방을 나감
     */
    public void onDestroy()
    {
        System.out.println(SharedPreferences.getTime() + "try to destroy");
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                mediaInOutManager.closeAllVideoSocket();
            }
        });
        t.start();
        System.out.println(SharedPreferences.getTime() + "try to wait");
        try
        {
            t.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println(SharedPreferences.getTime() + "try to close cap");
        videoCap.cap.release();
        if (inviteFrame != null)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    inviteFrame.dispose();
                }
            });
        }
    }
}
