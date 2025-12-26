import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameFrame implements Runnable {
    private JFrame frame;
    private Timer detectionTimer;
    private JPanel chessBoardPanel;
    private BufferedImage woodTexture;

    private static final Color BOARD_BORDER = new Color(139, 69, 19);
    private static final Color BOARD_SHADOW = new Color(0, 0, 0, 80);
    private static final Color RIVER_TEXT = new Color(101, 67, 33);
    private static final Color RIVER_SHADOW = new Color(0, 0, 0, 40);
    private static final Color TURN_RED = new Color(220, 20, 60);
    private static final Color TURN_BLACK = new Color(30, 30, 30);
    private static final Color BUTTON_GRADIENT_START = new Color(245, 245, 245);
    private static final Color BUTTON_GRADIENT_END = new Color(225, 225, 225);
    private static final Color BUTTON_HOVER_START = new Color(235, 235, 235);
    private static final Color BUTTON_HOVER_END = new Color(215, 215, 215);
    private static final Color BUTTON_PRESSED_START = new Color(225, 225, 225);
    private static final Color BUTTON_PRESSED_END = new Color(205, 205, 205);
    private static final Color BUTTON_DISABLED_START = new Color(220, 220, 220);
    private static final Color BUTTON_DISABLED_END = new Color(180, 180, 180);
    private static final Color BUTTON_DISABLED_TEXT = new Color(150, 150, 150);
    private static final Color SELECTION_LAST = new Color(169, 169, 169);
    private static final Color SELECTION_CURRENT = new Color(139, 69, 19);
    private static final Color SELECTION_MOVE = new Color(0, 128, 0);
    private static final Color SELECTION_CAPTURE = new Color(220, 20, 60);

    private static final Font BOARD_FONT = new Font("微软雅黑", Font.BOLD, 20);
    private static final Font TURN_FONT = new Font("微软雅黑", Font.BOLD, 22);
    private static final Font RIVER_FONT = new Font("微软雅黑", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("微软雅黑", Font.BOLD, 14);

    private long selectionAnimationStart = 0;
    private float selectionAlpha = 0.0f;
    private boolean selectionAnimating = false;
    private long turnAnimationStart = 0;
    private float turnPulse = 0.0f;
    private boolean turnAnimating = false;
    private boolean lastTurnState = true;

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("象棋");
            frame.setSize(1280, 720);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

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

                    g2d.setColor(BOARD_SHADOW);
                    g2d.setStroke(new BasicStroke(3));
                    for (int i = 3; i >= 1; i--) {
                        float alpha = 0.3f - (i - 1) * 0.1f;
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        RoundRectangle2D shadowRect = new RoundRectangle2D.Float(
                                textureX + i, textureY + i, textureWidth, textureHeight, arc, arc);
                        g2d.draw(shadowRect);
                    }
                    g2d.setComposite(AlphaComposite.SrcOver);

                    drawChessBoard(g2d, x, y, boardSize);

                    g2d.setColor(BOARD_BORDER);
                    g2d.setStroke(new BasicStroke(4));
                    g2d.draw(textureRectangle);

                    drawAllPieces(g2d, x, y, boardSize);

                    g2d.setFont(TURN_FONT);

                    Color turnColor = Main.tern ? TURN_RED : TURN_BLACK;
                    String turnText = Main.tern ? "红方回合" : "黑方回合";

                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(turnText);
                    int textX = getWidth() - textWidth - 50;
                    int textY = 50;

                    Composite originalComposite = g2d.getComposite();

                    if (turnPulse < 1.0f) {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, turnPulse));
                    }

                    g2d.setColor(turnColor);
                    g2d.drawString(turnText, textX, textY);

                    g2d.setComposite(originalComposite);

                    g2d.dispose();
                }

                private void drawChessBoard(Graphics2D g2d, int x, int y, int boardSize) {
                    g2d.setColor(BOARD_BORDER);
                    g2d.setStroke(new BasicStroke(2.5f));

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

                    Stroke originalStroke = g2d.getStroke();
                    g2d.setStroke(new BasicStroke(3.5f));
                    g2d.drawLine(x + 3 * cellWidth, y, x + 5 * cellWidth, y + 2 * cellHeight);
                    g2d.drawLine(x + 5 * cellWidth, y, x + 3 * cellWidth, y + 2 * cellHeight);

                    g2d.drawLine(x + 3 * cellWidth, y + 7 * cellHeight, x + 5 * cellWidth, y + 9 * cellHeight);
                    g2d.drawLine(x + 5 * cellWidth, y + 7 * cellHeight, x + 3 * cellWidth, y + 9 * cellHeight);
                    g2d.setStroke(originalStroke);

                    g2d.setFont(RIVER_FONT);
                    String text = "楚 河          汉 界";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textX = x + (boardSize - textWidth) / 2;
                    int textY = y + 4 * cellHeight + cellHeight / 2 + fm.getAscent() / 2;

                    g2d.setColor(RIVER_SHADOW);
                    g2d.drawString(text, textX + 2, textY + 2);

                    g2d.setColor(RIVER_TEXT);
                    g2d.drawString(text, textX, textY);
                }
            };

            chessBoardPanel.setPreferredSize(new Dimension(800, 800));
            chessBoardPanel.setOpaque(false);

            chessBoardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    xy clickedPos = getClickedPosition(e.getX(), e.getY());
                    Main.select = clickedPos;
                    if (clickedPos != null) {
                        System.out.println("点击位置: 行=" + clickedPos.y + ", 列=" + clickedPos.x);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "加载棋盘资源失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private xy getClickedPosition(int mouseX, int mouseY) {
        int panelWidth = chessBoardPanel.getWidth();
        int panelHeight = chessBoardPanel.getHeight();

        int boardSize = Math.min(panelWidth, panelHeight) * 7 / 10;
        int boardX = (panelWidth - boardSize) / 2;
        int boardY = (panelHeight - boardSize) / 2;

        if (mouseX < boardX || mouseX > boardX + boardSize ||
                mouseY < boardY || mouseY > boardY + boardSize) {
            return null;
        }

        int cellWidth = boardSize / 8;
        int cellHeight = boardSize / 9;

        int relativeX = mouseX - boardX;
        int relativeY = mouseY - boardY;

        int col = Math.round((float)relativeX / cellWidth);
        int row = Math.round((float)relativeY / cellHeight);

        if (col >= 0 && col <= 8 && row >= 0 && row <= 9) {
            return new xy(col, row);
        }

        return null;
    }

    private void drawPiece(Graphics2D g2d, int num, xy pos) {
        try {
            BufferedImage pieceImage = ImageIO.read(new File("content/Pieces/" + num + ".png"));

            int boardSize = Math.min(chessBoardPanel.getWidth(), chessBoardPanel.getHeight()) * 7 / 10;
            int boardX = (chessBoardPanel.getWidth() - boardSize) / 2;
            int boardY = (chessBoardPanel.getHeight() - boardSize) / 2;

            int cellWidth = boardSize / 8;
            int cellHeight = boardSize / 9;

            int scaledWidth = (int) (pieceImage.getWidth() * 0.25);
            int scaledHeight = (int) (pieceImage.getHeight() * 0.25);

            int pieceX = boardX + pos.x * cellWidth - scaledWidth / 2;
            int pieceY = boardY + pos.y * cellHeight - scaledHeight / 2;

            g2d.drawImage(pieceImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), pieceX, pieceY, null);

            Composite originalComposite = g2d.getComposite();
            Ellipse2D highlight = new Ellipse2D.Float(
                    pieceX + scaledWidth / 4f,
                    pieceY + scaledHeight / 6f,
                    scaledWidth / 2f,
                    scaledHeight / 4f
            );
            GradientPaint highlightGradient = new GradientPaint(
                    pieceX + scaledWidth / 4f, pieceY + scaledHeight / 6f, new Color(255, 255, 255, 120),
                    pieceX + scaledWidth / 2f, pieceY + scaledHeight / 3f, new Color(255, 255, 255, 0)
            );
            g2d.setPaint(highlightGradient);
            g2d.fill(highlight);
            g2d.setComposite(originalComposite);

        } catch (IOException e) {
            System.err.println("加载棋子材质失败: " + num + ".png");
        }
    }

    private void startDetectionTimer() {
        detectionTimer = new Timer(16, e -> detectBoardSelectionAndTern());
        detectionTimer.start();
    }

    private void detectBoardSelectionAndTern() {
        updateAnimations();

        if (chessBoardPanel != null) {
            chessBoardPanel.repaint();
        }
    }

    private void updateAnimations() {
        long currentTime = System.currentTimeMillis();

        if (Main.tern != lastTurnState) {
            turnAnimationStart = currentTime;
            turnAnimating = true;
            lastTurnState = Main.tern;
        }

        if (hasSelection()) {
            if (!selectionAnimating) {
                selectionAnimationStart = currentTime;
                selectionAnimating = true;
            }
            long elapsed = currentTime - selectionAnimationStart;
            if (elapsed < 300) {
                selectionAlpha = Math.min(1.0f, elapsed / 300.0f);
            } else {
                selectionAlpha = 1.0f;
            }
        } else {
            selectionAnimating = false;
            selectionAlpha = 0.0f;
        }

        if (turnAnimating) {
            long elapsed = currentTime - turnAnimationStart;
            turnPulse = (float) (Math.sin(elapsed / 300.0) * 0.2 + 0.8);
            if (elapsed > 5000) {
                turnAnimating = false;
                turnPulse = 1.0f;
            }
        } else {
            turnPulse = 1.0f;
        }
    }

    private boolean hasSelection() {
        if (Main.selection == null) return false;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                if (Main.selection[row][col] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void drawAllPieces(Graphics2D g2d, int boardX, int boardY, int boardSize) {
        if (Main.board == null) return;

        int cellWidth = boardSize / 8;
        int cellHeight = boardSize / 9;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                int pieceNum = Main.board[row][col];

                if (pieceNum != 0) {
                    xy pos = new xy(col, row);
                    drawPiece(g2d, pieceNum, pos);
                }
            }
        }

        drawSelectionMarks(g2d, boardX, boardY, boardSize);
    }

    private void drawSelectionMarks(Graphics2D g2d, int boardX, int boardY, int boardSize) {
        if (Main.selection == null) return;

        int cellWidth = boardSize / 8;
        int cellHeight = boardSize / 9;
        int radius = (int) (Math.min(cellWidth, cellHeight) / 1.8f);
        int strokeWidth = Math.max(cellWidth, cellHeight) / 12;
        int gapSize = strokeWidth * 2;

        Composite originalComposite = g2d.getComposite();

        if (selectionAlpha > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, selectionAlpha));
        }

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                int selectionValue = Main.selection[row][col];
                if (selectionValue == 0) continue;

                int centerX = boardX + col * cellWidth;
                int centerY = boardY + row * cellHeight;

                Color markColor;
                switch (selectionValue) {
                    case 1:
                        markColor = SELECTION_LAST;
                        break;
                    case 2:
                        markColor = SELECTION_CURRENT;
                        break;
                    case 3:
                        boolean hasEnemyPiece = Main.board[row][col] != 0 && !GameLogic.isCurrentPlayersPiece(Main.board[row][col]);
                        if (hasEnemyPiece) {
                            markColor = SELECTION_CAPTURE;
                        } else {
                            markColor = SELECTION_MOVE;
                        }
                        break;
                    default:
                        continue;
                }

                g2d.setColor(markColor);
                g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

                int x = centerX - radius;
                int y = centerY - radius;
                int width = radius * 2;
                int height = radius * 2;

                g2d.drawArc(x, y, width, height, 0 + gapSize, 90 - gapSize * 2);
                g2d.drawArc(x, y, width, height, 270 + gapSize, 90 - gapSize * 2);
                g2d.drawArc(x, y, width, height, 90 + gapSize, 90 - gapSize * 2);
                g2d.drawArc(x, y, width, height, 180 + gapSize, 90 - gapSize * 2);
            }
        }
    }

    private void createButtonPanel(JLayeredPane layeredPane) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(0, 0, 550, 80);

        String[] buttonTexts = {"读取", "保存", "撤销", "新建", "投降"};

        for (int i = 0; i < 5; i++) {
            JButton button = new JButton(buttonTexts[i]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    AffineTransform originalTransform = g2d.getTransform();

                    if (getModel().isPressed()) {
                        double scale = 0.97;
                        double xOffset = (getWidth() - getWidth() * scale) / 2;
                        double yOffset = (getHeight() - getHeight() * scale) / 2;
                        g2d.translate(xOffset, yOffset);
                        g2d.scale(scale, scale);
                    }

                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);

                    Color bgColor = this.getBackground();
                    Color gradientStart, gradientEnd;

                    if (!isEnabled()) {
                        gradientStart = BUTTON_DISABLED_START;
                        gradientEnd = BUTTON_DISABLED_END;
                    } else if (bgColor.equals(BUTTON_HOVER_START) || bgColor.equals(new Color(220, 220, 220))) {
                        gradientStart = BUTTON_HOVER_START;
                        gradientEnd = BUTTON_HOVER_END;
                    } else if (bgColor.equals(BUTTON_PRESSED_START) || bgColor.equals(new Color(200, 200, 200))) {
                        gradientStart = BUTTON_PRESSED_START;
                        gradientEnd = BUTTON_PRESSED_END;
                    } else {
                        gradientStart = BUTTON_GRADIENT_START;
                        gradientEnd = BUTTON_GRADIENT_END;
                    }

                    GradientPaint gradient = new GradientPaint(
                            0, 0, gradientStart,
                            0, getHeight(), gradientEnd
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);

                    g2d.setColor(gradientEnd.darker());
                    g2d.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);

                    g2d.setTransform(originalTransform);

                    g2d.dispose();
                    super.paintComponent(g);
                }

                @Override
                public void setContentAreaFilled(boolean b) {
                }
            };

            button.setBounds(20 + i * (90 + 10), 20, 90, 35);
            button.setBackground(BUTTON_GRADIENT_START);
            button.setForeground(new Color(50, 50, 50));
            button.setFont(BUTTON_FONT);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(false);

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalColor = button.getBackground();

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!button.isEnabled()) return;
                    button.setBackground(BUTTON_HOVER_START);
                    button.setForeground(Color.BLACK);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!button.isEnabled()) return;
                    button.setBackground(originalColor);
                    button.setForeground(new Color(50, 50, 50));
                }

                public void mousePressed(java.awt.event.MouseEvent evt) {
                    if (!button.isEnabled()) return;
                    button.setBackground(BUTTON_PRESSED_START);
                }

                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    if (!button.isEnabled()) return;
                    button.setBackground(BUTTON_HOVER_START);
                }
            });

            button.addActionListener(e -> {
                String command = ((JButton)e.getSource()).getText();

                switch (command) {
                    case "读取":
                        if (Main.isGuest()) {
                            System.out.println("游客模式无法读取存档！");
                            JOptionPane.showMessageDialog(frame, "游客模式无法读取存档！", "功能受限", JOptionPane.WARNING_MESSAGE);
                            break;
                        }

                        Gamesave savedGame = Database.getGame(Main.num);
                        if (savedGame != null) {
                            Main.board = Main.saveToboard(savedGame.status);
                            Main.tern = savedGame.tern;
                            System.out.println("成功读取存档");
                        } else {
                            System.out.println("未找到存档，无法读取");
                        }
                        break;

                    case "保存":
                        if (Main.username != null && !Main.username.isEmpty()) {
                            if (Main.isGuest()) {
                                System.out.println("游客模式无法保存游戏！");
                                JOptionPane.showMessageDialog(frame, "游客模式无法保存游戏！", "功能受限", JOptionPane.WARNING_MESSAGE);
                                break;
                            }

                            int[][] currentBoard = new int[10][9];
                            for (int k = 0; k < 10; k++) {
                                for (int j = 0; j < 9; j++) {
                                    currentBoard[k][j] = Main.board[k][j];
                                }
                            }

                            System.out.println("保存时棋盘状态摘要：");
                            System.out.println("第4行第6列: " + Main.board[4][6]);
                            System.out.println("第5行第6列: " + Main.board[5][6]);

                            if(Main.num != 0) {
                                Database.saveGame(Main.num, Main.boardTosave(currentBoard), Main.tern);
                                System.out.println("存档已更新，编号：" + Main.num);
                            } else {
                                int newSaveNum = Database.getNextSaveNum();
                                Database.saveGame(newSaveNum, Main.boardTosave(currentBoard), Main.tern);
                                Database.updateUserSave(Main.username, newSaveNum);
                                Main.num = newSaveNum;
                                System.out.println("新存档已创建，编号：" + newSaveNum);
                            }
                            System.out.println("保存按钮被点击");
                        } else {
                            System.out.println("保存失败：用户未登录");
                        }
                        break;

                    case "撤销":
                        if (!Main.hasUndone) {
                            boolean boardsAreEqual = true;
                            for (int j = 0; j < 10; j++) {
                                for (int k = 0; k < 9; k++) {
                                    if (Main.board[j][k] != Main.formal_board[j][k]) {
                                        boardsAreEqual = false;
                                        break;
                                    }
                                }
                                if (!boardsAreEqual) break;
                            }
                            
                            if (boardsAreEqual) {
                                System.out.println("没有可撤销的步");
                            } else {
                                for (int j = 0; j < 10; j++) {
                                    System.arraycopy(Main.formal_board[j], 0, Main.board[j], 0, 9);
                                }
                                Main.tern = !Main.tern;
                                Main.hasUndone = true;
                                System.out.println("撤销按钮被点击，已恢复到上一步");
                            }
                        } else {
                            System.out.println("已经撤销过一步，不能重复撤销");
                        }
                        break;

                    case "新建":
                        Main.board = Main.saveToboard(Env.DEFAULT_BOARD.toString());
                        Main.tern = true;
                        Main.hasUndone = false;
                        Main.selected = false;
                        Main.select = new xy(-1, -1);
                        Main.last = new xy(-1, -1);
                        for (int j = 0; j < 10; j++)
                            System.arraycopy(Main.board[j], 0, Main.formal_board[j], 0, 9);
                        System.out.println("新建按钮被点击，棋盘已重置为默认布局");
                        break;
                    case "投降":
                        GameLogic.surrender();
                        break;
                    default:
                        System.out.println("未知命令: " + command);
                        break;
                }
            });

            if (Main.isGuest() && (buttonTexts[i].equals("读取") || buttonTexts[i].equals("保存"))) {
                button.setEnabled(false);
                button.setBackground(BUTTON_DISABLED_START);
                button.setForeground(BUTTON_DISABLED_TEXT);
            }

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