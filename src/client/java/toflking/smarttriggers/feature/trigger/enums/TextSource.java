package toflking.smarttriggers.feature.trigger.enums;

public enum TextSource {
    CHAT("Chat"),
    ACTIONBAR("Action Bar"),
    TITLE("Title"),
    SCOREBOARD("Scoreboard"),
    TABLIST("Tablist");

    private final String display;

    TextSource(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public TextSource next() {
        return switch (this) {
            case CHAT -> ACTIONBAR;
            case ACTIONBAR -> TITLE;
            case TITLE -> SCOREBOARD;
            case SCOREBOARD -> TABLIST;
            case TABLIST -> CHAT;
        };
    }
}