package toflking.smarttriggers.feature.trigger.validation;

import java.util.List;

public record ValidationResult(boolean success, List<ValidationIssue> errors) {
    public static ValidationResult ok() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, List.of(ValidationIssue.global(message)));
    }

    public static ValidationResult errors(List<ValidationIssue> errors) {
        return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
    }
}
