package toflking.smarttriggers.feature.trigger.enums;

public enum ActionType {
    CHAT("Chat"),
    TITLE("Title"),
    ACTIONBAR("Action Bar"),
    SOUND("Sound"),
    COMMAND("Command"),

    SET_FLAG("Set Flag"),
    TOGGLE_FLAG("Toggle Flag"),
    REMOVE_FLAG("Remove Flag"),

    START_TIMER("Start Timer"),
    STOP_TIMER("Stop Timer"),
    RESET_TIMER("Reset Timer"),
    REMOVE_TIMER("Remove Timer"),

    INCREMENT_COUNTER("Increment Counter"),
    SET_COUNTER("Set Counter"),
    RESET_COUNTER("Reset Counter"),
    REMOVE_COUNTER("Remove Counter");

    private final String display;

    ActionType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public ActionType next() {
        return switch (this) {
            case CHAT -> TITLE;
            case TITLE -> ACTIONBAR;
            case ACTIONBAR -> SOUND;
            case SOUND -> COMMAND;
            case COMMAND -> SET_FLAG;

            case SET_FLAG -> TOGGLE_FLAG;
            case TOGGLE_FLAG -> REMOVE_FLAG;
            case REMOVE_FLAG -> START_TIMER;

            case START_TIMER -> STOP_TIMER;
            case STOP_TIMER -> RESET_TIMER;
            case RESET_TIMER -> REMOVE_TIMER;

            case REMOVE_TIMER -> INCREMENT_COUNTER;
            case INCREMENT_COUNTER -> SET_COUNTER;
            case SET_COUNTER -> RESET_COUNTER;
            case RESET_COUNTER -> REMOVE_COUNTER;
            case REMOVE_COUNTER -> CHAT;
        };
    }

    public ActionType previous() {
        return switch (this) {
            case CHAT -> REMOVE_COUNTER;
            case TITLE -> CHAT;
            case ACTIONBAR -> TITLE;
            case SOUND -> ACTIONBAR;
            case COMMAND -> SOUND;

            case SET_FLAG -> COMMAND;
            case TOGGLE_FLAG -> SET_FLAG;
            case REMOVE_FLAG -> TOGGLE_FLAG;

            case START_TIMER -> REMOVE_FLAG;
            case STOP_TIMER -> START_TIMER;
            case RESET_TIMER -> STOP_TIMER;

            case REMOVE_TIMER -> RESET_TIMER;
            case INCREMENT_COUNTER -> REMOVE_TIMER;
            case SET_COUNTER -> INCREMENT_COUNTER;
            case RESET_COUNTER -> SET_COUNTER;
            case REMOVE_COUNTER -> RESET_COUNTER;
        };
    }
}
