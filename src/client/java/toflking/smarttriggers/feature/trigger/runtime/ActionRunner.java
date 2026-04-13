package toflking.smarttriggers.feature.trigger.runtime;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;

import java.util.List;

public class ActionRunner {
    private final ActionExecutorContext context;

    ActionRunner(ActionExecutorContext context) {
        this.context = context;
    }

    public void executeAllActions(List<ExecutableAction> actions) {
        for (ExecutableAction action : actions) {
            action.execute(context);
        }

    }}
