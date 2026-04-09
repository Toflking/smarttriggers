package toflking.smarttriggers.feature.trigger.action;

import toflking.smarttriggers.feature.trigger.runtime.ActionExecutorContext;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

public class SoundAction implements ExecutableAction {
    private final String soundId;

    public SoundAction(String soundId) {
        this.soundId = soundId;
    }

    @Override
    public void execute(TriggerEvent event, ActionExecutorContext ctx) {
        ctx.playSound(soundId);
    }
}
