package toflking.smarttriggers.feature.hud;

import toflking.smarttriggers.feature.hud.config.HudElementConfig;

public final class HudLayout {
    private static final int MIN_INTERACTION_SIZE = 10;

    public static int computeX(HudElement element, HudElementConfig ecfg, HudRenderContext ctx) {
        float offset = ecfg.getX();
        float scale = ecfg.getScale();

        int w = Math.round(element.width(ctx) * scale);
        int sw = ctx.getScreenWidth();

        float x = switch (ecfg.getAnchor()) {
            case TOP_LEFT, BOTTOM_LEFT -> offset;
            case TOP_RIGHT, BOTTOM_RIGHT -> sw - w - offset;
        };
        return Math.round(x);
    }

    public static int computeY(HudElement element, HudElementConfig ecfg, HudRenderContext ctx) {
        float offset = ecfg.getY();
        float scale = ecfg.getScale();

        int h = Math.round(element.height(ctx) * scale);
        int sh = ctx.getScreenHeight();

        float y = switch (ecfg.getAnchor()) {
            case TOP_LEFT, TOP_RIGHT -> offset;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> sh - h - offset;
        };
        return Math.round(y);
    }

    public static Rect computeBounds(HudElement element, HudElementConfig ecfg, HudRenderContext ctx) {
        int x = computeX(element, ecfg, ctx);
        int y = computeY(element, ecfg, ctx);
        int w = Math.round(element.width(ctx) * ecfg.getScale());
        int h = Math.round(element.height(ctx) * ecfg.getScale());
        return new Rect(x, y, w, h);
    }

    public static Rect computeInteractionBounds(HudElement element, HudElementConfig ecfg, HudRenderContext ctx) {
        Rect bounds = computeBounds(element, ecfg, ctx);
        int interactionWidth = Math.max(MIN_INTERACTION_SIZE, bounds.getWidth());
        int interactionHeight = Math.max(MIN_INTERACTION_SIZE, bounds.getHeight());
        return new Rect(bounds.getX(), bounds.getY(), interactionWidth, interactionHeight);
    }

    public static int unapplyAnchorX(int finalX, int elementWidthScaled, int screenWidth, HudElementConfig.Anchor anchor) {
        int configX = 0;
        if (anchor == HudElementConfig.Anchor.TOP_LEFT || anchor == HudElementConfig.Anchor.BOTTOM_LEFT) {
            configX = finalX;
        } else if (anchor == HudElementConfig.Anchor.TOP_RIGHT || anchor == HudElementConfig.Anchor.BOTTOM_RIGHT) {
            configX = screenWidth - elementWidthScaled - finalX;
        }
        return configX;
    }

    public static int unapplyAnchorY(int finalY, int elementHeightScaled, int screenHeight, HudElementConfig.Anchor anchor) {
        int configY = 0;
        if (anchor == HudElementConfig.Anchor.TOP_LEFT || anchor == HudElementConfig.Anchor.TOP_RIGHT) {
            configY = finalY;
        } else if (anchor == HudElementConfig.Anchor.BOTTOM_LEFT || anchor == HudElementConfig.Anchor.BOTTOM_RIGHT) {
            configY = screenHeight - elementHeightScaled - finalY;
        }
        return configY;
    }

}
