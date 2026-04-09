package toflking.smarttriggers.feature.trigger.ui.state;

import toflking.smarttriggers.feature.trigger.config.TriggerActionConfig;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;


public class ActionEditorState {
    private ActionType type;
    private TimerFormat timerType;

    private String text;
    private String key;
    private String value;
    private String soundId;

    public static ActionEditorState fromConfig(TriggerActionConfig config) {
        ActionEditorState action = new ActionEditorState();
        if (config == null) {
            return action;
        }

        action.setType(config.getType());
        action.setTimerType(config.getTimerType());
        action.setText(config.getText());
        action.setKey(config.getKey());
        action.setValue(config.getValue());
        action.setSoundId(config.getSoundId());

        return action;
    }

    public TriggerActionConfig toConfig() {
        TriggerActionConfig action = new TriggerActionConfig();
        action.setType(type);
        switch (action.getType()) {
            case ACTIONBAR, CHAT, COMMAND, TITLE ->
                    action.setText(text);
            case SOUND ->
                    action.setSoundId(soundId);
            case INCREMENT_COUNTER, REMOVE_COUNTER, RESET_COUNTER, SET_COUNTER, REMOVE_FLAG, SET_FLAG, TOGGLE_FLAG,
                 REMOVE_TIMER, RESET_TIMER, STOP_TIMER -> {
                action.setKey(key);
                action.setValue(value);
            }
            case START_TIMER -> {
                action.setKey(key);
                action.setValue(value);
                action.setTimerType(timerType);
            }
        }
        return action;
    }

    public void resetFieldsForType() {
        switch (type) {
            case ACTIONBAR, CHAT, COMMAND, TITLE -> {
                timerType = null;
                text = "";
                key = null;
                value = null;
                soundId = null;
            }
            case SOUND -> {
                timerType = null;
                text = null;
                key = null;
                value = null;
                soundId = "";
            }
            case INCREMENT_COUNTER, REMOVE_COUNTER, RESET_COUNTER, SET_COUNTER, REMOVE_FLAG, SET_FLAG, TOGGLE_FLAG,
                 REMOVE_TIMER, RESET_TIMER, START_TIMER, STOP_TIMER -> {
                timerType = type == ActionType.START_TIMER ? TimerFormat.SECONDS : null;
                text = null;
                key = "";
                value = "";
                soundId = null;
            }
        }
    }

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
        if (this.type != type)  {
            this.type = type;
            resetFieldsForType();
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TimerFormat getTimerType() {
        return timerType;
    }

    public void setTimerType(TimerFormat timerType) {
        this.timerType = timerType;
    }
}
