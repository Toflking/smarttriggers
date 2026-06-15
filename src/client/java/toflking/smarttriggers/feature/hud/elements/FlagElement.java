package toflking.smarttriggers.feature.hud.elements;

import toflking.smarttriggers.feature.hud.HudElement;
import toflking.smarttriggers.feature.hud.HudRenderContext;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlagElement extends HudElement {
    private static final String ID = "flag";
    private static final String displayName = "Flag";


    @Override
    public String id() {
        return ID;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public HudElementConfig createDefaultConfig() {
        return HudElementConfig.createDefaultFlag();
    }

    @Override
    public void render(HudRenderContext ctx) {
        int yOffset = 0;
        for (String line : buildLines(ctx)) {
            ctx.getGuiGraphicsExtractor().text(ctx.getFont(), line, 0, yOffset, 0xFFFFFFFF, false);
            yOffset += ctx.getFont().lineHeight;
        }
    }

    @Override
    public int width(HudRenderContext ctx) {
        int maxWidth = 0;
        for (String line : buildLines(ctx)) {
            maxWidth = Math.max(maxWidth, ctx.getFont().width(line));
        }
        return maxWidth;
    }

    @Override
    public int height(HudRenderContext ctx) {
        return ctx.getFont().lineHeight * buildLines(ctx).size();
    }

    private List<String> buildLines(HudRenderContext ctx) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : ctx.getStateStore().getFlags().entrySet()) {
            lines.add(entry.getKey() + ": " + entry.getValue());
        }
        return lines;
    }
}
