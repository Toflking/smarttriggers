package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.*;
import toflking.smarttriggers.feature.trigger.enums.TextSource;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreboardSource {
    private static final Comparator<PlayerScoreEntry> SIDEBAR_ORDER = Comparator
            .comparingInt(PlayerScoreEntry::value)
            .reversed()
            .thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);

    private final Manager manager;
    private List<String> lastLines = Collections.emptyList();

    public ScoreboardSource(Manager manager) {
        this.manager = manager;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleScoreboardSnapshot(pollScoreboard(client)));
    }

    public List<Component> pollScoreboard(Minecraft mc) {
        if (mc.level == null) return List.of();

        Scoreboard scoreboard = mc.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (objective == null) return List.of();

        return scoreboard.listPlayerScores(objective).stream()
                .filter(entry -> !entry.isHidden())
                .sorted(SIDEBAR_ORDER)
                .limit(15)
                .map(entry -> toRenderedText(scoreboard, entry))
                .toList();
    }

    public void handleScoreboardSnapshot(List<Component> lines) {
        List<String> normalizedLines = TextNormalizer.normalizeLines(lines);
        if (normalizedLines.isEmpty()) return;
        if (lastLines.equals(normalizedLines)) return;
        TriggerEvent event = new TriggerEvent(TextSource.SCOREBOARD, null, normalizedLines, System.currentTimeMillis(), false);
        manager.handleEvent(event);
        lastLines = normalizedLines;
    }

    private Component toRenderedText(Scoreboard scoreboard, PlayerScoreEntry entry) {
        PlayerTeam team = scoreboard.getPlayersTeam(entry.owner());
        Component baseText = entry.display() != null ? entry.display().copy() : entry.ownerName().copy();
        return entry.display() != null
                ? baseText.copy()
                : team != null ? PlayerTeam.formatNameForTeam(team, baseText.copy()) : baseText.copy();
    }
}
