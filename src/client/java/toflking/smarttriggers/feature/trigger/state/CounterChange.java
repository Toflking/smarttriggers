package toflking.smarttriggers.feature.trigger.state;

public record CounterChange(
        String key,
        boolean existedBefore,
        boolean existsNow,
        int oldValue,
        int newValue
) implements StateChange {}
