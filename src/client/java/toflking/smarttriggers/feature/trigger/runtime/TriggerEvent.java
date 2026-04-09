package toflking.smarttriggers.feature.trigger.runtime;

import toflking.smarttriggers.feature.trigger.enums.TextSource;

import java.util.List;

public record TriggerEvent(TextSource source, String text, List<String> lines, long timestamp, boolean subtitle) {

    public boolean hasText() {
        return text != null;
    }

    public boolean hasLines() {
        return lines != null;
    }


}
