import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class Login {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public Login() {
        initialize();
    }

    private void initialize() {
        // 创建主窗口
        frame = new JFrame("中国象棋 - 登录");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLocationRelativeTo(null); // 窗口居中显示
        frame.setResizable(false);
        
        // 创建主面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // 绘制内容区域圆角
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        
        // 内容面板
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        
        // 设置内容面板
        frame.setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(245, 245, 245));
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 20, 20);
                g2d.dispose();
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        // 创建标题标签
        JLabel loginTitle = new JLabel("中国象棋登录系统", JLabel.CENTER);
        loginTitle.setFont(new Font("微软雅黑", Font.BOLD, 24));
        loginTitle.setForeground(new Color(50, 50, 50));
        loginTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // 添加副标题
        JLabel subTitle = new JLabel("欢迎回来，请登录您的账号", JLabel.CENTER);
        subTitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subTitle.setForeground(new Color(120, 120, 120));
        subTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(loginTitle, BorderLayout.NORTH);
        titlePanel.add(subTitle, BorderLayout.CENTER);

        // 创建输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // 用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(80, 80, 80));
        usernameField = new JTextField();
        styleTextField(usernameField, "输入用户名");

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(80, 80, 80));
        passwordField = new JPasswordField();
        stylePasswordField(passwordField, "••••••••");

        // 添加组件到输入面板
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // 登录按钮
        loginButton = new JButton("登 录");
        styleButton(loginButton, new Color(255, 140, 0), true);
        
        // 注册按钮
        registerButton = new JButton("注 册");
        styleButton(registerButton, new Color(255, 255, 255), false);
        registerButton.setForeground(new Color(100, 100, 100));
        
        // 添加按钮到面板
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // 添加按钮到面板
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // 添加所有组件到内容面板
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加内容到内容面板
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 添加内容面板到主面板
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 添加主面板到窗口
        frame.add(mainPanel);
        

        // 添加事件监听器
        addEventListeners();
    }

    private JLabel createSocialIcon(String tooltip, String text) {
        JLabel icon = new JLabel(text, JLabel.CENTER);
        icon.setToolTipText(tooltip);
        icon.setFont(new Font("微软雅黑", Font.BOLD, 14));
        icon.setForeground(new Color(100, 100, 100));
        icon.setOpaque(true);
        icon.setBackground(new Color(240, 240, 240));
        icon.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        icon.setPreferredSize(new Dimension(32, 32));
        icon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                icon.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                icon.setBackground(new Color(240, 240, 240));
            }
        });
        
        return icon;
    }
    
    private void styleButton(JButton button, Color bgColor, boolean isPrimary) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(isPrimary ? Color.WHITE : new Color(80, 80, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(new Color(235, 120, 0));
                } else {
                    button.setBackground(new Color(245, 245, 245));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(new Color(215, 100, 0));
                } else {
                    button.setBackground(new Color(235, 235, 235));
                }
            }
        });
    }
    
    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(new Color(50, 50, 50));
        field.setCaretColor(new Color(255, 140, 0));
        
        // 设置占位符
        if (field.getText().isEmpty()) {
            field.setText(placeholder);
            field.setForeground(new Color(180, 180, 180));
        }
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(50, 50, 50));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 165, 0)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(180, 180, 180));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
    }
    
    private void stylePasswordField(JPasswordField field, String placeholder) {
        styleTextField(field, placeholder);
        field.setEchoChar('•');
    }
    
    
    private void addEventListeners() {
        // 登录按钮事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 输入验证
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "请输入用户名和密码！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 调用数据库验证
                boolean loginSuccess = Database.loginUser(username, password);
                
                if (loginSuccess) {
                    JOptionPane.showMessageDialog(frame, "登录成功！欢迎 " + username, "登录成功", JOptionPane.INFORMATION_MESSAGE);
                    // 这里可以添加跳转到游戏界面的代码
                    frame.dispose(); // 关闭登录窗口
                    Gaming.main(new String[0]); // 打开游戏窗口
                } else {
                    JOptionPane.showMessageDialog(frame, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 注册按钮事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 输入验证
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "请输入用户名和密码！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(frame, "密码长度不能少于6位！", "密码太短", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 调用数据库注册
                boolean registerSuccess = Database.registerUser(username, password);
                
                if (registerSuccess) {
                    JOptionPane.showMessageDialog(frame, "注册成功！请使用新账号登录。", "注册成功", JOptionPane.INFORMATION_MESSAGE);
                    // 清空输入框
                    usernameField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "注册失败！用户名可能已存在。", "注册失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 回车键登录功能
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick(); // 模拟点击登录按钮
            }
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public static void main(String[] args) {
        // 设置Swing界面风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建并显示登录界面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}