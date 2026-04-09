package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public interface ExecutableAction {
    void execute(TriggerEvent event, ActionExecutorContext ctx);
}
