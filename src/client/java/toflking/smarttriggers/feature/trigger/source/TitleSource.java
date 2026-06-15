package toflking.smarttriggers.feature.trigger.source;

import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.enums.TextSource;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class TitleSource {
    private static TitleSource instance;
    private static boolean suppressCapture;

    private final Manager manager;

    public TitleSource(Manager manager) {
        this.manager = manager;
        instance = this;
    }

    public void register() {
        // Use mixin hook
    }

    public static void runWithoutCapture(Runnable action) {
        boolean previous = suppressCapture;
        suppressCapture = true;
        try {
            action.run();
        } finally {
            suppressCapture = previous;
        }
    }

    public static void handleTitleStatic(Component title) {
        if (!(instance == null || suppressCapture)) {
            instance.onTitleChange(title);
        }
    }

    public static void handleSubTitleStatic(Component subtitle) {
        if (!(instance == null || suppressCapture)) {
            instance.onSubTitleChange(subtitle);
        }
    }

    public void onTitleChange(Component title) {
        if (title == null) return;
        String msg = TextNormalizer.normalizeText(title);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.TITLE, msg, null, System.currentTimeMillis(), false);
        manager.handleEvent(event);
    }

    public void onSubTitleChange(Component subtitle) {
        if (subtitle == null) return;
        String msg = TextNormalizer.normalizeText(subtitle);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.TITLE, msg, null, System.currentTimeMillis(), true);
        manager.handleEvent(event);
    }
}
