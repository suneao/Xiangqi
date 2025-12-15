public class Main {
    // 游戏状态变量
    public static volatile int[][] board = saveToboard(Env.DEFAULT_BOARD.toString());
    public static volatile int[][] formal_board = new int[10][9];
    public static int[][] selection = new int[10][9];
    public static boolean hasUndone = false;
    public static boolean tern = true;
    public static xy select = new xy(-1,-1);
    public static boolean selected = false;
    public static xy last = new xy(-1,-1);
    public static String username = "";
    public static int num;
    
    // 默认棋盘布局
    public static int[][] default_board = {
        {11, 17, 19, 23, 15, 23, 19, 17, 11}, // 红方棋子
        { 0,  0,  0,  0,  0,  0,  0,  0,  0},
        { 0, 21,  0,  0,  0,  0,  0, 21,  0},
        {13,  0, 13,  0, 13,  0, 13,  0, 13},
        { 0,  0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0,  0},
        {12,  0, 12,  0, 12,  0, 12,  0, 12},
        { 0, 20,  0,  0,  0,  0,  0, 20,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0,  0},
        {10, 16, 18, 22, 14, 22, 18, 16, 10}  // 黑方棋子
    };

    public static void main(String[] args) {
        // 连接数据库并初始化游戏
        Database.connectToDatabase();
        Main.board = saveToboard(Env.DEFAULT_BOARD.toString());
        
        // 复制棋盘到正式棋盘
        for (int i = 0; i < 10; i++) {
            System.arraycopy(Main.board[i], 0, Main.formal_board[i], 0, 9);
        }
        
        // 启动登录界面
        Login.main(args);
    }
    
    // 将字符串转换为棋盘数组
    public static int[][] saveToboard(String status) {
        int[][] tmp = new int[10][9];
        
        // 初始化棋盘为全0
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 9; j++) {
                tmp[i][j] = 0;
            }
        }
        
        // 解析状态字符串
        for(int i = 0; i + 1 < status.length(); i += 2) {
            int row = i / 18;
            int col = (i % 18) / 2;
            if (row < 10 && col < 9) {
                tmp[row][col] = (status.charAt(i) - '0') * 10 + (status.charAt(i+1) - '0');
            }
        }
        return tmp;
    }
    
    // 将棋盘数组转换为字符串
    public static String boardTosave(int[][] tmp) {
        StringBuilder status = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 9; j++) {
                int piece = tmp[i][j];
                status.append(String.format("%02d", piece));
            }
        }
        return status.toString();
    }
}