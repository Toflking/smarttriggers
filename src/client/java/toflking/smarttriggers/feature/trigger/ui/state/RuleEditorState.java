package toflking.smarttriggers.feature.trigger.ui.state;

import toflking.smarttriggers.feature.trigger.config.TriggerActionConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerRuleConfig;
import toflking.smarttriggers.feature.trigger.enums.*;
import toflking.smarttriggers.feature.trigger.time.DurationCodec;

import java.util.ArrayList;
import java.util.List;

public class RuleEditorState {
    private String id;
    private boolean enabled;
    private boolean expanded;

    private RuleInputType inputType;
    private TextSource source;
    private MatchType matchType;
    private StateOperator stateOperator;
    private String key;
    private String pattern;
    private boolean caseSensitive;
    private String cooldownString;
    private TimerFormat cooldownType;

    private final List<ActionEditorState> actions = new ArrayList<>();

    public static RuleEditorState fromConfig(TriggerRuleConfig config) {
        RuleEditorState rule = new RuleEditorState();
        if (config == null) {
            rule.setInputType(RuleInputType.TEXT);
            rule.setStateOperator(StateOperator.IS);
            rule.setKey("");
            rule.setCooldownString("0:00");
            rule.setCooldownType(TimerFormat.SECONDS);
            return rule;
        }
        rule.setId(config.getId());
        rule.setEnabled(config.isEnabled());
        rule.setInputType(config.getInputType() == null ? RuleInputType.TEXT : config.getInputType());
        rule.setSource(config.getSource());
        rule.setMatchType(config.getMatchType());
        rule.setStateOperator(config.getStateOperator() == null ? StateOperator.IS : config.getStateOperator());
        rule.setKey(config.getKey());
        rule.setPattern(config.getPattern());
        rule.setCaseSensitive(config.isCaseSensitive());
        rule.convertAndSetCooldown(config.getCooldownMs());
        if (config.getActions() != null) {
            for (TriggerActionConfig actionConfig : config.getActions()) {
                if (actionConfig != null) {
                    rule.getActions().add(ActionEditorState.fromConfig(actionConfig));
                }
            }
        }
        return rule;
    }

    public TriggerRuleConfig toTriggerConfig() {
        TriggerRuleConfig rule = new TriggerRuleConfig();
        rule.setId(id);
        rule.setEnabled(enabled);
        rule.setInputType(inputType);
        rule.setSource(source);
        rule.setMatchType(matchType);
        rule.setStateOperator(stateOperator);
        rule.setKey(key);
        rule.setPattern(pattern);
        rule.setCaseSensitive(caseSensitive);
        rule.setCooldownMs(parseCooldownOrDefault());
        List<TriggerActionConfig> actionConfigs = new ArrayList<>();
        for (ActionEditorState action : actions) {
            actionConfigs.add(action.toConfig());
        }
        rule.setActions(actionConfigs);
        return rule;
    }

    private void convertAndSetCooldown(long cooldownMs) {
        DurationCodec.FormattedDuration formattedCooldown = DurationCodec.formatFromMillis(cooldownMs);
        cooldownType = formattedCooldown.format();
        cooldownString = formattedCooldown.value();
    }

    private long parseCooldownOrDefault() {
        if (cooldownString == null || cooldownType == null || cooldownString.trim().isEmpty()) {
            return 0L;
        }

        try {
            return DurationCodec.parseToMillis(cooldownString, cooldownType);
        } catch (IllegalArgumentException ignored) {
            return 0L;
        }
    }

    public List<ActionEditorState> getActions() {
        return actions;
    }

    public RuleInputType getInputType() {
        return inputType;
    }

    public void setInputType(RuleInputType inputType) {
        this.inputType = inputType;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getCooldownString() {
        return cooldownString;
    }

    public void setCooldownString(String cooldownString) {
        this.cooldownString = cooldownString;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public StateOperator getStateOperator() {
        return stateOperator;
    }

    public void setStateOperator(StateOperator stateOperator) {
        this.stateOperator = stateOperator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TextSource getSource() {
        return source;
    }

    public void setSource(TextSource source) {
        this.source = source;
    }

    public TimerFormat getCooldownType() {
        return cooldownType;
    }

    public void setCooldownType(TimerFormat cooldownType) {
        this.cooldownType = cooldownType;
    }

    public String isExpandedDisplay() {
        if (expanded) {
            return "▼";
        } else {
            return "◀";
        }
    }
}
