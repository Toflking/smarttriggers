package toflking.smarttriggers.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toflking.smarttriggers.feature.trigger.source.ActionBarSource;
import toflking.smarttriggers.feature.trigger.source.TitleSource;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "setTitle", at = @At("HEAD"))
    private void onTitle(Text title, CallbackInfo ci) {
        TitleSource.handleTitleStatic(title);
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void onSubtitle(Text subtitle, CallbackInfo ci) {
        TitleSource.handleSubTitleStatic(subtitle);
    }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void onOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        ActionBarSource.handleActionBarStatic(message);
    }
}
