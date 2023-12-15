class AStarState {
    int x, y, g, h;
    AStarState parent;

    public AStarState(int x, int y, int g, int h, AStarState parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public int f() {
        return g + h;
    }
}