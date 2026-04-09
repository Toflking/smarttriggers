package toflking.smarttriggers.feature.trigger.action.state.counter;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class SetCounterAction implements ExecutableAction {
    private final String counter;
    private final int value;

    public SetCounterAction(String counter, int value) {
        this.counter = counter;
        this.value = value;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.getStateStore().setCounter(counter, value);
    }
}
