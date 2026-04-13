package toflking.smarttriggers.feature.trigger.action.state.flag;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class SetFlagAction implements ExecutableAction {
    private final String flag;
    private final boolean value;

    public SetFlagAction(String flag, boolean value) {
        this.flag = flag;
        this.value = value;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.getStateStore().setFlag(flag, value);
    }
}
