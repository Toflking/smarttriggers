package toflking.smarttriggers.feature.trigger.action.state.counter;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class ResetCounterAction implements ExecutableAction {
    private final String counter;

    public ResetCounterAction(String counter) {
        this.counter = counter;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().resetCounter(counter);
    }
}
