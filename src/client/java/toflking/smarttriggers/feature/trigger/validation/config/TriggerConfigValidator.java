package toflking.smarttriggers.feature.trigger.validation.config;

import toflking.smarttriggers.feature.trigger.config.TriggerActionConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerRuleConfig;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static toflking.smarttriggers.feature.trigger.validation.StateOperatorSupport.isOperatorSupported;

public final class TriggerConfigValidator {
    private TriggerConfigValidator() {
    }

    public static List<ValidationIssue> validateConfig(TriggerConfig config) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (config == null) {
            issues.add(ValidationIssue.global("Config is missing"));
            return issues;
        }
        if (config.getTriggerRules() == null) {
            issues.add(ValidationIssue.global("Trigger config is missing"));
            return issues;
        }

        Set<String> seenIds = new HashSet<>();
        for (int i = 0; i < config.getTriggerRules().size(); i++) {
            TriggerRuleConfig rule = config.getTriggerRules().get(i);
            issues.addAll(validateRule(i, rule));
            if (rule != null && !isBlank(rule.getId())) {
                if (!seenIds.add(rule.getId().trim())) {
                    issues.add(ValidationIssue.rule(i, ValidationField.RULE_ID, "Duplicate rule id '" + rule.getId().trim() + "'"));
                }
            }
        }
        return issues;
    }

    public static List<ValidationIssue> validateRule(int ruleIndex, TriggerRuleConfig rule) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (rule == null) {
            issues.add(ValidationIssue.rule(ruleIndex, null, "Rule is missing"));
            return issues;
        }
        if (isBlank(rule.getId())) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_ID, "Rule id is missing"));
        }
        if (rule.getInputType() == null) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.INPUT_TYPE, "Input type is missing"));
        } else if (rule.getInputType() == RuleInputType.TEXT) {
            if (rule.getSource() == null) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.SOURCE, "Source is missing"));
            }
            if (rule.getMatchType() == null) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.MATCH_TYPE, "Match type is missing"));
            }
            if (isBlank(rule.getPattern())) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_PATTERN, "Pattern is missing"));
            }
        } else {
            if (rule.getStateOperator() == null) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.STATE_OPERATOR, "Operator is missing"));
            }
            if (isBlank(rule.getKey())) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_KEY, "Key is missing"));
            }
            if (rule.getStateOperator() != null && !rule.getStateOperator().isUnary() && isBlank(rule.getPattern())) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_PATTERN, "Pattern is missing"));
            }
            if (!isOperatorSupported(rule.getStateOperator(), rule.getInputType())) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.STATE_OPERATOR, "This Operator isn't supported for this Input Type"));
            }
            validateStateRulePattern(ruleIndex, rule, issues);
        }
        if (rule.getCooldownMs() < 0) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown must not be negative"));
        }
        if (rule.getActions() == null || rule.getActions().isEmpty()) {
            issues.add(ValidationIssue.rule(ruleIndex, null, "Rule has no actions"));
            return issues;
        }

        for (int i = 0; i < rule.getActions().size(); i++) {
            TriggerActionConfig action = rule.getActions().get(i);
            issues.addAll(validateAction(ruleIndex, i, action));
        }
        return issues;
    }

    public static List<ValidationIssue> validateAction(int ruleIndex, int actionIndex, TriggerActionConfig action) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (action == null) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, null, "Action is missing"));
            return issues;
        }
        ActionType type = action.getType();
        if (type == null) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_TYPE, "Action type is missing"));
            return issues;
        }

        switch (type) {
            case CHAT, TITLE, ACTIONBAR, COMMAND -> requireText(ruleIndex, actionIndex, action, issues);
            case SOUND -> requireSoundId(ruleIndex, actionIndex, action, issues);
            case SET_FLAG -> {
                requireKey(ruleIndex, actionIndex, action, issues);
                requireBooleanValue(ruleIndex, actionIndex, action, issues);
            }
            case TOGGLE_FLAG, REMOVE_FLAG, RESET_COUNTER, REMOVE_COUNTER, STOP_TIMER, RESET_TIMER, REMOVE_TIMER ->
                    requireKey(ruleIndex, actionIndex, action, issues);
            case SET_COUNTER, INCREMENT_COUNTER -> {
                requireKey(ruleIndex, actionIndex, action, issues);
                requireIntegerValue(ruleIndex, actionIndex, action, issues, false);
            }
            case START_TIMER -> {
                requireKey(ruleIndex, actionIndex, action, issues);
                requireTimerValue(ruleIndex, actionIndex, action, issues);
            }
        }

        return issues;
    }

    private static void requireText(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues) {
        if (isBlank(action.getText())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_TEXT, "Text is required"));
        }
    }

    private static void requireSoundId(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues) {
        if (isBlank(action.getSoundId())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_SOUND_ID, "Sound id is required"));
        }
    }

    private static void requireKey(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues) {
        if (isBlank(action.getKey())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_KEY, "Key is required"));
        }
    }

    private static void requireBooleanValue(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues) {
        if (isBlank(action.getValue())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Boolean value is required"));
            return;
        }
        String value = action.getValue().trim();
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Value must be 'true' or 'false'"));
        }
    }

    private static void requireIntegerValue(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues, boolean mustBeNonNegative) {
        if (isBlank(action.getValue())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Integer value is required"));
            return;
        }
        try {
            int parsed = Integer.parseInt(action.getValue().trim());
            if (mustBeNonNegative && parsed < 0) {
                issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Value must be non-negative"));
            }
        } catch (NumberFormatException ex) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Value must be an integer"));
        }
    }

    private static void requireTimerValue(int ruleIndex, int actionIndex, TriggerActionConfig action, List<ValidationIssue> issues) {
        if (action.getTimerType() == null) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_TIMER_TYPE, "Timer type is required"));
        }
        if (isBlank(action.getValue())) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Timer value is required"));
            return;
        }

        String[] parts = action.getValue().trim().split(":");
        if (parts.length != 2) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Timer value must contain exactly one ':'"));
            return;
        }

        long left;
        long right;
        try {
            left = Long.parseLong(parts[0].trim());
            right = Long.parseLong(parts[1].trim());
        } catch (NumberFormatException ex) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Timer value must contain only integers"));
            return;
        }

        if (left < 0 || right < 0) {
            issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Timer value must be non-negative"));
            return;
        }

        TimerFormat timerType = action.getTimerType();
        if (timerType == null) {
            return;
        }

        switch (timerType) {
            case SECONDS -> {
                if (right > 999) {
                    issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "Milliseconds must be between 0 and 999"));
                }
            }
            case MINUTES, HOURS -> {
                if (right > 59) {
                    issues.add(ValidationIssue.action(ruleIndex, actionIndex, ValidationField.ACTION_VALUE, "The right-hand timer value must be between 0 and 59"));
                }
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void validateStateRulePattern(int ruleIndex, TriggerRuleConfig rule, List<ValidationIssue> issues) {
        StateOperator operator = rule.getStateOperator();
        if (operator == null || operator.isUnary()) {
            return;
        }

        String pattern = rule.getPattern();
        if (isBlank(pattern)) {
            return;
        }

        if (rule.getInputType() == RuleInputType.FLAG) {
            if (!"true".equalsIgnoreCase(pattern.trim()) && !"false".equalsIgnoreCase(pattern.trim())) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_PATTERN, "Pattern must be 'true' or 'false'"));
            }
            return;
        }

        if (rule.getInputType() == RuleInputType.COUNTER || rule.getInputType() == RuleInputType.TIMER) {
            try {
                Long.parseLong(pattern.trim());
            } catch (NumberFormatException ex) {
                issues.add(ValidationIssue.rule(ruleIndex, ValidationField.RULE_PATTERN, "Pattern must be numeric"));
            }
        }
    }
}
