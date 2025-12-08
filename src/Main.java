public class Main {
    public static int[][] board = saveToboard(Env.DEFAULT_BOARD.toString());
    public static int[][] formal_board = new int[10][9];
    public static int[][] selection = new int[10][9];
    public static boolean tern = true;
    public static xy select = new xy(-1,-1);
    public static boolean selected = false;
    public static xy last = new xy(-1,-1);
    public static String username;
    public static int num;
    public static int[][] default_board = {
            {11, 17, 19, 23, 15, 23, 19, 17, 11},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            { 0, 21,  0,  0,  0,  0,  0, 21,  0},
            {13,  0, 13,  0, 13,  0, 13,  0, 13},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            {12,  0, 12,  0, 12,  0, 12,  0, 12},
            { 0, 20,  0,  0,  0,  0,  0, 20,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            {10, 16, 18, 22, 14, 22, 18, 16, 10}
    };

    public static void main(String[] args) {
        Database.connectToDatabase();
        // System.out.println(boardTosave(default_board));
        Main.board = saveToboard(Env.DEFAULT_BOARD.toString());
        for (int i = 0; i < 10; i++)
            System.arraycopy(Main.board[i], 0, Main.formal_board[i], 0, 9);
        Login.main(args);
    }
    public static int[][] saveToboard(String status) {
        int[][] tmp = new int[10][9];
        for(int i = 0;i < 10;i++)
            for(int j = 0;j < 9;j++)
                tmp[i][j] = 0;
        for(int i = 0;i + 1 < status.length();i+=2) {
            int row = i / 18;
            int col = (i % 18) / 2;
            if (row < 10 && col < 9) {
                tmp[row][col] = (status.charAt(i) - '0') * 10 + (status.charAt(i+1) - '0');
            }
        }
        return tmp;
    }
    
    public static String boardTosave(int[][] tmp) {
        String status = "";
        for(int i = 0;i < 10;i++)
            for(int j = 0;j < 9;j++) {
                int piece = tmp[i][j];
                status += String.format("%02d", piece);
            }
        return status;
    }
}