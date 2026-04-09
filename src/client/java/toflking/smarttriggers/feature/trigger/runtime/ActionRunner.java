package toflking.smarttriggers.feature.trigger.runtime;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;

import java.util.List;

public class ActionRunner {
    private final ActionExecutorContext context;

    ActionRunner(ActionExecutorContext context) {
        this.context = context;
    }

    public void executeAll(List<ExecutableAction> actions, TriggerEvent event) {
        for (ExecutableAction action : actions) {
            action.execute(event, context);
        }

    }}
