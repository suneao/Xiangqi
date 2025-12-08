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
    /**
     * 将游戏数据保存到数据库的save表中
     * @param num 游戏编号
     * @param status 游戏状态
     * @param tern 当前玩家
     */
    public static void saveGame(int num, String status, boolean tern) {
        // 获取数据库连接
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法保存游戏数据：数据库连接未建立。");
            connectToDatabase(); // 尝试重新连接
            conn = getConnection();
            if (conn == null) {
                return; // 如果仍然无法连接，则退出
            }
        }
        
        // 使用INSERT INTO ... ON DUPLICATE KEY UPDATE语句避免重复插入
        String sql = "INSERT INTO xiangqi.save (num, status, tern) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE status = ?, tern = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            pstmt.setString(2, status);
            pstmt.setBoolean(3, tern);
            // 更新部分的参数
            pstmt.setString(4, status);
            pstmt.setBoolean(5, tern);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("游戏数据保存成功！");
            } else {
                System.out.println("游戏数据保存失败。");
            }
        } catch (SQLException e) {
            System.err.println("保存游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Gamesave getGame(int num){
        // 获取数据库连接
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法获取游戏数据：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                System.err.println("重试失败，无法获取游戏数据：数据库连接未建立。");
                return null;
            }
        }

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
            } else {
                System.out.println("未找到编号为 " + num + " 的游戏数据。");
            }
            return gamesave;
        } catch (SQLException e) {
            System.err.println("获取游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static void deleteGame(int num) {
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法删除游戏数据：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                System.err.println("重试失败，无法删除游戏数据：数据库连接未建立。");
                return;
            }
        }

        String sql = "DELETE FROM xiangqi.save WHERE num = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, num);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("游戏数据删除成功！");
            } else {
                System.out.println("游戏数据删除失败。");
            }
        } catch (SQLException e) {
            System.err.println("删除游戏数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static boolean loginUser(String username, String password) {
        // 获取数据库连接
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法验证用户登录：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                return false;
            }
        }
        
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
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法注册用户：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                return false;
            }
        }
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
                // save字段已经在SQL语句中设置为0，不需要再单独设置
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
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法更新用户存档：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                return;
            }
        }
        
        String sql = "UPDATE xiangqi.usr SET `save` = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saveNum);
            pstmt.setString(2, username);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("用户存档编号更新成功！");
            } else {
                System.out.println("用户存档编号更新失败。");
            }
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
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("无法获取下一个存档编号：数据库连接未建立。");
            connectToDatabase();
            conn = getConnection();
            if (conn == null) {
                return 0;
            }
        }
        
        String sql = "SELECT MAX(num) FROM xiangqi.save";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int maxNum = rs.getInt(1);
                return maxNum + 1;
            }
        } catch (SQLException e) {
            System.err.println("获取下一个存档编号时出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 1; // 如果没有找到最大编号，从1开始
    }
}