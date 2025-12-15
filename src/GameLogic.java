import javax.swing.*;

public class GameLogic {

    // 是否已经结束、谁赢
    private static boolean gameOver = false;
    private static String winner = null;
    // 记录当前选中的棋子位置
    private static xy lastSelect = null;

    /**
     * GameLoop 会不停调用这个方法，相当于“每一帧更新逻辑”
     */
    public static void main(String[] args) {
        update();
    }

    /**
     * 每一帧的逻辑更新，目前主要是同步 formal_board，
     * 方便以后扩展悔棋等功能。
     */
    public static void update() {
        if (Main.board == null) return;

        if (Main.formal_board == null
                || Main.formal_board.length != Main.board.length
                || Main.formal_board[0].length != Main.board[0].length) {
            Main.formal_board = new int[Main.board.length][Main.board[0].length];
        }

        // 检测棋子移动意图
        if (Main.select != null && Main.select.x != -1 && Main.select.y != -1) {
            // 检查点击位置是否有棋子
            if (Main.board[Main.select.y][Main.select.x] != 0 && isCurrentPlayersPiece(Main.board[Main.select.y][Main.select.x])) {
                // 点击了当前玩家的棋子，选中它
                // 只有当点击的位置与上次选中的位置不同时，才更新选择
                if (lastSelect == null || lastSelect.x != Main.select.x || lastSelect.y != Main.select.y) {
                    lastSelect = new xy(Main.select.x, Main.select.y);
                    System.out.println("选中棋子");
                }
            } else if (lastSelect != null) {
                // 点击了其他位置，检查是否是可走位置
                boolean isMoveable = canMove(Main.board, lastSelect.x, lastSelect.y, Main.select.x, Main.select.y, Main.tern);
                if (isMoveable) {
                    // 点击了可走位置，移动棋子
                    boolean moved = tryMove(lastSelect, Main.select);
                    if (moved) {
                        System.out.println("棋子移动成功");
                        lastSelect = null; // 移动后取消选择
                    }
                } else {
                    // 点击了不能走的位置，取消选择
                    lastSelect = null;
                    System.out.println("取消选择棋子");
                }
            }
            
            // 处理完选择后，重置Main.select变量，避免每一帧都重复处理
            Main.select = new xy(-1, -1);
        }
        
        // 更新selection数组，显示上一步位置、当前选中位置和可走位置
        updateSelection();
    }

    // 对外查询是否结束、赢家是谁
    public static boolean isGameOver() {
        return gameOver;
    }

    public static String getWinner() {
        return winner;
    }

    // 工具方法：判断颜色 / 同方
    private static boolean isRedPiece(int piece) {
        return piece != 0 && piece % 2 != 0;
    }

    private static boolean isBlackPiece(int piece) {
        return piece != 0 && piece % 2 == 0;
    }

    public static boolean isSameSide(int p1, int p2) {
        if (p1 == 0 || p2 == 0) return false;
        return (p1 % 2) == (p2 % 2);
    }

    public static boolean isCurrentPlayersPiece(int piece) {
        if (piece == 0) return false;
        boolean red = isRedPiece(piece);
        return Main.tern ? red : !red;
    }

    private static boolean inBoard(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 10;
    }
    

    
    /**
     * 更新Main.selection数组，显示上一步位置、当前选中位置和可走位置
     */
    private static void updateSelection() {
        if (Main.selection == null) {
            Main.selection = new int[10][9];
        }
        
        // 清除之前的selection数组
        for (int i = 0; i < Main.selection.length; i++) {
            for (int j = 0; j < Main.selection[i].length; j++) {
                Main.selection[i][j] = 0;
            }
        }
        
        // 标记上一步棋子位置（值为1）
        if (Main.last != null && Main.last.x != -1 && Main.last.y != -1) {
            Main.selection[Main.last.y][Main.last.x] = 1;
        }
        
        // 标记当前选中的棋子位置（值为2）
        if (lastSelect != null && lastSelect.x != -1 && lastSelect.y != -1) {
            Main.selection[lastSelect.y][lastSelect.x] = 2;
            
            // 如果选中的位置有棋子，计算并标记能走的位置（值为3）
            int selectedPiece = Main.board[lastSelect.y][lastSelect.x];
            if (selectedPiece != 0 && isCurrentPlayersPiece(selectedPiece)) {
                markValidMoves(lastSelect);
            }
        }
    }
    
