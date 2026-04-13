package toflking.smarttriggers.feature.trigger.action.state.counter;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class IncrementCounterAction implements ExecutableAction {
    private final String counter;
    private final int amount;

    public IncrementCounterAction(String counter, int amount) {
        this.counter = counter;
        this.amount = amount;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().incrementCounter(counter, amount);
    }
}
