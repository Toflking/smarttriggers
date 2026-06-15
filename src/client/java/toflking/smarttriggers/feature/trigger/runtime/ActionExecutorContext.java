package toflking.smarttriggers.feature.trigger.runtime;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import toflking.smarttriggers.feature.trigger.source.ActionBarSource;
import toflking.smarttriggers.feature.trigger.source.ChatSource;
import toflking.smarttriggers.feature.trigger.source.TitleSource;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.text.LegacyFormattingTextParser;

public class ActionExecutorContext {
    private final TriggerStateStore stateStore;
    private final Minecraft mc = Minecraft.getInstance();

    public ActionExecutorContext(TriggerStateStore stateStore) {
        this.stateStore = stateStore;
    }

    public void sendChatMessage(String message) {
        if (mc.player == null) return;
        ChatSource.runWithoutCapture(() -> mc.player.sendSystemMessage(LegacyFormattingTextParser.parse(message)));
    }

    public void showTitle(String title) {
        TitleSource.runWithoutCapture(() -> mc.gui.setTitle(LegacyFormattingTextParser.parse(title)));
    }

    public void showActionBar(String actionBar) {
        if (mc.player == null) return;
        ActionBarSource.runWithoutCapture(() -> mc.player.sendOverlayMessage(LegacyFormattingTextParser.parse(actionBar)));
    }

    public void playSound(String soundId) {
        if (mc.player == null) return;
        Identifier id = Identifier.tryParse(soundId);
        if (id == null) return;
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(id);
        if (soundEvent == null) return;
        mc.player.playSound(soundEvent, 1.0F, 1.0F);
    }

    public void executeCommand(String command) {
        if (mc.player == null) return;
        String normalized = command.startsWith("/") ? command.substring(1) : command;
        mc.player.connection.sendCommand(normalized);
    }

    public TriggerStateStore getStateStore() {
        return stateStore;
    }


}
