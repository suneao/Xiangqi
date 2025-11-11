import java.sql.*;

public class Main {
    // 数据库连接信息 - 请根据实际情况填写
    private static final String DB_URL = "jdbc:mysql://mysql2.sqlpub.com:3307/xiangqi";  // 数据库URL
    private static final String DB_USER = "sustech_admin"; // 数据库用户名
    private static final String DB_PASSWORD = "oIIzEuVdcu7Sc5v7"; // 数据库密码
    
    // 数据库连接对象
    private static Connection connection = null;
    
    public static void main(String[] args) {
        // 尝试连接数据库
        connectToDatabase();
        
        // 创建usr表
        createUsrTable();
        
        // 创建save表
        createSaveTable();
        
        // 关闭数据库连接
        closeDatabaseConnection();
    }
    
    /**
     * 连接到数据库
     */
    private static void connectToDatabase() {
        try {
            // 加载数据库驱动（根据使用的数据库类型可能需要调整）
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL示例
            // Class.forName("org.postgresql.Driver");    // PostgreSQL示例
            
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
        // catch (ClassNotFoundException e) {
        //     System.err.println("找不到数据库驱动: " + e.getMessage());
        //     e.printStackTrace();
        // }
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
    private static void closeDatabaseConnection() {
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
    
    /**
     * 创建usr表
     */
    private static void createUsrTable() {
        if (connection == null) {
            System.err.println("数据库未连接，无法创建表。");
            return;
        }
        
        String createTableSQL = "CREATE TABLE IF NOT EXISTS usr (" +
                "username VARCHAR(50) NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "save INT NOT NULL, " +
                "PRIMARY KEY (username)" +
                ")";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("成功创建或确认usr表已存在。");
        } catch (SQLException e) {
            System.err.println("创建usr表时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建save表
     */
    private static void createSaveTable() {
        if (connection == null) {
            System.err.println("数据库未连接，无法创建表。");
            return;
        }
        
        String createTableSQL = "CREATE TABLE IF NOT EXISTS save (" +
                "num INT NOT NULL, " +
                "status VARCHAR(255) NOT NULL, " +
                "round BOOLEAN NOT NULL" +
                ")";
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("成功创建或确认save表已存在。");
        } catch (SQLException e) {
            System.err.println("创建save表时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}