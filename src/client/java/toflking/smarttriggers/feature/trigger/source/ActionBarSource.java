package toflking.smarttriggers.feature.trigger.source;

import net.minecraft.network.chat.Component;
import toflking.smarttriggers.feature.trigger.enums.TextSource;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class ActionBarSource {
    private static ActionBarSource instance;
    private static boolean suppressCapture;

    private final Manager manager;

    public ActionBarSource(Manager manager) {
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

    public static void handleActionBarStatic(Component message) {
        if (!(instance == null || suppressCapture)) {
            instance.onActionBarChange(message);
        }
    }

    public void onActionBarChange(Component message) {
        if (message == null) return;
        String msg = TextNormalizer.normalizeText(message);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.ACTIONBAR, msg, null, System.currentTimeMillis(), false);
        manager.handleEvent(event);
    }
}
