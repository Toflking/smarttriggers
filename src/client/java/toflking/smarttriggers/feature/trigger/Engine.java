package toflking.smarttriggers.feature.trigger;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.HudEditController;
import toflking.smarttriggers.feature.trigger.compilation.ActionFactory;
import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;
import toflking.smarttriggers.feature.trigger.compilation.Compiler;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.CooldownTracker;
import toflking.smarttriggers.feature.trigger.runtime.Manager;
import toflking.smarttriggers.feature.trigger.runtime.matching.Matcher;
import toflking.smarttriggers.feature.trigger.source.*;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.ui.TriggerRulesController;
import toflking.smarttriggers.feature.trigger.ui.screen.TriggerRulesScreen;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;
import toflking.smarttriggers.feature.trigger.validation.ValidationResult;
import toflking.smarttriggers.feature.trigger.validation.config.TriggerConfigValidator;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

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
        registerCommands(mc, controller, stateStore, hudEditController);
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

    private void registerCommands(MinecraftClient mc, TriggerRulesController controller, TriggerStateStore stateStore, HudEditController hudEditController) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("smarttriggers")
                        .executes(ctx -> {
                            mc.execute(() -> mc.setScreen(new TriggerRulesScreen(mc.currentScreen, controller, this)));
                            return 1;
                        })
                        .then(literal("clear")
                                .then(literal("all")
                                        .executes(context -> {
                                            stateStore.removeAllFlags();
                                            stateStore.removeAllCounters();
                                            stateStore.removeAllTimers();
                                            return 1;
                                        })
                                )
                                .then(literal("flags")
                                        .executes(context -> {
                                            stateStore.removeAllFlags();
                                            return 1;
                                        })
                                        .then(argument("key", StringArgumentType.word())
                                                .executes(context -> {
                                                    stateStore.removeFlag(StringArgumentType.getString(context, "key"));
                                                    return 1;
                                                })
                                        )

                                )
                                .then(literal("timers")
                                        .executes(context -> {
                                            stateStore.removeAllTimers();
                                            return 1;
                                        })
                                        .then(argument("key", StringArgumentType.word())
                                                .executes(context -> {
                                                    stateStore.removeTimer(StringArgumentType.getString(context, "key"));
                                                    return 1;
                                                })
                                        )

                                )
                                .then(literal("counters")
                                        .executes(context -> {
                                            stateStore.removeAllCounters();
                                            return 1;
                                        })
                                        .then(argument("key", StringArgumentType.word())
                                                .executes(context -> {
                                                    stateStore.removeCounter(StringArgumentType.getString(context, "key"));
                                                    return 1;
                                                })
                                        )

                                )
                        )
                        .then(literal("gui")
                                .executes(context -> {
                                    hudEditController.toggleEditMode(null);
                                    return 1;
                                })
                        )
        ));
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
