package toflking.smarttriggers.feature.trigger.enums;

public enum RuleInputType {
    TEXT("Text Event"),
    FLAG("Flag"),
    COUNTER("Counter"),
    TIMER("Timer");

    private final String display;

    RuleInputType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public RuleInputType next() {
        return switch (this) {
            case TEXT -> FLAG;
            case FLAG -> COUNTER;
            case COUNTER -> TIMER;
            case TIMER -> TEXT;
        };
    }

    public RuleInputType previous() {
        return switch (this) {
            case TEXT -> TIMER;
            case FLAG -> TEXT;
            case COUNTER -> FLAG;
            case TIMER -> COUNTER;
        };
    }
}
