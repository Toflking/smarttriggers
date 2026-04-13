package toflking.smarttriggers.feature.trigger.action.state.timer;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class RemoveTimerAction implements ExecutableAction {
    private final String timer;

    public RemoveTimerAction(String timer) {
        this.timer = timer;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().removeTimer(timer);
    }
}
