// 游戏主流程控制类
public class Gaming {
    
    /**
     * 游戏主入口
     */
    public static void main(String[] args) {
        loadGameOrUseDefault();
        startGameThreads();
        setupShutdownHook();
    }
    
    /**
     * 加载游戏存档或使用默认棋盘
     */
    private static void loadGameOrUseDefault() {
        Gamesave savedGame = Database.getGame(Main.num);
        if (savedGame != null) {
            Main.board = Main.saveToboard(savedGame.status);
            Main.tern = savedGame.tern;
            System.out.println("成功加载存档");
        } else {
            Main.board = Main.default_board;
            Main.tern = true;
            System.out.println("未找到存档，使用默认棋盘");
            if (Main.num == 0 && Main.username != null && !Main.username.isEmpty()) {
                Main.num = Database.getNextSaveNum();
            }
        }
    }
    private static void startGameThreads() {
        Thread frameThread = new Thread(new GameFrame());
        frameThread.start();
        Thread loopThread = new Thread(new GameLoop());
        loopThread.start();
    }
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (Main.username != null && !Main.username.isEmpty() && Main.board != null) {
                if (Main.num != 0) {
                    Database.saveGame(Main.num, Main.boardTosave(Main.board), Main.tern);
                } else {
                    int newSaveNum = Database.getNextSaveNum();
                    Database.saveGame(newSaveNum, Main.boardTosave(Main.board), Main.tern);
                    Database.updateUserSave(Main.username, newSaveNum);
                    Main.num = newSaveNum;
                }
            }
            Database.closeDatabaseConnection();
        }));
    }
}