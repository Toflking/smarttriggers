package toflking.smarttriggers.feature.trigger.compilation;

import toflking.smarttriggers.feature.trigger.config.TriggerActionConfig;
import toflking.smarttriggers.feature.trigger.action.*;
import toflking.smarttriggers.feature.trigger.action.state.counter.IncrementCounterAction;
import toflking.smarttriggers.feature.trigger.action.state.counter.RemoveCounterAction;
import toflking.smarttriggers.feature.trigger.action.state.counter.ResetCounterAction;
import toflking.smarttriggers.feature.trigger.action.state.counter.SetCounterAction;
import toflking.smarttriggers.feature.trigger.action.state.flag.RemoveFlagAction;
import toflking.smarttriggers.feature.trigger.action.state.flag.SetFlagAction;
import toflking.smarttriggers.feature.trigger.action.state.flag.ToggleFlagAction;
import toflking.smarttriggers.feature.trigger.action.state.timer.RemoveTimerAction;
import toflking.smarttriggers.feature.trigger.action.state.timer.ResetTimerAction;
import toflking.smarttriggers.feature.trigger.action.state.timer.StartTimerAction;
import toflking.smarttriggers.feature.trigger.action.state.timer.StopTimerAction;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.time.DurationCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActionFactory {
    private final Map<ActionType, Function<TriggerActionConfig, ExecutableAction>> builders;

    public ActionFactory() {
        this.builders = new HashMap<>();
        builders.put(ActionType.CHAT, cfg -> new ChatAction(cfg.getText()));
        builders.put(ActionType.TITLE, cfg -> new TitleAction(cfg.getText()));
        builders.put(ActionType.ACTIONBAR, cfg -> new ActionBarAction(cfg.getText()));
        builders.put(ActionType.SOUND, cfg -> new SoundAction(cfg.getSoundId()));
        builders.put(ActionType.COMMAND, cfg -> new CommandAction(cfg.getText()));

        builders.put(ActionType.SET_FLAG, cfg -> new SetFlagAction(cfg.getKey(), Boolean.parseBoolean(cfg.getValue())));
        builders.put(ActionType.TOGGLE_FLAG, cfg -> new ToggleFlagAction(cfg.getKey()));
        builders.put(ActionType.REMOVE_FLAG, cfg -> new RemoveFlagAction(cfg.getKey()));

        builders.put(ActionType.SET_COUNTER, cfg -> new SetCounterAction(cfg.getKey(), parseIntArg(cfg)));
        builders.put(ActionType.INCREMENT_COUNTER, cfg -> new IncrementCounterAction(cfg.getKey(), parseIntArg(cfg)));
        builders.put(ActionType.RESET_COUNTER, cfg -> new ResetCounterAction(cfg.getKey()));
        builders.put(ActionType.REMOVE_COUNTER, cfg -> new RemoveCounterAction(cfg.getKey()));

        builders.put(ActionType.START_TIMER, cfg -> new StartTimerAction(cfg.getKey(), parseDurationArg(cfg)));
        builders.put(ActionType.STOP_TIMER, cfg -> new StopTimerAction(cfg.getKey()));
        builders.put(ActionType.RESET_TIMER, cfg -> new ResetTimerAction(cfg.getKey()));
        builders.put(ActionType.REMOVE_TIMER, cfg -> new RemoveTimerAction(cfg.getKey()));

    }

    public ExecutableAction create(TriggerActionConfig cfg) {
        Function<TriggerActionConfig, ExecutableAction> builder = builders.get(cfg.getType());

        if (builder == null) {
            throw new IllegalArgumentException(String.format("Unknown trigger action type: %s", cfg.getType()));
        }

        return builder.apply(cfg);
    }

    private static int parseIntArg(TriggerActionConfig cfg) {
        try {
            return Integer.parseInt(cfg.getValue());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    String.format(
                            "Trigger action %s requires an integer value, got '%s' for key '%s'",
                            cfg.getType(), cfg.getValue(), cfg.getKey()
                    ),
                    ex
            );
        }
    }

    private static long parseDurationArg(TriggerActionConfig cfg) {
        return DurationCodec.parseToMillis(cfg.getValue(), cfg.getTimerType());
    }
}
