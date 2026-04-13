package toflking.smarttriggers.feature.trigger.compilation;

import toflking.smarttriggers.feature.trigger.config.TriggerActionConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerRuleConfig;
import toflking.smarttriggers.feature.trigger.action.ExecutableAction;
import toflking.smarttriggers.feature.trigger.validation.config.TriggerConfigValidator;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;

import java.util.ArrayList;
import java.util.List;

import static toflking.smarttriggers.SmartTriggersClient.LOGGER;

public class Compiler {
    private final ActionFactory factory;

    public Compiler(ActionFactory fact) {
        this.factory = fact;
    }

    public List<CompiledTriggerRule> compile(TriggerConfig cfg) {
        List<CompiledTriggerRule> rules = new ArrayList<>();
        for (ValidationIssue issue : TriggerConfigValidator.validateConfig(cfg)) {
            LOGGER.warn("Trigger config validation: {}", issue);
        }
        for (int i = 0; i < cfg.getTriggerRules().size(); i++) {
            TriggerRuleConfig rule = cfg.getTriggerRules().get(i);
            List<ValidationIssue> issues = TriggerConfigValidator.validateRule(i, rule);
            if (!issues.isEmpty()) {
                for (ValidationIssue issue : issues) {
                    LOGGER.warn("Skipping invalid trigger rule: {}", issue);
                }
                continue;
            }
            CompiledTriggerRule compiledRule = compileRule(rule);
            if (!compiledRule.getActions().isEmpty()) {
                rules.add(compiledRule);
            } else {
                LOGGER.warn("Skipping trigger rule '{}' because no valid actions remain after compilation", rule.getId());
            }
        }
        return rules;
    }

    public CompiledTriggerRule compileRule(TriggerRuleConfig rule) {
        CompiledTriggerRule compiledRule = new CompiledTriggerRule();
        compiledRule.setId(rule.getId());
        compiledRule.setEnabled(rule.isEnabled());
        compiledRule.setInputType(rule.getInputType());
        compiledRule.setSource(rule.getSource());
        compiledRule.setMatchType(rule.getMatchType());
        compiledRule.setStateOperator(rule.getStateOperator());
        compiledRule.setKey(rule.getKey());
        compiledRule.setPattern(rule.getPattern());
        compiledRule.setCaseSensitive(rule.isCaseSensitive());
        compiledRule.setCooldownMs(rule.getCooldownMs());
        compiledRule.setActions(compileActions(rule));
        return compiledRule;
    }

    public List<ExecutableAction> compileActions(TriggerRuleConfig rule) {
        List<ExecutableAction> actions = new ArrayList<>();
        for (int i = 0; i < rule.getActions().size(); i++) {
            TriggerActionConfig action = rule.getActions().get(i);
            List<ValidationIssue> issues = TriggerConfigValidator.validateAction(0, i, action);
            if (!issues.isEmpty()) {
                for (ValidationIssue issue : issues) {
                    LOGGER.warn("Skipping invalid trigger action: {}", issue);
                }
                continue;
            }
            try {
                actions.add(factory.create(action));
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Skipping invalid trigger action in rule '{}': {}", rule.getId(), ex.getMessage());
            }
        }
        return actions;
    }
}
