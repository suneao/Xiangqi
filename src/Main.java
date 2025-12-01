public class Main {
    public static int[][] board = new int[10][9];
    public static int[][] formal_board = new int[10][9];
    public static int[][] selection = new int[10][9];
    public static boolean tern = true;
    public static xy select = new xy(-1,-1);
    public static boolean selected = false;
    public static xy last = new xy(-1,-1);
    public static String username;
    public static int num;

    // 数据库连接对象
    public static void main(String[] args) {
        // 尝试连接数据库
        Database.connectToDatabase();
        Login.main(args);
    }
    public static int[][] saveToboard(String status) {
        int[][] tmp = new int[10][9];
        for(int i = 0;i < 10;i++)
            for(int j = 0;j < 9;j++)
                tmp[i][j] = 0;
        for(int i = 0;i < status.length();i+=2)
            tmp[i/10][i%9] = status.charAt(i+1) - '0'+(status.charAt(i)-'0')*10;
        return tmp;
    }
    public static String boardTosave(int[][] tmp) {
        String status = "";
        for(int i = 0;i < 10;i++)
            for(int j = 0;j < 9;j++)
                status += (tmp[i][j]/10+'0')+""+(tmp[i][j]%10+'0');
        return status;
    }
}