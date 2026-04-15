package toflking.smarttriggers.feature.trigger.runtime.matching;

import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;
import toflking.smarttriggers.feature.trigger.state.CounterChange;
import toflking.smarttriggers.feature.trigger.state.FlagChange;
import toflking.smarttriggers.feature.trigger.state.StateChange;
import toflking.smarttriggers.feature.trigger.state.TimerChange;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;

import static toflking.smarttriggers.feature.trigger.enums.StateOperator.GREATER_OR_EQUAL;
import static toflking.smarttriggers.feature.trigger.enums.StateOperator.GREATER_THAN;
import static toflking.smarttriggers.feature.trigger.enums.StateOperator.IS;
import static toflking.smarttriggers.feature.trigger.enums.StateOperator.IS_NOT;
import static toflking.smarttriggers.feature.trigger.enums.StateOperator.LESS_OR_EQUAL;
import static toflking.smarttriggers.feature.trigger.enums.StateOperator.LESS_THAN;

public class OperatorComparer {

    public boolean matches(CompiledTriggerRule rule, StateChange change, TriggerStateStore stateStore) {
        if (!isCompatible(rule.getInputType(), change)) {
            return false;
        }
        if (!hasMatchingKey(rule, change.key())) {
            return false;
        }

        return switch (rule.getInputType()) {
            case FLAG -> matchesFlag(rule, (FlagChange) change);
            case COUNTER -> matchesCounter(rule, (CounterChange) change);
            case TIMER -> matchesTimer(rule, (TimerChange) change, stateStore);
            case TEXT -> false;
        };
    }

    private boolean matchesFlag(CompiledTriggerRule rule, FlagChange change) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null) {
            return false;
        }

        if (!change.existsNow()) {
            return false;
        }

        return switch (operator) {
            case EXISTS -> change.existsNow();
            case MISSING -> !change.existsNow();
            case IS -> parseBoolean(rule.getPattern()) == change.newValue();
            case IS_NOT -> parseBoolean(rule.getPattern()) != change.newValue();
            default -> false;
        };
    }

    private boolean matchesCounter(CompiledTriggerRule rule, CounterChange change) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null) {
            return false;
        }

        if (!change.existsNow()) {
            return operator == StateOperator.MISSING;
        }

        return switch (operator) {
            case EXISTS -> change.existsNow();
            case MISSING -> !change.existsNow();
            case IS -> compareLong(change.newValue(), parseLong(rule.getPattern()), IS);
            case IS_NOT -> compareLong(change.newValue(), parseLong(rule.getPattern()), IS_NOT);
            case GREATER_THAN -> compareLong(change.newValue(), parseLong(rule.getPattern()), GREATER_THAN);
            case GREATER_OR_EQUAL -> compareLong(change.newValue(), parseLong(rule.getPattern()), GREATER_OR_EQUAL);
            case LESS_THAN -> compareLong(change.newValue(), parseLong(rule.getPattern()), LESS_THAN);
            case LESS_OR_EQUAL -> compareLong(change.newValue(), parseLong(rule.getPattern()), LESS_OR_EQUAL);
            default -> false;
        };
    }

    private boolean matchesTimer(CompiledTriggerRule rule, TimerChange change, TriggerStateStore stateStore) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null) {
            return false;
        }

        if (!change.existsNow()) {
            return operator == StateOperator.MISSING;
        }

        TriggerStateStore.TimerState timerState = stateStore.getTimers().get(change.key());
        long remainingMs = timerState == null ? 0L : timerState.getRemainingMs();
        boolean running = timerState != null && timerState.isRunning();

        return switch (operator) {
            case RUNNING -> running;
            case STOPPED -> !running;
            case IS -> compareLong(remainingMs, parseLong(rule.getPattern()), IS);
            case IS_NOT -> compareLong(remainingMs, parseLong(rule.getPattern()), IS_NOT);
            case GREATER_THAN -> compareLong(remainingMs, parseLong(rule.getPattern()), GREATER_THAN);
            case GREATER_OR_EQUAL -> compareLong(remainingMs, parseLong(rule.getPattern()), GREATER_OR_EQUAL);
            case LESS_THAN -> compareLong(remainingMs, parseLong(rule.getPattern()), LESS_THAN);
            case LESS_OR_EQUAL -> compareLong(remainingMs, parseLong(rule.getPattern()), LESS_OR_EQUAL);
            default -> false;
        };
    }

    private boolean isCompatible(RuleInputType inputType, StateChange change) {
        return switch (inputType) {
            case FLAG -> change instanceof FlagChange;
            case COUNTER -> change instanceof CounterChange;
            case TIMER -> change instanceof TimerChange;
            case TEXT -> false;
        };
    }

    private boolean hasMatchingKey(CompiledTriggerRule rule, String key) {
        return rule.getKey() != null && rule.getKey().equals(key);
    }

    private boolean compareLong(long actual, long expected, StateOperator operator) {
        return switch (operator) {
            case IS -> actual == expected;
            case IS_NOT -> actual != expected;
            case GREATER_THAN -> actual > expected;
            case GREATER_OR_EQUAL -> actual >= expected;
            case LESS_THAN -> actual < expected;
            case LESS_OR_EQUAL -> actual <= expected;
            default -> false;
        };
    }

    private boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value == null ? "" : value.trim());
    }

    private long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ignored) {
            return 0L;
        }
    }
}
