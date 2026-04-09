package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class ActionBarAction implements ExecutableAction {
    private final String message;

    public ActionBarAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.showActionBar(message);
    }
}

