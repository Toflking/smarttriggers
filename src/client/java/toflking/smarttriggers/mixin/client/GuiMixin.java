package toflking.smarttriggers.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toflking.smarttriggers.feature.trigger.source.ActionBarSource;
import toflking.smarttriggers.feature.trigger.source.TitleSource;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "setTitle", at = @At("HEAD"))
    private void onTitle(Component title, CallbackInfo ci) {
        TitleSource.handleTitleStatic(title);
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void onSubtitle(Component subtitle, CallbackInfo ci) {
        TitleSource.handleSubTitleStatic(subtitle);
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onOverlayMessage(Component message, boolean tinted, CallbackInfo ci) {
        ActionBarSource.handleActionBarStatic(message);
    }
}
