import java.sql.*;

public class Database {
    private static final String DB_URL = Env.DB_SERVER_URL.toString();  // 数据库URL
    private static final String DB_USER = Env.DB_USR.toString(); // 数据库用户名
    private static final String DB_PASSWORD = Env.DB_PASSWORD.toString(); // 数据库密码
    private static Connection connection = null;

    /**
     * 连接到数据库
     */
    public static void connectToDatabase() {
        try {
            // 加载数据库驱动（根据使用的数据库类型可能需要调整）
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL示例

            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (connection != null) {
                System.out.println("成功连接到数据库！");
            } else {
                System.out.println("无法连接到数据库。");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("找不到数据库驱动: " + e.getMessage());
            System.err.println("请确保已将MySQL JDBC驱动添加到项目的classpath中。");
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接对象
     * @return Connection对象
     */
     public static Connection getConnection() {
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭。");
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库连接时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
