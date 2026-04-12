package toflking.smarttriggers.feature.trigger.ui.support;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import toflking.smarttriggers.feature.trigger.enums.ActionType;
import toflking.smarttriggers.feature.trigger.enums.TimerFormat;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.List;
import java.util.Objects;

public final class TriggerRulesUiSupport {
    private TriggerRulesUiSupport() {
    }

    public static ActionEditorState createDefaultAction() {
        ActionEditorState action = new ActionEditorState();
        action.setType(ActionType.CHAT);
        action.setText("");
        return action;
    }

    public static String getTimerTypeLabel(ActionEditorState action) {
        TimerFormat timerType = action.getTimerType();
        return (timerType == null ? TimerFormat.SECONDS : timerType).getDisplay();
    }

    public static String getCooldownTypeLabel(RuleEditorState rule) {
        TimerFormat timerType = rule.getCooldownType();
        return (timerType == null ? TimerFormat.SECONDS : timerType).getDisplay();
    }

    public static ValidationField toValidationField(ActionFieldSpec spec) {
        return switch (spec.key()) {
            case "text" -> ValidationField.ACTION_TEXT;
            case "key" -> ValidationField.ACTION_KEY;
            case "value" -> ValidationField.ACTION_VALUE;
            case "soundId" -> ValidationField.ACTION_SOUND_ID;
            default -> null;
        };
    }

    public static int[] computeFieldWidths(List<ActionFieldSpec> specs, int availableWidth, int fieldGap) {
        int count = specs.size();
        int[] widths = new int[count];
        if (count == 0) {
            return widths;
        }

        int totalGapWidth = fieldGap * (count - 1);
        int usableWidth = Math.max(count * 40, availableWidth - totalGapWidth);

        int totalWeight = 0;
        for (ActionFieldSpec spec : specs) {
            totalWeight += fieldWeight(spec);
        }

        int assignedWidth = 0;
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                widths[i] = Math.max(40, usableWidth - assignedWidth);
                continue;
            }

            int width = (usableWidth * fieldWeight(specs.get(i))) / totalWeight;
            widths[i] = Math.max(40, width);
            assignedWidth += widths[i];
            totalWeight -= fieldWeight(specs.get(i));
        }

        return widths;
    }

    public static String readActionField(ActionEditorState action, String key) {
        return switch (key) {
            case "text" -> action.getText();
            case "key" -> action.getKey();
            case "value" -> action.getValue();
            case "soundId" -> action.getSoundId();
            default -> "";
        };
    }

    public static void writeActionField(ActionEditorState action, String key, String value) {
        switch (key) {
            case "text" -> action.setText(value);
            case "key" -> action.setKey(value);
            case "value" -> action.setValue(value);
            case "soundId" -> action.setSoundId(value);
            default -> {
            }
        }
    }

    public static void syncField(TextFieldWidget field, String value) {
        if (!Objects.equals(field.getText(), value) && !field.isFocused()) {
            field.setText(value);
        }
    }

    public static void drawErrorOutline(DrawContext ctx, ClickableWidget widget) {
        int color = 0xFFFF5555;
        int x = widget.getX();
        int y = widget.getY();
        int right = x + widget.getWidth();
        int bottom = y + widget.getHeight();
        ctx.fill(x, y, right, y + 1, color);
        ctx.fill(x, bottom - 1, right, bottom, color);
        ctx.fill(x, y, x + 1, bottom, color);
        ctx.fill(right - 1, y, right, bottom, color);
    }

    private static int fieldWeight(ActionFieldSpec spec) {
        return switch (spec.type()) {
            case BOOLEAN -> 1;
            case INTEGER -> 1;
            case TEXT -> "key".equals(spec.key()) ? 2 : 1;
        };
    }
}
