package toflking.smarttriggers.feature.hud;

import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;

public abstract class HudElement {
    public abstract String id();

    public abstract String displayName();

    public boolean isEnabled(ModConfig cfg) {
        return cfg.getHud().getOrCreateHudElementConfig(this).isEnabled();
    }

    public abstract HudElementConfig createDefaultConfig();

    public void tick(HudRenderContext ctx) {
    }

    public abstract void render(HudRenderContext ctx);

    public void renderBackground(HudRenderContext ctx, Rect bounds, int color) {
        ctx.getDrawContext().fill(bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), color);
    }

    public abstract int width(HudRenderContext ctx);

    public abstract int height(HudRenderContext ctx);

    public boolean allowDrag() {
        return true;
    }
}
