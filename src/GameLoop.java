public class GameLoop implements Runnable {
    private volatile boolean running = true;

    // 停止游戏循环
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        // 游戏主循环
        while (running) {
            GameLogic.update(); // 更新游戏逻辑
            waitFor(8); // 等待8毫秒
        }
    }

    // 等待指定毫秒数
    private static void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("等待被中断: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GameLoop gameLoop = new GameLoop();
        gameLoop.run();
    }
}