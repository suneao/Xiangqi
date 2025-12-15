// 坐标类，用于表示棋盘上的位置
public class xy {
    public int x; // 列坐标 (0-8)
    public int y; // 行坐标 (0-9)
    
    // 构造函数：指定坐标
    public xy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 默认构造函数：无效坐标(-1,-1)
    public xy(){
        this.x = -1;
        this.y = -1;
    }

    // 复制构造函数
    public xy(xy other) {
        if (other != null) {
            this.x = other.x;
            this.y = other.y;
        } else {
            this.x = -1;
            this.y = -1;
        }
    }

    // 获取X坐标
    public int getX() {
        return x;
    }

    // 获取Y坐标
    public int getY() {
        return y;
    }

    // 设置X坐标
    public void setX(int x) {
        this.x = x;
    }

    // 设置Y坐标
    public void setY(int y) {
        this.y = y;
    }
}