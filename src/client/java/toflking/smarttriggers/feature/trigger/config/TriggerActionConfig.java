package toflking.smarttriggers.feature.trigger.config;

import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;

public class TriggerActionConfig {
    private ActionType type;
    private TimerFormat timerType;
    private String text;
    private String key;
    private String value;
    private String soundId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSoundId() {
        return soundId;
    }

    public void setSoundId(String soundId) {
        this.soundId = soundId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public TimerFormat getTimerType() {
        return timerType;
    }

    public void setTimerType(TimerFormat timerType) {
        this.timerType = timerType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
