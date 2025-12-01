import javax.swing.*;
import java.awt.*;

public class GameFrame implements Runnable {
    @Override
    public void run() {
        // 在Swing事件调度线程中创建和显示UI
        SwingUtilities.invokeLater(() -> {
            // 创建主窗口
            JFrame frame = new JFrame("象棋");
            frame.setSize(1280, 720);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // 窗口居中
            frame.setLayout(new BorderLayout(10, 10));
            
            // 显示窗口
            frame.setVisible(true);
        });
    }
    
    // 保留原有main方法以兼容单独运行
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        gameFrame.run();
    }
}