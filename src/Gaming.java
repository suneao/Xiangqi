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
            if (Main.num == 0 && !Main.username.isEmpty()) {
                Main.num = Main.username.hashCode();
            }
        }
        Thread frameThread = new Thread(new GameFrame());
        Thread loopThread = new Thread(new GameLoop());
        frameThread.start();
        loopThread.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(Main.num != 0) {
                int newSaveNum = Database.getNextSaveNum();
                Database.saveGame(newSaveNum, Main.boardTosave(Main.board), Main.tern);
                Database.updateUserSave(Main.username, newSaveNum);
                Database.deleteGame(Main.num);
                Main.num = newSaveNum;
                System.out.println("新存档已创建，编号：" + newSaveNum + "，原存档已删除");
            } else {
                Database.saveGame(Main.username.hashCode(), Main.boardTosave(Main.board), Main.tern);
                Database.updateUserSave(Main.username, Main.username.hashCode());
                Main.num = Main.username.hashCode();
                System.out.println("存档已创建，编号：" + Main.username.hashCode());
            }
            Database.closeDatabaseConnection();
            System.out.println("游戏程序正在关闭...");
        }));
    }
}