package toflking.smarttriggers.feature.trigger.validation.editor;

import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;
import toflking.smarttriggers.feature.trigger.validation.ValidationIssue;

import java.util.ArrayList;
import java.util.List;

public final class TriggerEditorValidator {
    private TriggerEditorValidator() {
    }

    public static List<ValidationIssue> validateRules(List<RuleEditorState> rules) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (rules == null) {
            return issues;
        }

        for (int i = 0; i < rules.size(); i++) {
            RuleEditorState rule = rules.get(i);
            if (rule == null) {
                issues.add(ValidationIssue.rule(i, null, "Rule is missing"));
                continue;
            }
            validateCooldown(i, rule, issues);
        }
        return issues;
    }

    private static void validateCooldown(int ruleIndex, RuleEditorState rule, List<ValidationIssue> issues) {
        String cooldownValue = rule.getCooldownString();
        TimerFormat cooldownType = rule.getCooldownType();

        if (cooldownType == null) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown type is required"));
            return;
        }

        if (cooldownValue == null || cooldownValue.trim().isEmpty()) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown is required"));
            return;
        }

        String[] parts = cooldownValue.trim().split(":");
        if (parts.length != 2) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown must contain exactly one ':'"));
            return;
        }

        long left;
        long right;
        try {
            left = Long.parseLong(parts[0].trim());
            right = Long.parseLong(parts[1].trim());
        } catch (NumberFormatException ex) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown must contain only integers"));
            return;
        }

        if (left < 0 || right < 0) {
            issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Cooldown must be non-negative"));
            return;
        }

        switch (cooldownType) {
            case SECONDS -> {
                if (right > 999) {
                    issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "Milliseconds must be between 0 and 999"));
                }
            }
            case MINUTES, HOURS -> {
                if (right > 59) {
                    issues.add(ValidationIssue.rule(ruleIndex, ValidationField.COOLDOWN, "The right-hand cooldown value must be between 0 and 59"));
                }
            }
        }
    }
}
