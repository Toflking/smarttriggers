package toflking.smarttriggers.feature.hud;

import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;

public abstract class HudElement {
    public abstract String id();

    public abstract String displayName();

    public boolean isEnabled(ModConfig cfg) {
        return cfg.getHud().getOrCreateHudElementConfig(this).isVisible();
    }

    public abstract HudElementConfig createDefaultConfig();

    public abstract void render(HudRenderContext ctx);

    public void renderBackground(HudRenderContext ctx, Rect bounds, int color) {
        ctx.getDrawContext().fill(bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), color);
    }

    public abstract int width(HudRenderContext ctx);

    public abstract int height(HudRenderContext ctx);

}
