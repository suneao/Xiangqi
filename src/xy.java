public class xy {
    public int x;
    public int y;
    public xy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public xy(){
        this.x = -1;
        this.y = -1;
    }

    public xy(xy other) {
        if (other != null) {
            this.x = other.x;
            this.y = other.y;
        } else {
            this.x = -1;
            this.y = -1;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
