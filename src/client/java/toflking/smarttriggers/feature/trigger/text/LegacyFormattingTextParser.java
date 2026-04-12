package toflking.smarttriggers.feature.trigger.text;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.EnumMap;
import java.util.Map;

public final class LegacyFormattingTextParser {
    private static final char SECTION_SIGN = '§';
    private static final char AMPERSAND = '&';
    private static final Map<Formatting, Boolean> EMPTY_FLAGS = new EnumMap<>(Formatting.class);

    private LegacyFormattingTextParser() {
    }

    public static Text parse(String input) {
        if (input == null || input.isEmpty()) {
            return Text.empty();
        }

        MutableText root = Text.empty();
        StringBuilder segment = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (!isFormatPrefix(current) || i + 1 >= input.length()) {
                segment.append(current);
                continue;
            }

            Formatting formatting = Formatting.byCode(Character.toLowerCase(input.charAt(i + 1)));
            if (formatting == null) {
                segment.append(current);
                continue;
            }

            appendSegment(root, segment, currentStyle);
            currentStyle = applyFormatting(currentStyle, formatting);
            i++;
        }

        appendSegment(root, segment, currentStyle);
        return root;
    }

    private static boolean isFormatPrefix(char c) {
        return c == SECTION_SIGN || c == AMPERSAND;
    }

    private static void appendSegment(MutableText root, StringBuilder segment, Style style) {
        if (segment.isEmpty()) {
            return;
        }
        root.append(Text.literal(segment.toString()).setStyle(style));
        segment.setLength(0);
    }

    private static Style applyFormatting(Style baseStyle, Formatting formatting) {
        if (formatting == Formatting.RESET) {
            return Style.EMPTY;
        }
        if (formatting.isColor()) {
            return Style.EMPTY.withColor(formatting);
        }

        return switch (formatting) {
            case BOLD -> baseStyle.withBold(true);
            case ITALIC -> baseStyle.withItalic(true);
            case UNDERLINE -> baseStyle.withUnderline(true);
            case STRIKETHROUGH -> baseStyle.withStrikethrough(true);
            case OBFUSCATED -> baseStyle.withObfuscated(true);
            default -> baseStyle;
        };
    }
}
