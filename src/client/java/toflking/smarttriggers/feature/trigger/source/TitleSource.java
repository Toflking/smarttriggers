package toflking.smarttriggers.feature.trigger.source;

import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

public class TitleSource {
    private static TitleSource instance;
    private static boolean suppressCapture;

    private final Manager manager;

    public TitleSource(Manager manager) {
        this.manager = manager;
        instance = this;
    }

    public void register() {
        // User mixin hook
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

    public static void handleTitleStatic(Text title) {
        if (!(instance == null || suppressCapture)) {
            instance.onTitleChange(title);
        }
    }

    public static void handleSubTitleStatic(Text subTitle) {
        if (!(instance == null || suppressCapture)) {
            instance.onSubTitleChange(subTitle);
        }
    }

    public void onTitleChange(Text title) {
        if (title == null) return;
        String msg = TextNormalizer.normalizeText(title);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.TITLE, msg, null, System.currentTimeMillis(), false);
        manager.handleEvent(event);
    }

    public void onSubTitleChange(Text subTitle) {
        if (subTitle == null) return;
        String msg = TextNormalizer.normalizeText(subTitle);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.TITLE, msg, null, System.currentTimeMillis(), true);
        manager.handleEvent(event);
    }
}
