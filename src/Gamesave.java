// 游戏存档类
public class Gamesave {
    int num;        // 存档编号
    String status;  // 游戏状态字符串
    boolean tern;   // 当前玩家回合
    
    // 构造函数
    public Gamesave(int num, String status, boolean tern) {
        this.num = num;
        this.status = status;
        this.tern = tern;
    }
}