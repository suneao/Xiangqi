import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("象棋登录");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 窗口居中
        frame.setLayout(new BorderLayout(10, 10));

        // 创建顶部标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // 顶部边距

        // 大标题
        JLabel mainTitle = new JLabel("象棋");
        mainTitle.setFont(new Font("宋体", Font.BOLD, 36));
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 小标题
        JLabel subTitle = new JLabel("登录");
        subTitle.setFont(new Font("宋体", Font.PLAIN, 20));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(mainTitle);
        titlePanel.add(Box.createVerticalStrut(10)); // 标题间距
        titlePanel.add(subTitle);

        // 创建输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50)); // 边距

        // 账号输入
        JLabel usernameLabel = new JLabel("账号:");
        JTextField usernameField = new JTextField();

        // 密码输入
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(100, 30));

        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // 简单的登录验证逻辑
                if (!username.isEmpty() && !password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "登录成功！");
                } else {
                    JOptionPane.showMessageDialog(frame, "请输入账号和密码！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(loginButton);

        // 添加所有面板到主窗口
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 显示窗口
        frame.setVisible(true);
    }
}