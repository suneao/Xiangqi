public class Gaming {
    public static void main(String[] args) {
        // 创建游戏窗口线程
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
            // 这里可以添加资源清理代码
            System.out.println("游戏程序正在关闭...");
        }));
    }
}