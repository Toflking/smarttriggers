package toflking.smarttriggers.feature.trigger.validation;

public record ValidationIssue(
        int ruleIndex,
        Integer actionIndex,
        ValidationField field,
        String message
) {
    public static ValidationIssue global(String message) {
        return new ValidationIssue(-1, null, null, message);
    }

    public static ValidationIssue rule(int ruleIndex, ValidationField field, String message) {
        return new ValidationIssue(ruleIndex, null, field, message);
    }

    public static ValidationIssue action(int ruleIndex, int actionIndex, ValidationField field, String message) {
        return new ValidationIssue(ruleIndex, actionIndex, field, message);
    }
}
