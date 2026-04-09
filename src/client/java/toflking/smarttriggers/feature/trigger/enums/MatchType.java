package toflking.smarttriggers.feature.trigger.enums;

public enum MatchType {
    CONTAINS("Contains"),
    EQUALS("Equals"),
    STARTS_WITH("Starts With"),
    ENDS_WITH("Ends With"),
    REGEX("Regex");

    private final String display;

    MatchType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public MatchType next() {
        return switch (this) {
            case CONTAINS -> EQUALS;
            case EQUALS -> STARTS_WITH;
            case STARTS_WITH -> ENDS_WITH;
            case ENDS_WITH -> REGEX;
            case REGEX -> CONTAINS;
        };
    }
}
