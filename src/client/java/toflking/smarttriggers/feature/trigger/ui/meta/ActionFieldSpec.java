package toflking.smarttriggers.feature.trigger.ui.meta;

public record ActionFieldSpec(
        String key,
        String label,
        ActionFieldType type,
        boolean required
) {
    public enum ActionFieldType {
        TEXT,
        INTEGER,
        BOOLEAN
    }
}
