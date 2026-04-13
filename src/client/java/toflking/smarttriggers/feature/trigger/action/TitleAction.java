package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class TitleAction implements ExecutableAction {
    private final String title;

    public TitleAction(String title) {
        this.title = title;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.showTitle(title);
    }
}
