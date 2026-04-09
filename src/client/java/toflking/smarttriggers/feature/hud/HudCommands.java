package toflking.smarttriggers.feature.hud;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HudCommands {

    public static void registerCommands(HudEditController controller) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("smarttriggers").then((literal("gui"))
                        .executes(ctx -> {
                            controller.toggleEditMode(null);
                            return 1;
                        }))
        ));
    }
}
