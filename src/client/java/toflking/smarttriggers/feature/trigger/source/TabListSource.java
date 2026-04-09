package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.Collections;
import java.util.List;

public class TabListSource {
    private final Manager manager;
    private List<String> lastLines = Collections.emptyList();

    public TabListSource(Manager manager) {
        this.manager = manager;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleTabListSnapshot(pollTabList(client)));
    }

    public List<Text> pollTabList(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null) return List.of();

        return mc.getNetworkHandler().getListedPlayerListEntries().stream()
                .map(entry -> entry.getDisplayName() != null ? entry.getDisplayName() : Text.literal(entry.getProfile().name()))
                .toList();
    }

    public void handleTabListSnapshot(List<Text> lines) {
        List<String> normalizedLines = TextNormalizer.normalizeLines(lines);
        if (normalizedLines.isEmpty()) return;
        if (lastLines.equals(normalizedLines)) return;
        TriggerEvent event = new TriggerEvent(TextSource.TABLIST, null, normalizedLines, System.currentTimeMillis(), false);
        manager.handleEvent(event);
        lastLines = normalizedLines;
    }
}
