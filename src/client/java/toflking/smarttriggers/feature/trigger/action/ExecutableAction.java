package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public interface ExecutableAction {
    void execute(ActionExecutorContext ctx);
}
