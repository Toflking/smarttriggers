package toflking.smarttriggers.feature.trigger.action.state.timer;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class RemoveTimerAction implements ExecutableAction {
    private final String timer;

    public RemoveTimerAction(String timer) {
        this.timer = timer;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.getStateStore().removeTimer(timer);
    }
}
