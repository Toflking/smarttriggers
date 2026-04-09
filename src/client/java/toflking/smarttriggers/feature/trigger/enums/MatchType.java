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

    public MatchType previous() {
        return switch (this) {
            case CONTAINS -> REGEX;
            case EQUALS -> CONTAINS;
            case STARTS_WITH -> EQUALS;
            case ENDS_WITH -> STARTS_WITH;
            case REGEX -> ENDS_WITH;
        };
    }
}