    /**
     * 标记指定棋子能走的所有位置
     */
    private static void markValidMoves(xy from) {
        int sx = from.x;
        int sy = from.y;
        int piece = Main.board[sy][sx];
        
        // 遍历整个棋盘，检查哪些位置是可走的
        for (int dy = 0; dy < 10; dy++) {
            for (int dx = 0; dx < 9; dx++) {
                if (sx == dx && sy == dy) continue;
                xy to = new xy(dx, dy);
                if (canMove(Main.board, sx, sy, dx, dy, Main.tern)) {
                    Main.selection[dy][dx] = 3;
                }
            }
        }
    }

    /**
     * 对外调用的“尝试走子”接口：
     * 负责：检查合法性 -> 更新棋盘 -> 判断将军/胜负 -> 切换回合
     */
    public static boolean tryMove(xy from, xy to) {
        if (from == null || to == null) return false;
        if (gameOver) {
            JOptionPane.showMessageDialog(null,
                    "游戏已结束，请新建棋局或重新登录。",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        int sx = from.x;
        int sy = from.y;
        int dx = to.x;
        int dy = to.y;

        if (!inBoard(sx, sy) || !inBoard(dx, dy)) return false;
        if (sx == dx && sy == dy) return false;

        int piece = Main.board[sy][sx];
        if (piece == 0) return false;
        if (!isCurrentPlayersPiece(piece)) return false;

        // 先按规则判断这步是否可走（含不能自家将被将 / 不能将帅对脸）
        boolean legal = canMove(Main.board, sx, sy, dx, dy, Main.tern);
        if (!legal) {
            JOptionPane.showMessageDialog(null,
                    "非法走子！",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        // 备份当前棋盘到 formal_board（用于悔棋等）
        if (Main.formal_board == null
                || Main.formal_board.length != Main.board.length
                || Main.formal_board[0].length != Main.board[0].length) {
            Main.formal_board = new int[Main.board.length][Main.board[0].length];
        }
        for (int i = 0; i < Main.board.length; i++) {
            System.arraycopy(Main.board[i], 0, Main.formal_board[i], 0, Main.board[i].length);
        }

        int captured = Main.board[dy][dx];
        Main.board[dy][dx] = piece;
        Main.board[sy][sx] = 0;
        Main.last = new xy(dx, dy);

        boolean opponentInCheck = false;

        // 吃掉将/帅 => 直接结束
        if (captured == 14 || captured == 15) {
            gameOver = true;
            if (captured == 14) {
                winner = "黑方胜利";
            } else {
                winner = "红方胜利";
            }
            JOptionPane.showMessageDialog(null,
                    winner + "！",
                    "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            
            // 胜利后重置游戏
            resetGame();
        } else {
            // 判断是否将军对方（在切换回合前，检测移动后对方是否被将军）
            // 移动方是Main.tern，对方是!Main.tern，所以检测对方是否被将军
            opponentInCheck = isKingInCheck(Main.board, !Main.tern);
        }

        // 切换回合
        Main.tern = !Main.tern;
        // 移动成功后重置撤销标志，允许再次撤销
        Main.hasUndone = false;

        if (!gameOver && opponentInCheck) {
            JOptionPane.showMessageDialog(null,
                    "将军！",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
        }

        return true;
    }

    /**
     * 判断在当前局面下，某一方的将/帅是否被将军。
     * redKing = true 代表判断“红方将/帅是否在被攻击”。
     */
    private static boolean isKingInCheck(int[][] board, boolean redKing) {
        int kingValue = redKing ? 15 : 14; // 修正：红方帅是15（奇数），黑方将是14（偶数）
        xy kingPos = findPiece(board, kingValue);
        if (kingPos == null) return false; // 已经被吃掉，游戏会被判结束

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                int p = board[y][x];
                if (p == 0) continue;
                if (isSameSide(p, kingValue)) continue; // 同一方，跳过
                // 敌方棋子是否可以按规则走到我方将/帅处
                if (basicCanMove(board, x, y, kingPos.x, kingPos.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 找某个特定棋子的位置（用于找到将/帅）
     */
    private static xy findPiece(int[][] board, int value) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] == value) {
                    return new xy(x, y);
                }
            }
        }
        return null;
    }

    /**
     * 判断执行这步棋是否“总体合法”，包括：
     * 1. 起点有棋、是当前方
     * 2. 终点不是己方棋子
     * 3. 按棋子类型的走法合法
     * 其他检查（如将军、将帅对脸）不再阻碍移动，仅在吃掉将帅时判定胜负
     */
    private static boolean canMove(int[][] board, int sx, int sy, int dx, int dy, boolean redTurn) {
        if (!inBoard(sx, sy) || !inBoard(dx, dy)) return false;
        if (sx == dx && sy == dy) return false;

        int piece = board[sy][sx];
        if (piece == 0) return false;

        boolean isRed = isRedPiece(piece);
        if (redTurn != isRed) return false; // 不是当前行动方

        int dest = board[dy][dx];
        if (dest != 0 && isSameSide(piece, dest)) return false; // 不能吃自己人

        // 只检查棋子类型的基本走法
        return basicCanMove(board, sx, sy, dx, dy);
    }

    private static int[][] copyBoard(int[][] src) {
        int[][] dst = new int[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, src[i].length);
        }
        return dst;
    }

    /**
     * 重置游戏状态
     */
    private static void resetGame() {
        // 重置棋盘为初始状态，与main方法使用相同的初始化方式
        Main.board = Main.saveToboard(Env.DEFAULT_BOARD.toString());
        
        // 重置游戏状态变量
        gameOver = false;
        winner = null;
        
        // 重置回合为红方先行
        Main.tern = true;
        
        // 重置选中和最后移动位置
        Main.select = null;
        Main.last = null;
        
        // 重置正式棋盘（用于悔棋等）
        Main.formal_board = new int[Main.board.length][Main.board[0].length];
        for (int i = 0; i < Main.board.length; i++) {
            System.arraycopy(Main.board[i], 0, Main.formal_board[i], 0, Main.board[i].length);
        }
        
        System.out.println("游戏已重置");
    }

    /**
     * 判断棋盘上是否出现“将帅对脸”的情况。
     */
    private static boolean isFlyingGeneral(int[][] board) {
        xy redKing = findPiece(board, 14);
        xy blackKing = findPiece(board, 15);
        if (redKing == null || blackKing == null) return false;

        if (redKing.x != blackKing.x) return false;

        int x = redKing.x;
        int minY = Math.min(redKing.y, blackKing.y) + 1;
        int maxY = Math.max(redKing.y, blackKing.y);
        for (int y = minY; y < maxY; y++) {
            if (board[y][x] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 只按“棋子本身的走法”判断是否能从 (sx,sy) 到 (dx,dy)，
     * 不考虑“轮到谁走”“自家将被将军”“将帅对脸”等高层规则。
     */
    private static boolean basicCanMove(int[][] board, int sx, int sy, int dx, int dy) {
        if (!inBoard(sx, sy) || !inBoard(dx, dy)) return false;
        if (sx == dx && sy == dy) return false;

        int piece = board[sy][sx];
        if (piece == 0) return false;

        boolean isRed = isRedPiece(piece);
        int type = piece / 2; // 10/11 ->5(车)，12/13->6(兵)，14/15->7(将)，16/17->8(马)，18/19->9(相)，20/21->10(炮)，22/23->11(士)

        int dxDiff = dx - sx;
        int dyDiff = dy - sy;
        int absDx = Math.abs(dxDiff);
        int absDy = Math.abs(dyDiff);

        int dest = board[dy][dx];

        switch (type) {
            case 5: // 车
                if (sx != dx && sy != dy) return false;
                if (sx == dx) {
                    int step = dyDiff > 0 ? 1 : -1;
                    for (int y = sy + step; y != dy; y += step) {
                        if (board[y][sx] != 0) return false;
                    }
                } else {
                    int step = dxDiff > 0 ? 1 : -1;
                    for (int x = sx + step; x != dx; x += step) {
                        if (board[sy][x] != 0) return false;
                    }
                }
                return true;

            case 10: // 炮
                if (sx != dx && sy != dy) return false;
                int count = 0;
                if (sx == dx) {
                    int stepY = dyDiff > 0 ? 1 : -1;
                    for (int y = sy + stepY; y != dy; y += stepY) {
                        if (board[y][sx] != 0) count++;
                    }
                } else {
                    int stepX = dxDiff > 0 ? 1 : -1;
                    for (int x = sx + stepX; x != dx; x += stepX) {
                        if (board[sy][x] != 0) count++;
                    }
                }
                if (dest == 0) {
                    // 平炮：中间不能有棋子
                    return count == 0;
                } else {
                    // 炮吃子：中间必须正好一个棋子
                    return count == 1;
                }

            case 8: // 马
                if (!((absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2))) return false;
                int legX, legY;
                if (absDx == 2) {
                    legX = sx + (dxDiff > 0 ? 1 : -1);
                    legY = sy;
                } else {
                    legX = sx;
                    legY = sy + (dyDiff > 0 ? 1 : -1);
                }
                if (!inBoard(legX, legY)) return false;
                return board[legY][legX] == 0;

            case 9: // 相 / 象
                if (!(absDx == 2 && absDy == 2)) return false;
                // 不能过河
                if (isRed && dy >= 5) return false;
                if (!isRed && dy < 5) return false;
                int eyeX = sx + dxDiff / 2;
                int eyeY = sy + dyDiff / 2;
                if (!inBoard(eyeX, eyeY)) return false;
                return board[eyeY][eyeX] == 0;

            case 11: // 士 / 仕
                if (!(absDx == 1 && absDy == 1)) return false;
                if (!inPalace(dx, dy, isRed)) return false;
                return true;

            case 7: // 将 / 帅
                // 处理普通上下左右一步的情况
                if (!((absDx == 1 && absDy == 0) || (absDx == 0 && absDy == 1))) {
                    return false;
                }
                return inPalace(dx, dy, isRed);

            case 6: // 兵 / 卒
                // 红方在上方，兵/卒向下移动
                // 黑方在下方，兵/卒向上移动
                if (isRed) {
                    // 不能后退
                    if (dy < sy) return false;
                    if (sy <= 4) {
                        // 过河前，只能直走一步
                        return dx == sx && dy == sy + 1;
                    } else {
                        // 过河后，可以前进或左右平移一步
                        if (dy == sy + 1 && dx == sx) return true;
                        return dy == sy && absDx == 1;
                    }
                } else {
                    // 黑兵：不能后退
                    if (dy > sy) return false;
                    if (sy >= 5) {
                        // 过河前，只能直走一步
                        return dx == sx && dy == sy - 1;
                    } else {
                        // 过河后，可以前进或左右平移一步
                        if (dy == sy - 1 && dx == sx) return true;
                        return dy == sy && absDx == 1;
                    }
                }

            default:
                return false;
        }
    }

    /**
     * 判断某一坐标是否在“九宫格”内
     */
    private static boolean inPalace(int x, int y, boolean isRed) {
        if (x < 3 || x > 5) return false;
        if (isRed) {
            return y >= 7 && y <= 9;
        } else {
            return y >= 0 && y <= 2;
        }
    }
}