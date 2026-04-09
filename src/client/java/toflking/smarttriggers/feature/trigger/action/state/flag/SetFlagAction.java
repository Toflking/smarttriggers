package toflking.smarttriggers.feature.trigger.action.state.flag;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class SetFlagAction implements ExecutableAction {
    private final String flag;
    private final boolean value;

    public SetFlagAction(String flag, boolean value) {
        this.flag = flag;
        this.value = value;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.getStateStore().setFlag(flag, value);
    }
}
