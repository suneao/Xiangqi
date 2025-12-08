import java.sql.*;

public class Database {
    private static final String DB_URL = Env.DB_SERVER_URL.toString();
    private static final String DB_USER = Env.DB_USR.toString();
    private static final String DB_PASSWORD = Env.DB_PASSWORD.toString();
    private static Connection connection = null;

    /**
     * 连接到数据库
     */
    public static void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (connection != null && !connection.isClosed())
                System.out.println("成功连接到数据库！");
            else
                System.out.println("无法连接到数据库。");
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
     * 获取有效的数据库连接，如果连接断开则尝试重新连接
     * @param operationDescription 操作描述，用于错误信息
     * @return Connection对象或null
     */
    private static Connection getValidConnection(String operationDescription) {
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法" + operationDescription + "：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                System.err.println("重试失败，无法" + operationDescription + "：数据库连接未建立。");
                return null;
            }
        }
        return conn;
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
    /**
     * 将游戏数据保存到数据库的save表中
     * @param num 游戏编号
     * @param status 游戏状态
     * @param tern 当前玩家
     */
    public static void saveGame(int num, String status, boolean tern) {
        Connection conn = getValidConnection("保存游戏数据");
        if (conn == null) return;
        String sql = "INSERT INTO xiangqi.save (num, status, tern) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE status = ?, tern = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            pstmt.setString(2, status);
            pstmt.setBoolean(3, tern);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0)
                System.out.println("游戏数据保存成功！");
            else
                System.out.println("游戏数据保存失败。");
        } catch (SQLException e) {
            System.err.println("保存游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Gamesave getGame(int num){
        Connection conn = getValidConnection("获取游戏数据");
        if (conn == null) return null;
        String sql = "SELECT * FROM xiangqi.save WHERE num = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            Gamesave gamesave = null;
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int savedNum = rs.getInt("num");
                String savedStatus = rs.getString("status");
                boolean savedTern = rs.getBoolean("tern");
                gamesave = new Gamesave(savedNum, savedStatus, savedTern);
                System.out.println("游戏编号: " + savedNum);
                System.out.println("游戏状态: " + savedStatus);
                System.out.println("当前玩家: " + (savedTern ? "黑方" : "白方"));
            } else
                System.out.println("未找到编号为 " + num + " 的游戏数据。");
            return gamesave;
        } catch (SQLException e) {
            System.err.println("获取游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static void deleteGame(int num) {
        Connection conn = getValidConnection("删除游戏数据");
        if (conn == null) return;
        String sql = "DELETE FROM xiangqi.save WHERE num = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0)
                System.out.println("游戏数据删除成功！");
            else
                System.out.println("游戏数据删除失败。");
        } catch (SQLException e) {
            System.err.println("删除游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static boolean loginUser(String username, String password) {
        Connection conn = getValidConnection("登录");
        if (conn == null) return false;
        
        String sql = "SELECT * FROM xiangqi.usr WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("用户 " + username + " 登录成功！");
                Main.username = username;
                int saveNum = rs.getInt("save");
                Main.num = saveNum;
                System.out.println("用户保存的游戏编号: " + saveNum);
                return true;
            } else {
                System.out.println("用户名或密码错误！");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("验证用户登录时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public static boolean registerUser(String username, String password) {
        Connection conn = getValidConnection("注册用户");
        if (conn == null) return false;
        String checkSql = "SELECT * FROM xiangqi.usr WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                System.out.println("用户 " + username + " 已存在！");
                return false;
            }
            String insertSql = "INSERT INTO xiangqi.usr (username, password, `save`) VALUES (?, ?, 0)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("用户 " + username + " 注册成功！");
                    return true;
                } else {
                    System.out.println("用户注册失败。");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("注册用户时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 更新用户表中的存档编号
     * @param username 用户名
     * @param saveNum 新的存档编号
     */
    public static void updateUserSave(String username, int saveNum) {
        Connection conn = getValidConnection("更新用户存档");
        if (conn == null) return;
        
        String sql = "UPDATE xiangqi.usr SET `save` = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saveNum);
            pstmt.setString(2, username);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0)
                System.out.println("用户存档编号更新成功！");
            else
                System.out.println("用户存档编号更新失败。");
        } catch (SQLException e) {
            System.err.println("更新用户存档编号时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取下一个可用的存档编号
     * @return 下一个可用的存档编号
     */
    public static int getNextSaveNum() {
        Connection conn = getValidConnection("获取下一个存档编号");
        if (conn == null)
            return -1;
        
        String sql = "SELECT MAX(num) FROM xiangqi.save";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next())
                return rs.getInt(1) + 1;
        } catch (SQLException e) {
            System.err.println("获取下一个存档编号时出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 1; // 如果没有找到最大编号，从1开始
    }
}