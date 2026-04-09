package toflking.smarttriggers.feature.trigger.compilation;

import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.enums.MatchType;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;
import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.ArrayList;
import java.util.List;

public class CompiledTriggerRule {
    private String id;
    private boolean enabled;
    private RuleInputType inputType;
    private TextSource source;
    private MatchType matchType;
    private StateOperator stateOperator;
    private String key;
    private String pattern;
    private boolean caseSensitive;
    private long cooldownMs;
    private List<ExecutableAction> actions = new ArrayList<>();


    public List<ExecutableAction> getActions() {
        return actions;
    }

    public void setActions(List<ExecutableAction> actions) {
        this.actions = actions;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public void setCooldownMs(long cooldownMs) {
        this.cooldownMs = cooldownMs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RuleInputType getInputType() {
        return inputType;
    }

    public void setInputType(RuleInputType inputType) {
        this.inputType = inputType;
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
}
