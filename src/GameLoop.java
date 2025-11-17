public class GameLoop implements Runnable {
    private volatile boolean running = true;
    
    public void stop() {
        running = false;
    }
    
    @Override
    public void run() {
        // 游戏循环
        while (running) {
            GameLogic.main(null); // 执行游戏逻辑
            waitFor(16); // 等待16毫秒，约60FPS
        }
    }
    
    /**
     * 等待指定的毫秒数，不会阻塞其他线程
     * @param milliseconds 要等待的毫秒数
     */
    private static void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("等待被中断: " + e.getMessage());
        }
    }
    
    // 保留原有main方法以兼容单独运行
    public static void main(String[] args) {
        GameLoop gameLoop = new GameLoop();
        gameLoop.run();
    }
}