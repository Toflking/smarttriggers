package toflking.smarttriggers.feature.trigger.ui.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import toflking.smarttriggers.feature.trigger.ui.layout.TriggerRulesLayout;
import toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec;
import toflking.smarttriggers.feature.trigger.ui.state.ActionEditorState;
import toflking.smarttriggers.feature.trigger.ui.state.RuleEditorState;
import toflking.smarttriggers.feature.trigger.validation.ValidationField;

import java.util.List;
import java.util.function.Consumer;

public interface TriggerRulesScreenHost {
    TextRenderer textRenderer();

    TriggerRulesLayout layout();

    void markDirty();

    void rebuildRuleWidgets();

    void removeRule(RuleEditorState rule);

    boolean hasRuleIssue(RuleEditorState rule, ValidationField field);

    boolean hasActionIssue(RuleEditorState rule, int actionIndex, ValidationField field);

    void drawErrorOutline(DrawContext ctx, ClickableWidget widget);

    void cycleStateOperator(RuleEditorState rule);

    void cycleStateOperator(RuleEditorState rule, boolean forward);

    boolean isRightClick(Click click);

    void playButtonClickSound();

    String getSourceButtonLabel(RuleEditorState rule);

    String getMatchButtonLabel(RuleEditorState rule);

    ActionEditorState createDefaultAction();

    String getTimerTypeLabel(ActionEditorState action);

    String getCooldownTypeLabel(RuleEditorState rule);

    ValidationField toValidationField(ActionFieldSpec spec);

    int rowHeight();

    void clearOverlaySuggestions();

    void showOverlaySuggestions(int x, int y, int width, List<String> suggestions);

    void renderOverlaySuggestions(DrawContext ctx);

    void setSuggestionsOpened(boolean suggestionsOpened);

    void setOverlaySelectHandler(Consumer<String> overlaySelectHandler);
}
