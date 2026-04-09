package toflking.smarttriggers.feature.trigger;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;
import toflking.smarttriggers.feature.trigger.compilation.ActionFactory;
import toflking.smarttriggers.feature.trigger.compilation.Compiler;
import toflking.smarttriggers.feature.hud.HudEditController;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.CooldownTracker;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.matching.Matcher;
import toflking.smarttriggers.feature.trigger.source.*;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.ui.TriggerRulesController;
import toflking.smarttriggers.feature.trigger.ui.TriggerRulesScreen;
import toflking.smarttriggers.feature.trigger.validation.config.TriggerConfigValidator;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;
import toflking.smarttriggers.feature.trigger.validation.ValidationResult;

import java.util.List;

public class Engine implements RuntimeReloader {
    private Manager manager;
    private Compiler compiler;

    public void init(ModConfig config, TriggerStateStore stateStore, HudEditController hudEditController) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ActionExecutorContext context = new ActionExecutorContext(stateStore);
        ActionFactory factory = new ActionFactory();
        compiler = new Compiler(factory);
        Matcher matcher = new Matcher();
        CooldownTracker cooldownTracker = new CooldownTracker();
        TriggerRulesController controller = new TriggerRulesController(config, hudEditController);
        List<CompiledTriggerRule> compiledTriggerRules = compiler.compile(config.getTrigger());
        manager = new Manager(matcher, context, cooldownTracker, stateStore, compiledTriggerRules);
        stateStore.addListener(manager::onStateChanged);
        ClientTickEvents.END_CLIENT_TICK.register(client -> manager.tickTimers());
        registerSources(manager);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                ClientCommandManager.literal("smarttriggers")
                        .executes(ctx -> {
                            mc.execute(() -> mc.setScreen(new TriggerRulesScreen(mc.currentScreen, controller, this)));
                            return 1;
                        })
        ));
    }

    private void registerSources(Manager manager) {
        ActionBarSource actionBarSource = new ActionBarSource(manager);
        ChatSource chatSource = new ChatSource(manager);
        ScoreboardSource scoreboardSource = new ScoreboardSource(manager);
        TabListSource tabListSource = new TabListSource(manager);
        TitleSource titleSource = new TitleSource(manager);
        actionBarSource.register();
        chatSource.register();
        scoreboardSource.register();
        tabListSource.register();
        titleSource.register();
    }

    @Override
    public ValidationResult reload(ModConfig config) {
        List<ValidationIssue> issues = TriggerConfigValidator.validateConfig(config.getTrigger());
        if (!issues.isEmpty()) {
            return ValidationResult.errors(issues);
        }

        try {
            List<CompiledTriggerRule> compiledTriggerRules = compiler.compile(config.getTrigger());
            manager.reloadRules(compiledTriggerRules);
            return ValidationResult.ok();
        } catch (IllegalArgumentException e) {
            return ValidationResult.error(e.getMessage());
        }
    }
}
