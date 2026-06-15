package toflking.smarttriggers.feature.hud.elements;

import toflking.smarttriggers.feature.hud.HudElement;
import toflking.smarttriggers.feature.hud.HudRenderContext;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimerElement extends HudElement {
    private static final String ID = "timer";
    private static final String displayName = "Timer";

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
        return HudElementConfig.createDefaultTimer();
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
        for (Map.Entry<String, TriggerStateStore.TimerState> entry : ctx.getStateStore().getTimers().entrySet()) {
            String state = entry.getValue().isRunning() ? "" : " (paused)";
            lines.add(entry.getKey() + ": " + formatRemaining(entry.getValue().getRemainingMs()) + state);
        }
        return lines;
    }

    private String formatRemaining(long remainingMs) {
        long totalSeconds = Math.max(0L, remainingMs) / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
