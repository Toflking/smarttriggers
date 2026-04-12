package toflking.smarttriggers.feature.trigger.runtime;

import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;
import toflking.smarttriggers.feature.trigger.runtime.matching.Matcher;
import toflking.smarttriggers.feature.trigger.runtime.matching.OperatorComparer;
import toflking.smarttriggers.feature.trigger.state.StateChange;
import toflking.smarttriggers.feature.trigger.state.TimerChange;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    private final Matcher matcher;
    private final ActionRunner executor;
    private final CooldownTracker cooldownTracker;
    private final TriggerStateStore stateStore;
    private final OperatorComparer operatorComparer;
    private List<CompiledTriggerRule> rules;

    public Manager(Matcher matcher, ActionExecutorContext ctx, CooldownTracker tracker, TriggerStateStore stateStore, List<CompiledTriggerRule> rules) {
        this.matcher = matcher;
        this.executor = new ActionRunner(ctx);
        this.cooldownTracker = tracker;
        this.stateStore = stateStore;
        this.operatorComparer = new OperatorComparer();
        this.rules = rules;
    }

    public void handleEvent(TriggerEvent event) {
        for (CompiledTriggerRule rule : getRulesForSource(event.source())) {
            processRule(rule, event, System.currentTimeMillis());
        }
    }

    public void processRule(CompiledTriggerRule rule, TriggerEvent event, long now) {
        if (!rule.isEnabled()) return;
        if (!matcher.matches(rule, event)) return;
        if (cooldownTracker.isOnCooldown(rule, now)) return;
        executor.executeAllActions(rule.getActions(), event);
        cooldownTracker.markTriggered(rule, now);
    }

    public void onStateChanged(StateChange change) {
        long now = System.currentTimeMillis();
        for (CompiledTriggerRule rule : rules) {
            if (!isMatchingStateRule(rule, change)) {
                continue;
            }
            if (cooldownTracker.isOnCooldown(rule, now)) {
                continue;
            }
            if (!operatorComparer.matches(rule, change, stateStore)) {
                continue;
            }
            executor.executeAllActions(rule.getActions(), new TriggerEvent(null, null, null, now, false));
            cooldownTracker.markTriggered(rule, now);
        }
    }

    public void tickTimers() {
        long now = System.currentTimeMillis();
        for (CompiledTriggerRule rule : rules) {
            if (!rule.isEnabled() || rule.getInputType() != RuleInputType.TIMER || rule.getKey() == null) {
                continue;
            }
            if (!stateStore.getTimers().containsKey(rule.getKey())) {
                continue;
            }
            if (cooldownTracker.isOnCooldown(rule, now)) {
                continue;
            }
            TimerChange snapshot = createTimerSnapshot(rule.getKey(), now);
            if (!operatorComparer.matches(rule, snapshot, stateStore)) {
                continue;
            }
            executor.executeAllActions(rule.getActions(), new TriggerEvent(null, null, null, now, false));
            cooldownTracker.markTriggered(rule, now);
        }
    }

    public List<CompiledTriggerRule> getRulesForSource(TextSource source) {
        List<CompiledTriggerRule> rules = new ArrayList<>();
        for (CompiledTriggerRule rule : this.rules) {
            if (rule.getInputType() == RuleInputType.TEXT && rule.getSource() == source) {
                rules.add(rule);
            }
        }
        return rules;
    }

    public void reloadRules(List<CompiledTriggerRule> rules) {
        this.rules = rules;
    }

    public TriggerStateStore getStateStore() {
        return stateStore;
    }

    private boolean isMatchingStateRule(CompiledTriggerRule rule, StateChange change) {
        return rule.isEnabled()
                && rule.getInputType() != null
                && rule.getInputType() != RuleInputType.TEXT
                && rule.getKey() != null
                && rule.getKey().equals(change.key());
    }

    private TimerChange createTimerSnapshot(String key, long now) {
        TriggerStateStore.TimerState timerState = stateStore.getTimers().get(key);
        long remainingMs = timerState == null ? 0L : timerState.getRemainingMs();
        boolean running = timerState != null && timerState.isRunning();
        return new TimerChange(key, timerState != null, timerState != null, remainingMs, remainingMs, running, running, now);
    }
}
