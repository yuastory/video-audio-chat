package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import client.ClientService;
import common.Alignment;
import common.CustomJFrame;
import common.CustomJTextField;
import common.SharedPreferences;

public class LoginFrame extends CustomJFrame
{
    private static final long serialVersionUID = 2333228960066179698L;
    JTextField                tf_id;
    JLabel                    lblAlert;
    ClientService             clientService;

    public LoginFrame()
    {
        super();
        JPanel panel = new JPanel();
        panel.setForeground(Color.RED);
        panel.setLayout(null);
        panel.setBackground(SharedPreferences.COLOR_WHITE);
        getContentPane().add(panel);

        JLabel lblNewLabel = new JLabel();
        lblNewLabel.setBounds(0, 0, SharedPreferences.WIDTH_MIN_CONTENTPANE, 1);
        lblNewLabel.setBorder(SharedPreferences.BORDER_FRAME);
        panel.add(lblNewLabel);

        JLabel lbl_logo = new JLabel();
        lbl_logo.setBounds(184, 102, 348, 109);
        lbl_logo.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, lbl_logo.getWidth()), lbl_logo.getY());
        panel.add(lbl_logo);
        lbl_logo.requestFocusInWindow();

        JLabel lbl1 = new JLabel("실시간 영상 커뮤니케이션 시스템");
        lbl1.setFont(new Font("굴림", Font.PLAIN, 18));
        lbl1.setBounds(246, 213, 270, 29);
        lbl1.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, lbl1.getWidth()), lbl1.getY());
        panel.add(lbl1);

        JLabel lbl2 = new JLabel("아이디와 비밀번호를 입력해주세요");
        lbl2.setFont(new Font("굴림", Font.PLAIN, 14));
        lbl2.setBounds(255, 252, 226, 15);
        lbl2.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, lbl2.getWidth()), lbl2.getY());
        panel.add(lbl2);

        // 아이디 비밀번호 패널
        JPanel paneIdPw = new JPanel();
        paneIdPw.setBackground(SharedPreferences.COLOR_WHITE);
        paneIdPw.setBounds(246, 288, 255, 58);
        paneIdPw.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, paneIdPw.getWidth()), paneIdPw.getY());
        panel.add(paneIdPw);
        paneIdPw.setLayout(new GridLayout(2, 0, 0, 0));

        // 아이디
        tf_id = new CustomJTextField("아이디");
        tf_id.setFont(new Font("굴림", Font.PLAIN, 14));
        tf_id.setColumns(20);
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                tf_id.requestFocus();
            }
        });
        paneIdPw.add(tf_id);

        lblAlert = new JLabel();
        lblAlert.setForeground(Color.RED);
        lblAlert.setHorizontalAlignment(SwingConstants.CENTER);
        paneIdPw.add(lblAlert);

        // 로그인 버튼
        JButton btn_login = new JButton("");
        getRootPane().setDefaultButton(btn_login);
        btn_login.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String id = tf_id.getText();
                if (id.length() > 0 && id.length() < 10)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            clientService = new ClientService(LoginFrame.this);
                            lblAlert.setText("");
                            int message = clientService.login(id, getLocation());
                            // Login Success
                            if (message == SharedPreferences.MESSAGE_LOGIN_DUPLICATED)
                                lblAlert.setText("이미 사용 중인 아이디입니다.");
                            else if (message == SharedPreferences.MESSAGE_LOGIN_TIMEOUT)
                                lblAlert.setText("서버에 접속할 수 없습니다.");
                            else
                            {
                                tf_id.setText("");
                                lblAlert.setText("");
                                setVisible(false);
                            }
                        }
                    });
                }
            }
        });
        btn_login.setIcon(new ImageIcon("res/btn_login.png"));
        btn_login.setForeground(SharedPreferences.COLOR_GREEN);
        btn_login.setFont(new Font("굴림", Font.PLAIN, 14));
        btn_login.setPressedIcon(new ImageIcon("res/btn_login_over.png"));
        btn_login.setBorderPainted(false);
        btn_login.setFocusPainted(false);
        btn_login.setContentAreaFilled(false);
        btn_login.setBounds(202, 346, 326, 38);
        btn_login.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, btn_login.getWidth()), btn_login.getY());
        panel.add(btn_login);

        JLabel lbl3 = new JLabel("별도의 회원가입은 필요하지 않습니다.");
        lbl3.setForeground(SharedPreferences.COLOR_GREEN);
        lbl3.setFont(new Font("굴림", Font.PLAIN, 13));
        lbl3.setBounds(252, 394, 226, 15);
        lbl3.setLocation(Alignment.getCenterWidthPos(getPreferredSize().width, lbl3.getWidth()), lbl3.getY());
        panel.add(lbl3);
    }

    public void serverClosed()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JOptionPane.showConfirmDialog(getContentPane(), "서버가 종료되었습니다.", "", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public void onDestroy()
    {
        System.out.println(SharedPreferences.getTime() + "Closing Login Frame..");
        System.exit(0);
    }
}
