public class Gaming {
    public static void main(String[] args) {
        // 创建游戏窗口线程
        Gamesave savedGame = Database.getGame(Main.num);
        if (savedGame != null) {
            Main.board = Main.saveToboard(savedGame.status);
            Main.tern = savedGame.tern;
        } else {
            // 如果没有找到存档，使用默认棋盘
            Main.board = Main.default_board;
            Main.tern = true;
            System.out.println("未找到存档，使用默认棋盘");
        }
        Thread frameThread = new Thread(new GameFrame());
        
        // 创建游戏循环线程
        Thread loopThread = new Thread(new GameLoop());
        
        // 启动窗口线程
        frameThread.start();
        
        // 稍微延迟后启动游戏循环线程，确保UI初始化完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 启动游戏循环线程
        loopThread.start();
        
        // 可以添加优雅关闭逻辑，确保程序退出时停止所有线程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(Main.num != 0) {
                // 创建新存档
                int newSaveNum = Database.getNextSaveNum();
                Database.saveGame(newSaveNum, Main.boardTosave(Main.board), Main.tern);
                // 更新用户表中的存档编号
                Database.updateUserSave(Main.username, newSaveNum);
                // 删除原本的存档
                Database.deleteGame(Main.num);
                // 更新Main.num为新的存档编号
                Main.num = newSaveNum;
                System.out.println("新存档已创建，编号：" + newSaveNum + "，原存档已删除");
            } else {
                // 在usr数据库中将save改为以用户账号为基础生成的hash值，hash值为int类型
                int hash = Main.username.hashCode();
                Database.saveGame(hash, Main.boardTosave(Main.board), Main.tern);
                // 更新用户表中的存档编号
                Database.updateUserSave(Main.username, hash);
                // 更新Main.num为新的存档编号
                Main.num = hash;
                System.out.println("存档已创建，编号：" + hash);
            }
            //关闭数据库连接
            Database.closeDatabaseConnection();
            System.out.println("游戏程序正在关闭...");
        }));
    }
}