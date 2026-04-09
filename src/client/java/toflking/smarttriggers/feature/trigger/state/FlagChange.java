package toflking.smarttriggers.feature.trigger.state;

public record FlagChange(
        String key,
        boolean existedBefore,
        boolean existsNow,
        boolean oldValue,
        boolean newValue,
        long timestampMs
) implements StateChange {}
