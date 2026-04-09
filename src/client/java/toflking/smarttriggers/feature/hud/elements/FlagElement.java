package toflking.smarttriggers.feature.hud.elements;

import toflking.smarttriggers.feature.hud.config.HudElementConfig;
import toflking.smarttriggers.feature.hud.HudElement;
import toflking.smarttriggers.feature.hud.HudRenderContext;

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
            ctx.getDrawContext().drawText(ctx.getTextRenderer(), line, 0, yOffset, 0xFFFFFFFF, false);
            yOffset += ctx.getTextRenderer().fontHeight;
        }
    }

    @Override
    public int width(HudRenderContext ctx) {
        int maxWidth = 0;
        for (String line : buildLines(ctx)) {
            maxWidth = Math.max(maxWidth, ctx.getTextRenderer().getWidth(line));
        }
        return maxWidth;
    }

    @Override
    public int height(HudRenderContext ctx) {
        return ctx.getTextRenderer().fontHeight * buildLines(ctx).size();
    }

    private List<String> buildLines(HudRenderContext ctx) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : ctx.getStateStore().getFlags().entrySet()) {
            lines.add(entry.getKey() + ": " + entry.getValue());
        }
        return lines;
    }
}
