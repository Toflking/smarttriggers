package toflking.smarttriggers.feature.trigger.validation;

import toflking.smarttriggers.feature.trigger.enums.RuleInputType;
import toflking.smarttriggers.feature.trigger.enums.StateOperator;

public class StateOperatorSupport {
    public static boolean isOperatorSupported(StateOperator operator, RuleInputType inputType) {
        if (operator == null || inputType == null) {
            return false;
        }
        switch (inputType) {
            case FLAG -> {
                switch (operator) {
                    case GREATER_THAN, GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL, RUNNING, STOPPED -> {
                        return false;
                    }
                    default -> {
                        return true;
                    }
                }
            }
            case COUNTER -> {
                switch (operator) {
                    case RUNNING, STOPPED -> {
                        return false;
                    }
                    default -> {
                        return true;
                    }
                }
            }
            case TIMER -> {
                switch (operator) {
                    case EXISTS, MISSING -> {
                        return false;
                    }
                    default -> {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
