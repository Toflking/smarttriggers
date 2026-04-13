package toflking.smarttriggers.feature.trigger.action.state.timer;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class ResetTimerAction implements ExecutableAction {
    private final String timer;

    public ResetTimerAction(String timer) {
        this.timer = timer;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().resetTimer(timer);
    }
}
