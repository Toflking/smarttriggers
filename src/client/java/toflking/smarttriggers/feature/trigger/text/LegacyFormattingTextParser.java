package toflking.smarttriggers.feature.trigger.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class LegacyFormattingTextParser {
    private static final char SECTION_SIGN = '§';
    private static final char AMPERSAND = '&';

    private LegacyFormattingTextParser() {
    }

    public static MutableComponent parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        MutableComponent root = Component.empty();
        StringBuilder segment = new StringBuilder();
        Style currentStyle = Style.EMPTY;

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (!isFormatPrefix(current) || i + 1 >= input.length()) {
                segment.append(current);
                continue;
            }

            ChatFormatting formatting = ChatFormatting.getByCode(Character.toLowerCase(input.charAt(i + 1)));
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

    private static void appendSegment(MutableComponent root, StringBuilder segment, Style style) {
        if (segment.isEmpty()) {
            return;
        }
        root.append(Component.literal(segment.toString()).setStyle(style));
        segment.setLength(0);
    }

    private static Style applyFormatting(Style baseStyle, ChatFormatting formatting) {
        if (formatting == ChatFormatting.RESET) {
            return Style.EMPTY;
        }
        if (formatting.isColor()) {
            return Style.EMPTY.withColor(formatting);
        }

        return switch (formatting) {
            case BOLD -> baseStyle.withBold(true);
            case ITALIC -> baseStyle.withItalic(true);
            case UNDERLINE -> baseStyle.withUnderlined(true);
            case STRIKETHROUGH -> baseStyle.withStrikethrough(true);
            case OBFUSCATED -> baseStyle.withObfuscated(true);
            default -> baseStyle;
        };
    }
}
