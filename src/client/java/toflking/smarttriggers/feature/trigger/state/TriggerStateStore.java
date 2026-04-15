package toflking.smarttriggers.feature.trigger.state;

import java.util.*;
import java.util.function.Consumer;

public class TriggerStateStore {
    private final Map<String, Boolean> flags = new LinkedHashMap<>();
    private final Map<String, Integer> counters = new LinkedHashMap<>();
    private final Map<String, TimerState> timers = new LinkedHashMap<>();

    private final List<Consumer<StateChange>> listeners = new ArrayList<>();

    public void addListener(Consumer<StateChange> listener) {
        listeners.add(listener);
    }

    public void notifyListeners(StateChange stateChange) {
        for (Consumer<StateChange> listener : listeners) {
            listener.accept(stateChange);
        }
    }


    public void setFlag(String flag, boolean value) {
        boolean existedBefore = flags.containsKey(flag);
        boolean oldValue = getFlag(flag);
        if (existedBefore && oldValue == value) {
            return;
        }
        flags.put(flag, value);
        notifyListeners(new FlagChange(flag, existedBefore, true, oldValue, value));
    }

    public boolean getFlag(String flag) {
        if (flags.containsKey(flag)) {
            return flags.get(flag);
        }
        return false;
    }

    public void removeFlag(String flag) {
        if (!flags.containsKey(flag)) {
            return;
        }
        boolean oldValue = flags.remove(flag);
        notifyListeners(new FlagChange(flag, true, false, oldValue, false));
    }

    public void removeAllFlags() {
        flags.clear();
    }

    public void toggleFlag(String flag) {
        setFlag(flag, !getFlag(flag));
    }

    public void incrementCounter(String flag, int amount) {
        setCounter(flag, getCounter(flag) + amount);
    }
    public void setCounter(String flag, int value) {
        boolean existedBefore = counters.containsKey(flag);
        int oldValue = getCounter(flag);
        if (existedBefore && oldValue == value) {
            return;
        }
        counters.put(flag, value);
        notifyListeners(new CounterChange(flag, existedBefore, true, oldValue, value));
    }

    public int getCounter(String flag) {
        if (counters.containsKey(flag)) {
            return counters.get(flag);
        }
        return 0;
    }
    public void resetCounter(String flag) {
        setCounter(flag, 0);
    }

    public void removeCounter(String flag) {
        if (!counters.containsKey(flag)) {
            return;
        }
        int oldValue = counters.remove(flag);
        notifyListeners(new CounterChange(flag, true, false, oldValue, 0));
    }

    public void removeAllCounters() {
        counters.clear();
    }

    public void startTimer(String flag, long durationMs) {
        boolean existedBefore = timers.containsKey(flag);
        TimerState oldState = timers.get(flag);
        long oldRemainingMs = oldState == null ? 0L : oldState.getRemainingMs();
        boolean oldRunning = oldState != null && oldState.isRunning();
        long normalizedDurationMs = Math.max(0L, durationMs);
        long now = System.currentTimeMillis();
        timers.put(flag, new TimerState(normalizedDurationMs, normalizedDurationMs, true, now));
        notifyListeners(new TimerChange(flag, existedBefore, true, oldRemainingMs, normalizedDurationMs, oldRunning, true));
    }
    public void stopTimer(String flag) {
        pauseTimer(flag);
    }
    public void pauseTimer(String flag) {
        TimerState timerState = timers.get(flag);
        if (timerState == null || !timerState.isRunning()) {
            return;
        }
        long oldRemainingMs = timerState.getRemainingMs();
        TimerState paused = timerState.pause();
        timers.put(flag, paused);
        notifyListeners(new TimerChange(flag, true, true, oldRemainingMs, paused.getRemainingMs(), true, false));
    }
    public void resetTimer(String flag) {
        TimerState timerState = timers.get(flag);
        if (timerState != null) {
            long oldRemainingMs = timerState.getRemainingMs();
            boolean oldRunning = timerState.isRunning();
            TimerState reset = timerState.reset();
            timers.put(flag, reset);
            notifyListeners(new TimerChange(flag, true, true, oldRemainingMs, reset.getRemainingMs(), oldRunning, true));
        }
    }
    public void removeTimer(String flag) {
        TimerState timerState = timers.remove(flag);
        if (timerState == null) {
            return;
        }
        notifyListeners(new TimerChange(flag, true, false, timerState.getRemainingMs(), 0L, timerState.isRunning(), false));
    }

    public void removeAllTimers() {
        timers.clear();
    }

    public Map<String, Boolean> getFlags() {
        return Collections.unmodifiableMap(flags);
    }

    public Map<String, Integer> getCounters() {
        return Collections.unmodifiableMap(counters);
    }

    public Map<String, TimerState> getTimers() {
        return Collections.unmodifiableMap(timers);
    }

    public static class TimerState {
        private final long durationMs;
        private final long remainingMs;
        private final boolean running;
        private final long startedAtMs;

        public TimerState(long durationMs, long remainingMs, boolean running, long startedAtMs) {
            this.durationMs = durationMs;
            this.remainingMs = remainingMs;
            this.running = running;
            this.startedAtMs = startedAtMs;
        }

        public boolean isRunning() {
            return running;
        }

        public long getRemainingMs() {
            if (!running) {
                return remainingMs;
            }
            long elapsedMs = System.currentTimeMillis() - startedAtMs;
            return Math.max(0L, remainingMs - elapsedMs);
        }

        public TimerState pause() {
            return new TimerState(durationMs, getRemainingMs(), false, 0L);
        }

        public TimerState reset() {
            return new TimerState(durationMs, durationMs, true, System.currentTimeMillis());
        }
    }
}
