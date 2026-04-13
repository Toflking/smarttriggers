package toflking.smarttriggers.feature.trigger.action.state.flag;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class RemoveFlagAction implements ExecutableAction {
    private final String flag;

    public RemoveFlagAction(String flag) {
        this.flag = flag;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().removeFlag(flag);
    }
}
