public class Gaming {
    public static void main(String[] args) {
        Gamesave savedGame = Database.getGame(Main.num);
        if (savedGame != null) {
            Main.board = Main.saveToboard(savedGame.status);
            Main.tern = savedGame.tern;
            System.out.println("成功加载存档");
        } else {
            Main.board = Main.default_board;
            Main.tern = true;
            System.out.println("未找到存档，使用默认棋盘");
            // 为新游戏设置一个临时存档编号
            if (Main.num == 0 && Main.username != null && !Main.username.isEmpty()) {
                Main.num = Database.getNextSaveNum();
            }
        }
        Thread frameThread = new Thread(new GameFrame());
        Thread loopThread = new Thread(new GameLoop());
        frameThread.start();
        loopThread.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 在程序关闭前保存游戏
            if (Main.username != null && !Main.username.isEmpty() && Main.board != null) {
                if (Main.num != 0) {
                    // 直接更新现有存档，不创建新存档
                    Database.saveGame(Main.num, Main.boardTosave(Main.board), Main.tern);
                } else {
                    // 生成唯一新存档编号
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