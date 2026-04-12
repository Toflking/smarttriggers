package toflking.smarttriggers.feature.trigger.runtime;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import toflking.smarttriggers.feature.trigger.source.ActionBarSource;
import toflking.smarttriggers.feature.trigger.source.ChatSource;
import toflking.smarttriggers.feature.trigger.source.TitleSource;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.text.LegacyFormattingTextParser;

public class ActionExecutorContext {
    TriggerStateStore stateStore;
    MinecraftClient mc = MinecraftClient.getInstance();

    public ActionExecutorContext(TriggerStateStore stateStore) {
        this.stateStore = stateStore;
    }

    public void sendChatMessage(String message) {
        if (mc.player == null) return;
        ChatSource.runWithoutCapture(() -> mc.player.sendMessage(LegacyFormattingTextParser.parse(message), false));
    }

    public void showTitle(String title) {
        if (mc.inGameHud == null) return;
        TitleSource.runWithoutCapture(() -> mc.inGameHud.setTitle(LegacyFormattingTextParser.parse(title)));
    }

    public void showActionBar(String actionBar) {
        if (mc.player == null) return;
        ActionBarSource.runWithoutCapture(() -> mc.player.sendMessage(LegacyFormattingTextParser.parse(actionBar), true));
    }

    public void playSound(String soundId) {
        if (mc.player == null) return;
        SoundEvent soundEvent = Registries.SOUND_EVENT.get(Identifier.of(soundId));
        mc.player.playSound(soundEvent,1.0F, 1.0F);
    }

    public void execteCommand(String command) {
        if (mc.player == null) return;
        String normalized = command.startsWith("/") ? command.substring(1) : command;
        mc.player.networkHandler.sendChatCommand(normalized);
    }

    public TriggerStateStore getStateStore() {
        return stateStore;
    }


}
