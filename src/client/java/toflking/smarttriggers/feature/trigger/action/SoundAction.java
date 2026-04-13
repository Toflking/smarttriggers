package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;

public class SoundAction implements ExecutableAction {
    private final String soundId;

    public SoundAction(String soundId) {
        this.soundId = soundId;
    }

    @Override
    public void execute(ActionExecutorContext ctx) {
        ctx.playSound(soundId);
    }
}
