package toflking.smarttriggers.feature.trigger.ui;

import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerRuleConfig;
import toflking.smarttriggers.feature.hud.HudEditController;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.MatchType;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;
import toflking.smarttriggers.feature.trigger.enums.TextSource;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;

import java.util.ArrayList;
import java.util.List;

public class TriggerRulesController {
    private final ModConfig config;
    private final List<RuleEditorState> rules = new ArrayList<>();
    private final HudEditController hudEditController;

    public boolean dirty;

    public TriggerRulesController(ModConfig config, HudEditController hudEditController) {
        this.config = config;
        this.hudEditController = hudEditController;
        loadFromConfig();
    }

    public List<RuleEditorState> getRules() {
        return rules;
    }

    public ModConfig getConfig() {
        return config;
    }

    public HudEditController getHudEditController() {
        return hudEditController;
    }

    public void loadFromConfig() {
        rules.clear();
        for (TriggerRuleConfig rule : config.getTrigger().getTriggerRules()) {
            rules.add(RuleEditorState.fromConfig(rule));
        }
    }

    public void saveToConfig() {
        if (!dirty) return;
        TriggerConfig triggerConfig = config.getTrigger();
        List<TriggerRuleConfig> triggerRuleConfigs = new ArrayList<>();
        for (RuleEditorState rule : rules) {
            triggerRuleConfigs.add(rule.toTriggerConfig());
        }
        triggerConfig.setTriggerRules(triggerRuleConfigs);
        config.setTrigger(triggerConfig);
        dirty  = false;
    }

    public void addRule() {
        RuleEditorState rule = new RuleEditorState();
        rule.setId(createNextRuleId());
        rule.setEnabled(true);
        rule.setExpanded(true);
        rule.setInputType(RuleInputType.TEXT);
        rule.setSource(TextSource.CHAT);
        rule.setMatchType(MatchType.CONTAINS);
        rule.setStateOperator(StateOperator.IS);
        rule.setKey("");
        rule.setPattern("");
        rule.setCaseSensitive(false);
        rule.setCooldownString("0:00");
        rule.setCooldownType(TimerFormat.SECONDS);

        ActionEditorState action = new ActionEditorState();
        action.setType(ActionType.CHAT);
        action.setText("");

        rule.getActions().add(action);
        rules.add(rule);
        dirty = true;
    }

    public void removeRule(RuleEditorState rule) {
        rules.remove(rule);
        dirty = true;
    }

    private String createNextRuleId() {
        String base = "new_rule";
        if (isRuleAvailable(base)) {
            return base;
        }

        int index = 2;
        while (!isRuleAvailable(base + "_" + index)) {
            index++;
        }
        return base + "_" + index;

    }

    private boolean isRuleAvailable(String candidate) {
        for (RuleEditorState rule : rules) {
            if (rule.getId() != null && rule.getId().trim().equalsIgnoreCase(candidate)) {
                return false;
            }
        }
        return true;
    }
}
