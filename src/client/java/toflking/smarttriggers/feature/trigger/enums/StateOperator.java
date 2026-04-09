package toflking.smarttriggers.feature.trigger.enums;

public enum StateOperator {
    IS("Is", false),
    IS_NOT("Is Not", false),
    GREATER_THAN(">", false),
    GREATER_OR_EQUAL(">=", false),
    LESS_THAN("<", false),
    LESS_OR_EQUAL("<=", false),
    EXISTS("Exists", true),
    MISSING("Missing", true),
    RUNNING("Running", true),
    STOPPED("Stopped", true);

    private final String display;
    private final boolean unary;

    StateOperator(String display, boolean unary) {
        this.display = display;
        this.unary = unary;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isUnary() {
        return unary;
    }

    public StateOperator next() {
        return switch (this) {
            case IS -> IS_NOT;
            case IS_NOT -> GREATER_THAN;
            case GREATER_THAN -> GREATER_OR_EQUAL;
            case GREATER_OR_EQUAL -> LESS_THAN;
            case LESS_THAN -> LESS_OR_EQUAL;
            case LESS_OR_EQUAL -> EXISTS;
            case EXISTS -> MISSING;
            case MISSING -> RUNNING;
            case RUNNING -> STOPPED;
            case STOPPED -> IS;
        };
    }
}
