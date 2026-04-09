package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.Collections;
import java.util.List;

public class ScoreboardSource {
    private final Manager manager;
    private List<String> lastLines = Collections.emptyList();

    public ScoreboardSource(Manager manager) {
        this.manager = manager;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleScoreboardSnapshot(pollScoreboard(client)));
    }

    public List<Text> pollScoreboard(MinecraftClient mc) {
        if (mc.world == null) return List.of();
        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (objective == null) return List.of();

        return scoreboard.getScoreboardEntries(objective).stream()
                .filter(entry -> !entry.hidden())
                .limit(15)
                .map(entry -> entry.display() != null ? entry.display() : entry.name())
                .toList();
    }

    public void handleScoreboardSnapshot(List<Text> lines) {
        List<String> normalizedLines = TextNormalizer.normalizeLines(lines);
        if (normalizedLines.isEmpty()) return;
        if (lastLines.equals(normalizedLines)) return;
        TriggerEvent event = new TriggerEvent(TextSource.SCOREBOARD, null, normalizedLines, System.currentTimeMillis(), false);
        manager.handleEvent(event);
        lastLines = normalizedLines;
    }
}
