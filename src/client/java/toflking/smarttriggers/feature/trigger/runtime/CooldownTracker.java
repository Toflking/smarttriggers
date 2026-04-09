package toflking.smarttriggers.feature.trigger.runtime;

import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownTracker {
    Map<String, Long> lastTriggerMs = new ConcurrentHashMap<>();

    public boolean isOnCooldown(CompiledTriggerRule rule, long now) {
        if (!lastTriggerMs.containsKey(rule.getId())) return false;
        return now - lastTriggerMs.get(rule.getId()) < rule.getCooldownMs();
    }

    public void markTriggered(CompiledTriggerRule rule, long now) {
        lastTriggerMs.put(rule.getId(), now);
    }

    public long getLastTriggerTime(CompiledTriggerRule rule) {
        return lastTriggerMs.get(rule.getId());
    }

    public void resetLastTriggerTime(CompiledTriggerRule rule) {
        lastTriggerMs.clear();
    }
}
