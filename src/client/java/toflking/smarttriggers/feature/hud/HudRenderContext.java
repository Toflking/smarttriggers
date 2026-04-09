package toflking.smarttriggers.feature.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;

public class HudRenderContext {
    private DrawContext drawContext;
    private final MinecraftClient client;
    private final int screenWidth;
    private final int screenHeight;
    private float tickDelta;
    private final TextRenderer textRenderer;
    private final ModConfig config;
    private final boolean editMode;
    private final TriggerStateStore stateStore;

    public HudRenderContext(MinecraftClient client, DrawContext drawContext, RenderTickCounter tickDelta, ModConfig config, boolean editMode,  TriggerStateStore stateStore) {
        this.client = client;
        this.drawContext = drawContext;
        this.screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        this.screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        this.tickDelta = tickDelta.getTickProgress(true);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.config = config;
        this.editMode = editMode;
        this.stateStore = stateStore;
    }

    public HudRenderContext(MinecraftClient client, ModConfig config, boolean editMode, TriggerStateStore stateStore) {
        this.client = client;
        this.screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        this.screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.config = config;
        this.editMode = editMode;
        this.stateStore = stateStore;
    }

    public MinecraftClient getClient() {
        return client;
    }

    public ModConfig getConfig() {
        return config;
    }

    public DrawContext getDrawContext() {
        return drawContext;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public TriggerStateStore getStateStore() {
        return stateStore;
    }
}
