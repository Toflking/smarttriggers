package toflking.smarttriggers.feature.trigger.action.state.counter;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class RemoveCounterAction implements ExecutableAction {
    private final String counter;

    public RemoveCounterAction(String counter) {
        this.counter = counter;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().removeCounter(counter);
    }
}
