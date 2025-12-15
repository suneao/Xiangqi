// 游戏主流程控制类
public class Gaming {
    
    /**
     * 游戏主入口
     */
    public static void main(String[] args) {
        // 加载游戏存档或使用默认棋盘
        loadGameOrUseDefault();
        
        // 启动游戏界面和逻辑循环
        startGameThreads();
        
        // 设置程序关闭时的保存钩子
        setupShutdownHook();
    }
    
    /**
     * 加载游戏存档或使用默认棋盘
     */
    private static void loadGameOrUseDefault() {
        Gamesave savedGame = Database.getGame(Main.num);
        if (savedGame != null) {
            // 加载存档
            Main.board = Main.saveToboard(savedGame.status);
            Main.tern = savedGame.tern;
            System.out.println("成功加载存档");
        } else {
            // 使用默认棋盘
            Main.board = Main.default_board;
            Main.tern = true;
            System.out.println("未找到存档，使用默认棋盘");
            
            // 为新游戏设置存档编号
            if (Main.num == 0 && Main.username != null && !Main.username.isEmpty()) {
                Main.num = Database.getNextSaveNum();
            }
        }
    }
    
    /**
     * 启动游戏线程
     */
    private static void startGameThreads() {
        // 启动游戏界面线程
        Thread frameThread = new Thread(new GameFrame());
        frameThread.start();
        
        // 启动游戏逻辑循环线程
        Thread loopThread = new Thread(new GameLoop());
        loopThread.start();
    }
    
    /**
     * 设置程序关闭时的保存钩子
     */
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 在程序关闭前保存游戏
            if (Main.username != null && !Main.username.isEmpty() && Main.board != null) {
                if (Main.num != 0) {
                    // 更新现有存档
                    Database.saveGame(Main.num, Main.boardTosave(Main.board), Main.tern);
                } else {
                    // 创建新存档
                    int newSaveNum = Database.getNextSaveNum();
                    Database.saveGame(newSaveNum, Main.boardTosave(Main.board), Main.tern);
                    Database.updateUserSave(Main.username, newSaveNum);
                    Main.num = newSaveNum;
                }
            }
            // 关闭数据库连接
            Database.closeDatabaseConnection();
        }));
    }
}