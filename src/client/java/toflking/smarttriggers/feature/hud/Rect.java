package toflking.smarttriggers.feature.hud;

public record Rect(int x, int y, int width, int height) {
    public boolean contains(int mx, int my) {
        return mx >= this.x && mx <= this.x + this.width && my >= this.y && my <= this.y + this.height;
    }

}
