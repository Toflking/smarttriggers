package toflking.smarttriggers;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toflking.smarttriggers.core.config.ConfigIO;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.HudCommands;
import toflking.smarttriggers.feature.hud.HudEditController;
import toflking.smarttriggers.feature.hud.HudManager;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.Engine;

public class SmartTriggersClient implements ClientModInitializer {
	public static final String MOD_ID = "smarttriggers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ModConfig config = ConfigIO.loadOrCreateDefaultConfig();
		TriggerStateStore stateStore = new TriggerStateStore();

		HudManager mgr = HudManager.init(config, stateStore);
		HudEditController ctrl = HudEditController.init(mgr, config);
		mgr.setHudEditController(ctrl);

		HudCommands.registerCommands(ctrl);

		Engine engine = new Engine();
		engine.init(config, stateStore, ctrl);
	}
}