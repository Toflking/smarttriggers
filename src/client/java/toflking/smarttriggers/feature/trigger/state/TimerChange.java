package toflking.smarttriggers.feature.trigger.state;

public record TimerChange(
        String key,
        boolean existedBefore,
        boolean existsNow,
        long oldRemainingMs,
        long newRemainingMs,
        boolean oldRunning,
        boolean newRunning
) implements StateChange {}
