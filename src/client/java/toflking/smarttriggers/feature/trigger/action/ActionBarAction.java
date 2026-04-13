package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class ActionBarAction implements ExecutableAction {
    private final String message;

    public ActionBarAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.showActionBar(message);
    }
}

