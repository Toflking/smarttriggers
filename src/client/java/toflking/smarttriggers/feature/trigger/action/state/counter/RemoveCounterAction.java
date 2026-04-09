package toflking.smarttriggers.feature.trigger.action.state.counter;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class RemoveCounterAction implements ExecutableAction {
    private final String counter;

    public RemoveCounterAction(String counter) {
        this.counter = counter;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.getStateStore().removeCounter(counter);
    }
}
