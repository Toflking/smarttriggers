package toflking.smarttriggers.feature.trigger.ui.layout;

public final class TriggerRulesLayout {
    public static final int ACTION_INDEX_X = 0;
    public static final int ACTION_FIELD_GAP = 8;
    public static final int ACTION_LABEL_WIDTH = 24;
    public static final int ACTION_ROW_BUTTON_WIDTH = 20;
    public static final int ACTION_ROW_BUTTON_GAP = 4;
    public static final int ACTION_TIMER_TYPE_WIDTH = 40;
    public static final int COOLDOWN_TIMER_TYPE_WIDTH = 40;
    public static final int CASE_SENSITIVE_WIDTH = 128;

    private final int screenWidth;

    public TriggerRulesLayout(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int contentLeft() {
        return Math.max(12, Math.min(20, screenWidth / 40));
    }

    public int contentRight() {
        return Math.max(contentLeft() + 320, screenWidth - contentLeft() - 28);
    }

    public int contentWidth() {
        return contentRight() - contentLeft();
    }

    public int summaryExpandX() {
        return contentRight() - 20;
    }

    public int summaryDeleteX() {
        return summaryExpandX() - 25;
    }

    public int summaryEnabledX() {
        return summaryDeleteX() - 115;
    }

    public int summaryNameWidth() {
        return Math.max(180, summaryEnabledX() - contentLeft() - 10);
    }

    public int compactButtonWidth() {
        return Math.max(90, Math.min(118, (contentWidth() - 220) / 3));
    }

    public int inputButtonWidth() {
        return Math.max(100, Math.min(128, (contentWidth() - 220) / 3 + 16));
    }

    public int sourceButtonX() {
        return contentLeft() + inputButtonWidth() + 4;
    }

    public int matchButtonX() {
        return sourceButtonX() + compactButtonWidth() + 4;
    }

    public int actionFieldX() {
        return actionTypeX() + actionTypeWidth() + ACTION_FIELD_GAP;
    }

    public int actionContentFieldWidth(boolean hasTimerTypeButton) {
        int trailingControlX = hasTimerTypeButton ? actionTimerTypeButtonX() : actionRemoveButtonX();
        return Math.max(70, trailingControlX - ACTION_FIELD_GAP - actionFieldX());
    }

    public int actionRemoveButtonX() {
        return contentRight() - ((ACTION_ROW_BUTTON_WIDTH * 2) + ACTION_ROW_BUTTON_GAP);
    }

    public int actionAddButtonX() {
        return contentRight() - ACTION_ROW_BUTTON_WIDTH;
    }

    public int actionTimerTypeButtonX() {
        return actionRemoveButtonX() - ACTION_FIELD_GAP - ACTION_TIMER_TYPE_WIDTH;
    }

    public int actionTypeX() {
        return contentLeft() + ACTION_LABEL_WIDTH;
    }

    public int actionTypeWidth() {
        int availableWidth = Math.max(140, actionRemoveButtonX() - actionTypeX() - ACTION_FIELD_GAP);
        return Math.max(90, Math.min(150, availableWidth / 2));
    }

    public int textPatternWidth() {
        return Math.max(140, contentWidth() - cooldownControlsWidth() - CASE_SENSITIVE_WIDTH - 24);
    }

    public int stateKeyWidth() {
        return Math.max(130, Math.min(180, contentWidth() / 4));
    }

    public int statePatternWidth() {
        return Math.max(100, contentWidth() - stateKeyWidth() - cooldownControlsWidth() - 24);
    }

    public int cooldownControlsWidth() {
        return cooldownFieldWidth() + ACTION_FIELD_GAP + COOLDOWN_TIMER_TYPE_WIDTH;
    }

    public int cooldownFieldWidth() {
        return 96;
    }

    public int cooldownFieldX() {
        return cooldownTypeButtonX() - ACTION_FIELD_GAP - cooldownFieldWidth();
    }

    public int cooldownTypeButtonX() {
        return contentRight() - COOLDOWN_TIMER_TYPE_WIDTH;
    }
}
