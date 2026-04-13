package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class CommandAction implements ExecutableAction {
    private final String command;

    public CommandAction(String command) {
        this.command = command;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.executeCommand(command);
    }
}
