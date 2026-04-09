package toflking.smarttriggers.feature.trigger.action.state.flag;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class ToggleFlagAction implements ExecutableAction {
    private final String flag;

    public ToggleFlagAction(String flag) {
        this.flag = flag;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.getStateStore().toggleFlag(flag);
    }
}
