package toflking.smarttriggers.feature.hud.config;

import toflking.smarttriggers.core.config.ConfigIO;

public class HudElementConfig {
    private boolean visible;
    private Anchor anchor;
    private float x;
    private float y;
    private float scale;
    private boolean background;
    private int backgroundColor;

    public static HudElementConfig createDefaultCounter() {
        HudElementConfig counter = new HudElementConfig();
        counter.visible = true;
        counter.anchor = Anchor.TOP_LEFT;
        counter.x = 10;
        counter.y = 10;
        counter.scale = 1.0f;
        counter.background = false;
        counter.backgroundColor = 0x55FFFFFF;
        return counter;
    }

    public static HudElementConfig createDefaultFlag() {
        HudElementConfig flag = new HudElementConfig();
        flag.visible = true;
        flag.anchor = Anchor.TOP_LEFT;
        flag.x = 10;
        flag.y = 10;
        flag.scale = 1.0f;
        flag.background = false;
        flag.backgroundColor = 0x55FFFFFD;
        return flag;
    }

    public static HudElementConfig createDefaultTimer() {
        HudElementConfig timer = new HudElementConfig();
        timer.visible = true;
        timer.anchor = Anchor.TOP_LEFT;
        timer.x = 10;
        timer.y = 10;
        timer.scale = 1.0f;
        timer.background = false;
        timer.backgroundColor = 0x55FFFFFE;
        return timer;
    }

    public static void ensureDefaultHudElementConfig(HudElementConfig ecfg) {
        if (ecfg.anchor == null) {
            ecfg.anchor = Anchor.TOP_LEFT;
            ConfigIO.setChanged(true);
        }
        if (ecfg.x <= 0) {
            ecfg.x = 10;
            ConfigIO.setChanged(true);
        }
        if (ecfg.y <= 0) {
            ecfg.y = 10;
            ConfigIO.setChanged(true);
        }
        if (ecfg.scale <= 0) {
            ecfg.scale = 1.0f;
            ConfigIO.setChanged(true);
        }
        if (ecfg.scale >= 5) {
            ecfg.scale = 5.0f;
            ConfigIO.setChanged(true);
        }
        if (ecfg.backgroundColor == 0) {
            ecfg.backgroundColor = 0x55FFFFFF;
        }
    }

    public enum Anchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
