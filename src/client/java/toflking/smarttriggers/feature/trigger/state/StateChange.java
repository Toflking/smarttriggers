package toflking.smarttriggers.feature.trigger.state;

public sealed interface StateChange permits FlagChange, CounterChange, TimerChange {
    String key();
}
