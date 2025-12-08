import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameFrame implements Runnable {
    private JFrame frame;
    private Timer detectionTimer;
    private JPanel chessBoardPanel;
    private BufferedImage woodTexture;
    
    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("象棋");
            frame.setSize(1280, 720);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            
            JLayeredPane layeredPane = new JLayeredPane();
            frame.setContentPane(layeredPane);
            
            createChessBoardPanel();
            chessBoardPanel.setBounds(0, 0, 1280, 720);
            layeredPane.add(chessBoardPanel, JLayeredPane.DEFAULT_LAYER);
            
            createButtonPanel(layeredPane);
            
            startDetectionTimer();
            
            frame.setVisible(true);
        });
    }
    
    private void createChessBoardPanel() {
        try {
            woodTexture = ImageIO.read(new File("content/Map/Wood-texture.png"));
            
            chessBoardPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    int boardSize = Math.min(getWidth(), getHeight()) * 7 / 10;
                    int x = (getWidth() - boardSize) / 2;
                    int y = (getHeight() - boardSize) / 2;
                    
                    int margin = boardSize / 10;
                    int textureX = x - margin;
                    int textureY = y - margin;
                    int textureWidth = boardSize + 2 * margin;
                    int textureHeight = boardSize + 2 * margin;
                    
                    int arc = 20;
                    RoundRectangle2D textureRectangle = new RoundRectangle2D.Float(
                        textureX, textureY, textureWidth, textureHeight, arc, arc);
                    
                    Shape originalClip = g2d.getClip();
                    g2d.setClip(textureRectangle);
                    
                    if (woodTexture != null) {
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, 0.25f);
                        g2d.setComposite(alphaComposite);
                        
                        TexturePaint texture = new TexturePaint(
                            woodTexture, 
                            new Rectangle(textureX, textureY, woodTexture.getWidth(), woodTexture.getHeight())
                        );
                        g2d.setPaint(texture);
                        g2d.fillRect(textureX, textureY, textureWidth, textureHeight);
                        
                        g2d.setComposite(AlphaComposite.SrcOver);
                    }
                    
                    g2d.setClip(originalClip);
                    
                    drawChessBoard(g2d, x, y, boardSize);
                    
                    // 绘制棋盘边缘线（在棋子下面）
                    g2d.setColor(new Color(139, 0, 0)); // 暗红色
                    g2d.setStroke(new BasicStroke(3));
                    g2d.draw(textureRectangle);
                    
                    // 绘制所有棋子（在棋盘边缘线上面）
                    drawAllPieces(g2d, x, y, boardSize);
                    
                    g2d.dispose();
                }
                
                private void drawChessBoard(Graphics2D g2d, int x, int y, int boardSize) {
                    g2d.setColor(new Color(139, 0, 0)); // 暗红色
                    g2d.setStroke(new BasicStroke(2));
                    
                    int cellWidth = boardSize / 8;
                    int cellHeight = boardSize / 9;
                    
                    g2d.drawRect(x, y, boardSize, boardSize);
                    
                    for (int i = 1; i < 9; i++) {
                        int lineY = y + i * cellHeight;
                        g2d.drawLine(x, lineY, x + boardSize, lineY);
                    }
                    
                    for (int i = 1; i < 8; i++) {
                        int lineX = x + i * cellWidth;
                        g2d.drawLine(lineX, y, lineX, y + 4 * cellHeight);
                        g2d.drawLine(lineX, y + 5 * cellHeight, lineX, y + 9 * cellHeight);
                    }
                    
                    // 绘制九宫格斜线
                    g2d.drawLine(x + 3 * cellWidth, y, x + 5 * cellWidth, y + 2 * cellHeight);
                    g2d.drawLine(x + 5 * cellWidth, y, x + 3 * cellWidth, y + 2 * cellHeight);
                    
                    g2d.drawLine(x + 3 * cellWidth, y + 7 * cellHeight, x + 5 * cellWidth, y + 9 * cellHeight);
                    g2d.drawLine(x + 5 * cellWidth, y + 7 * cellHeight, x + 3 * cellWidth, y + 9 * cellHeight);
                    
                    // 绘制楚河汉界
                    g2d.setFont(new Font("宋体", Font.BOLD, 24));
                    g2d.setColor(new Color(139, 69, 19));
                    String text = "楚 河          汉 界";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textX = x + (boardSize - textWidth) / 2;
                    int textY = y + 4 * cellHeight + cellHeight / 2 + fm.getAscent() / 2;
                    g2d.drawString(text, textX, textY);
                }
            };
            
            chessBoardPanel.setPreferredSize(new Dimension(800, 800));
            chessBoardPanel.setOpaque(false);
            
            // 添加鼠标点击监听器
            chessBoardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    xy clickedPos = getClickedPosition(e.getX(), e.getY());
                    Main.select = clickedPos;
                    if (clickedPos != null) {
                        System.out.println("点击位置: 行=" + clickedPos.y + ", 列=" + clickedPos.x);
                        // 可以在这里添加棋子选择逻辑
                    }
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "加载棋盘资源失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 获取鼠标点击位置对应的棋盘行列坐标（检测交叉点）
     * @param mouseX 鼠标点击的X坐标
     * @param mouseY 鼠标点击的Y坐标
     * @return xy对象，包含行和列坐标，如果点击位置不在棋盘内则返回null
     */
    private xy getClickedPosition(int mouseX, int mouseY) {
        // 获取棋盘面板的尺寸
        int panelWidth = chessBoardPanel.getWidth();
        int panelHeight = chessBoardPanel.getHeight();
        
        // 计算棋盘尺寸和位置（与paintComponent中的计算一致）
        int boardSize = Math.min(panelWidth, panelHeight) * 7 / 10;
        int boardX = (panelWidth - boardSize) / 2;
        int boardY = (panelHeight - boardSize) / 2;
        
        // 检查点击是否在棋盘范围内
        if (mouseX < boardX || mouseX > boardX + boardSize || 
            mouseY < boardY || mouseY > boardY + boardSize) {
            return null; // 点击位置不在棋盘内
        }
        
        // 计算每个格子的宽度和高度
        int cellWidth = boardSize / 8;
        int cellHeight = boardSize / 9;
        
        // 计算点击位置相对于棋盘左上角的坐标
        int relativeX = mouseX - boardX;
        int relativeY = mouseY - boardY;
        
        // 计算最近的交叉点列（0-8，共9列交叉点）
        int col = Math.round((float)relativeX / cellWidth);
        // 计算最近的交叉点行（0-9，共10行交叉点）
        int row = Math.round((float)relativeY / cellHeight);
        
        // 确保行列在有效范围内（象棋棋盘有9列10行交叉点）
        if (col >= 0 && col <= 8 && row >= 0 && row <= 9) {
            return new xy(col, row);
        }
        
        return null;
    }
    
    /**
     * 在棋盘上绘制棋子材质
     * @param g2d 图形上下文
     * @param num 材质编号（对应content/Pieces下的图片文件名）
     * @param pos 棋子位置（行列坐标）
     */
    private void drawPiece(Graphics2D g2d, int num, xy pos) {
        try {
            // 加载棋子材质
            BufferedImage pieceImage = ImageIO.read(new File("content/Pieces/" + num + ".png"));
            
            // 获取棋盘尺寸和位置
            int boardSize = Math.min(chessBoardPanel.getWidth(), chessBoardPanel.getHeight()) * 7 / 10;
            int boardX = (chessBoardPanel.getWidth() - boardSize) / 2;
            int boardY = (chessBoardPanel.getHeight() - boardSize) / 2;
            
            // 计算每个格子的尺寸
            int cellWidth = boardSize / 8;
            int cellHeight = boardSize / 9;
            
            // 计算棋子缩放后的大小（缩小为原来的25%，稍微增大一些）
            int scaledWidth = (int) (pieceImage.getWidth() * 0.25);
            int scaledHeight = (int) (pieceImage.getHeight() * 0.25);
            
            // 计算棋子绘制位置（交叉点中心）
            int pieceX = boardX + pos.x * cellWidth - scaledWidth / 2;
            int pieceY = boardY + pos.y * cellHeight - scaledHeight / 2;
            
            // 绘制缩放后的棋子
            g2d.drawImage(pieceImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), pieceX, pieceY, null);
            
        } catch (IOException e) {
            System.err.println("加载棋子材质失败: " + num + ".png");
        }
    }
    
    private void startDetectionTimer() {
        detectionTimer = new Timer(16, e -> detectBoardSelectionAndTern());
        detectionTimer.start();
    }

    private void detectBoardSelectionAndTern() {
        // 触发棋盘重绘，更新棋子显示
        Main.board=Main.default_board;
        if (chessBoardPanel != null) {
            chessBoardPanel.repaint();
        }
    }
    
    /**
     * 绘制棋盘上所有棋子
     * @param g2d 图形上下文
     * @param boardX 棋盘左上角X坐标
     * @param boardY 棋盘左上角Y坐标
     * @param boardSize 棋盘尺寸
     */
    private void drawAllPieces(Graphics2D g2d, int boardX, int boardY, int boardSize) {
        if (Main.board == null) return;
        
        int cellWidth = boardSize / 8;
        int cellHeight = boardSize / 9;
        
        // 遍历棋盘上的所有位置
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                int pieceNum = Main.board[row][col];
                
                // 如果该位置有棋子（编号不为0）
                if (pieceNum != 0) {
                    // 创建位置对象
                    xy pos = new xy(col, row);
                    // 绘制棋子
                    drawPiece(g2d, pieceNum, pos);
                }
            }
        }
    }
    
    private void createButtonPanel(JLayeredPane layeredPane) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 0, 500, 80);
        
        String[] buttonTexts = {"读取", "保存", "撤销"};

        for (int i = 0; i < 3; i++) {
            JButton button = new JButton(buttonTexts[i]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 绘制阴影
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);

                    // 绘制按钮背景
                    Color bgColor = this.getBackground();
                    g2d.setColor(bgColor);
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);

                    // 绘制边框
                    g2d.setColor(bgColor.darker());
                    g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);

                    g2d.dispose();
                    super.paintComponent(g);
                }

                @Override
                public void setContentAreaFilled(boolean b) {
                    // 重写以防止默认填充
                }
            };

            button.setBounds(20 + i * (90 + 10), 20, 90, 35);
            button.setBackground(new Color(240, 240, 240));
            button.setForeground(new Color(50, 50, 50));
            button.setFont(new Font("微软雅黑", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(false);

            // 添加悬停效果
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalColor = button.getBackground();

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(220, 220, 220));
                    button.setForeground(new Color(0, 0, 0));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalColor);
                    button.setForeground(new Color(50, 50, 50));
                }

                public void mousePressed(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(200, 200, 200));
                }

                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(220, 220, 220));
                }
            });

            button.addActionListener(e -> {
                String command = ((JButton)e.getSource()).getText();
                
                switch (command) {
                    case "读取":
                        Main.board=Main.saveToboard(Database.getGame(Main.num).status);
                        System.out.println("读取按钮被点击");
                        break;
                        
                    case "保存":
                        Database.saveGame(Main.num, Main.boardTosave(Main.board), Main.tern);
                        System.out.println("保存按钮被点击");
                        // 请在此处添加您的保存代码
                        break;
                        
                    case "撤销":
                        Main.board=Main.formal_board;
                        System.out.println("撤销按钮被点击");
                        break;
                        
                    default:
                        System.out.println("未知命令: " + command);
                        break;
                }
            });
            
            buttonPanel.add(button);
        }
        
        buttonPanel.setLayout(null);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
    }
    
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        gameFrame.run();
    }
}