package toflking.smarttriggers.feature.trigger.enums;

public enum TimerFormat {
    SECONDS("s:ms"),
    MINUTES("m:s"),
    HOURS("h:m");

    private final String display;

    TimerFormat(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public TimerFormat next() {
        return switch (this) {
            case SECONDS -> MINUTES;
            case MINUTES -> HOURS;
            case HOURS -> SECONDS;
        };
    }

}
