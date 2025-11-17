import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static int[][] board = new int[10][9];
    public static int[][] selection = new int[10][9];
    public static int tern = 0;
    public static int select = 0;
    public static Queue<int[]> signal = new LinkedList<int[]>();
    // 数据库连接对象
    public static void main(String[] args) {
        // 尝试连接数据库
        Database.connectToDatabase();
        Login.main(args);
        Gaming.main(args);
        // 关闭数据库连接
        Database.closeDatabaseConnection();
    }
}