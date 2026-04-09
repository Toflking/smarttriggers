package toflking.smarttriggers.feature.trigger.config;

import java.util.ArrayList;
import java.util.List;

public class TriggerConfig {
    private List<TriggerRuleConfig> triggerRuleConfigs;

    public List<TriggerRuleConfig> getTriggerRules() {
        return triggerRuleConfigs;
    }

    public void setTriggerRules(List<TriggerRuleConfig> triggerRuleConfigs) {
        this.triggerRuleConfigs = triggerRuleConfigs;
    }

    public static TriggerConfig createDefaultTriggerConfig() {
        TriggerConfig cfg = new TriggerConfig();
        cfg.setTriggerRules(new ArrayList<>());
        return cfg;
    }

    public static TriggerConfig ensureDefaultTriggerConfig(TriggerConfig cfg) {
        if (cfg.getTriggerRules() == null) {
            cfg.setTriggerRules(new ArrayList<>());
        }
        return cfg;
    }
}
