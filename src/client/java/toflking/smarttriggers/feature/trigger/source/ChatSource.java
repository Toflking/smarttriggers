package toflking.smarttriggers.feature.trigger.source;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.enums.TextSource;


public class ChatSource {
    private static boolean suppressCapture;

    private final Manager manager;

    public ChatSource(Manager manager) {
        this.manager = manager;
    }

    public void register() {
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> onChatMessage(message));
        ClientReceiveMessageEvents.GAME.register((gameMessage, overlay) -> {
            if (!overlay) {
                onChatMessage(gameMessage);
            }
        });
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

    public void onChatMessage(Text message) {
        if (suppressCapture) return;
        if (message == null) return;
        String msg = TextNormalizer.normalizeText(message);
        if (msg.isBlank()) return;
        TriggerEvent event = new TriggerEvent(TextSource.CHAT, msg, null, System.currentTimeMillis(), false);
        manager.handleEvent(event);
    }
}
