package toflking.smarttriggers.feature.trigger.source;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TextNormalizer {
    private static final Pattern MC_FORMATTING = Pattern.compile("§.");

    public static String toPlainString(Text text) {
        return text.getString();
    }

    public static String normalizeText(Text text) {
        if (text == null) return "";
        String normalizedText = toPlainString(text);
        normalizedText = MC_FORMATTING.matcher(normalizedText).replaceAll("");
        normalizedText = normalizedText.replaceAll("[\\p{Cf}\\p{Cc}]", "");
        normalizedText = normalizedText.replace('\u00A0', ' ');
        normalizedText = normalizedText.replaceAll("\\s+", " ").trim();
        return normalizedText;
    }

    public static List<String> normalizeLines(List<Text> lines) {
        if (lines == null) return Collections.emptyList();
        List<String> normalizedLines = new ArrayList<>(lines.size());
        for (Text line : lines) {
            String normalized = normalizeText(line);
            if (!normalized.isBlank()) {
                normalizedLines.add(normalized);
            }
        }
        return normalizedLines;
    }
}

