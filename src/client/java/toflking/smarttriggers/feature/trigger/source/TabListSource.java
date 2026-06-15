package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.enums.TextSource;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

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

    public List<Component> pollTabList(Minecraft mc) {
        if (mc.getConnection() == null) return List.of();

        return mc.getConnection().getListedOnlinePlayers().stream()
                .map(entry -> entry.getTabListDisplayName() != null ? entry.getTabListDisplayName() : Component.literal(entry.getProfile().name()))
                .toList();
    }

    public void handleTabListSnapshot(List<Component> lines) {
        List<String> normalizedLines = TextNormalizer.normalizeLines(lines);
        if (normalizedLines.isEmpty()) return;
        if (lastLines.equals(normalizedLines)) return;
        TriggerEvent event = new TriggerEvent(TextSource.TABLIST, null, normalizedLines, System.currentTimeMillis(), false);
        manager.handleEvent(event);
        lastLines = normalizedLines;
    }
}
