package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;

public class ScoreboardSource {
    private static final Comparator<ScoreboardEntry> SIDEBAR_ORDER = Comparator
            .comparingInt(ScoreboardEntry::value)
            .reversed()
            .thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);

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
                .sorted(SIDEBAR_ORDER)
                .limit(15)
                .map(entry -> toRenderedText(scoreboard, entry))
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

    private Text toRenderedText(Scoreboard scoreboard, ScoreboardEntry entry) {
        Team team = scoreboard.getScoreHolderTeam(entry.owner());
        Text baseText = entry.display() != null ? entry.display().copy() : entry.name().copy();
        return entry.display() != null
                ? baseText.copy()
                : team != null ? Team.decorateName(team, baseText.copy()) : baseText.copy();
    }
}
