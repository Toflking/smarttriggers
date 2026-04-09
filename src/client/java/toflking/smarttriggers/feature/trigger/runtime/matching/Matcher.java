package toflking.smarttriggers.feature.trigger.runtime.matching;

import toflking.smarttriggers.feature.trigger.compilation.CompiledTriggerRule;
import toflking.smarttriggers.feature.trigger.enums.MatchType;
import toflking.smarttriggers.feature.trigger.runtime.TriggerEvent;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Matcher {

    public boolean matches(CompiledTriggerRule rule, TriggerEvent event) {
        if (rule.getSource() != event.source()) {
            return false;
        }

        String pattern = rule.getPattern();
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }

        if (event.hasText() && !event.text().isEmpty()) {
            return matchesText(event.text(), pattern, rule.getMatchType(), rule.isCaseSensitive());
        }

        if (event.hasLines() && !event.lines().isEmpty()) {
            for (String line : event.lines()) {
                if (line != null && matchesText(line, pattern, rule.getMatchType(), rule.isCaseSensitive())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesText(String input, String pattern, MatchType matchType, boolean caseSensitive) {
        String normalizedInput = normalize(input, caseSensitive);
        String normalizedPattern = normalize(pattern, caseSensitive);

        if (matchType == MatchType.CONTAINS) {
            return normalizedInput.contains(normalizedPattern);
        } else if (matchType == MatchType.EQUALS) {
            return normalizedInput.equals(normalizedPattern);
        } else if (matchType == MatchType.STARTS_WITH) {
            return normalizedInput.startsWith(normalizedPattern);
        } else if (matchType == MatchType.ENDS_WITH) {
            return normalizedInput.endsWith(normalizedPattern);
        } else if (matchType == MatchType.REGEX) {
            try {
                int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
                return Pattern.compile(pattern, flags).matcher(input).find();
            } catch (PatternSyntaxException ignored) {
                return false;
            }
        }
        return false;
    }

    private String normalize(String value, boolean caseSensitive) {
        if (caseSensitive) {
            return value;
        }
        return value.toLowerCase(Locale.ROOT);
    }
}
