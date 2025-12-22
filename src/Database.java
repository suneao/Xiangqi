import java.sql.*;

public class Database {
    private static final String DB_URL = Env.DB_SERVER_URL.toString();
    private static final String DB_USER = Env.DB_USR.toString();
    private static final String DB_PASSWORD = Env.DB_PASSWORD.toString();
    private static Connection connection = null;

    public static void connectToDatabase() {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            if (connection != null && !connection.isClosed()) {
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
     public static Connection getConnection() {
        return connection;
    }
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
        try {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            System.err.println("设置事务自动提交属性时出错: " + e.getMessage());
            return null;
        }
        
        return conn;
    }
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
    public static void saveGame(int num, String status, boolean tern) {
        if (Main.isGuest()) {
            System.out.println("游客模式无法保存游戏！");
            return;
        }
        
        Connection conn = getValidConnection("保存游戏数据");
        if (conn == null) return;
        
        try {
            conn.setAutoCommit(false);
            System.out.println("准备保存游戏数据：");
            System.out.println("存档编号: " + num);
            System.out.println("游戏状态长度: " + status.length());
            System.out.println("当前玩家: " + tern);
            String deleteSql = "DELETE FROM xiangqi.save WHERE num = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, num);
                int deletedRows = deleteStmt.executeUpdate();
                System.out.println("删除旧存档影响的行数: " + deletedRows);
            }
            String insertSql = "INSERT INTO xiangqi.save (num, status, tern) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, num);
                insertStmt.setString(2, status);
                insertStmt.setBoolean(3, tern);
                int insertedRows = insertStmt.executeUpdate();
                System.out.println("插入新存档影响的行数: " + insertedRows);
            }
            
            conn.commit();
            System.out.println("保存操作完成");
            System.out.println("游戏数据保存成功！");
            
            Gamesave verifySave = getGame(num);
            if (verifySave != null) {
                System.out.println("保存验证成功：存档确实存在");
            } else {
                System.out.println("保存验证失败：存档不存在");
            }
        } catch (SQLException e) {
            System.err.println("保存游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("回滚事务时出错: " + rollbackEx.getMessage());
            }
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
                System.out.println("游戏数据删除操作完成（未找到指定编号的存档记录）。");
        } catch (SQLException e) {
            System.err.println("删除游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static boolean loginUser(String username, String password) {
        if (username.equals("0") && password.equals("0")) {
            System.out.println("游客模式登录成功！");
            Main.username = "0";
            Main.num = 0;
            return true;
        }
        
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
        if (username.equals("0")) {
            System.out.println("禁止注册游客账号！");
            return false;
        }
        
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
    public static void updateUserSave(String username, int saveNum) {
        if (Main.isGuest()) {
            System.out.println("游客模式无法更新存档！");
            return;
        }
        
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
        
        return 1;
    }
}