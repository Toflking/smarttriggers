package toflking.smarttriggers.feature.trigger;

import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.trigger.validation.ValidationResult;

public interface RuntimeReloader {
    ValidationResult reload(ModConfig config);
}
