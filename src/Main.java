public class Main {// 数据库连接对象
    public static void main(String[] args) {
        // 尝试连接数据库
        Database.connectToDatabase();
        Database.saveGame(0, "1324554134", true);
        // 关闭数据库连接
        Database.closeDatabaseConnection();
    }
}