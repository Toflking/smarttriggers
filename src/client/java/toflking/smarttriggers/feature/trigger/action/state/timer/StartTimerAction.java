package toflking.smarttriggers.feature.trigger.action.state.timer;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class StartTimerAction implements ExecutableAction {
    private final String timer;
    private final long value;

    public StartTimerAction(String timer, long value) {
        this.timer = timer;
        this.value = value;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().startTimer(timer, value);
    }
}
