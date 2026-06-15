package toflking.smarttriggers.feature.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;

public class HudRenderContext {
    private GuiGraphicsExtractor guiGraphicsExtractor;
    private final Minecraft client;
    private final int screenWidth;
    private final int screenHeight;
    private float tickDelta;
    private final Font font;
    private final ModConfig config;
    private final boolean editMode;
    private final TriggerStateStore stateStore;

    public HudRenderContext(Minecraft client, GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker tickDelta, ModConfig config, boolean editMode, TriggerStateStore stateStore) {
        this.client = client;
        this.guiGraphicsExtractor = guiGraphicsExtractor;
        this.screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.tickDelta = tickDelta.getGameTimeDeltaPartialTick(true);
        this.font = Minecraft.getInstance().font;
        this.config = config;
        this.editMode = editMode;
        this.stateStore = stateStore;
    }

    public HudRenderContext(Minecraft client, ModConfig config, boolean editMode, TriggerStateStore stateStore) {
        this.client = client;
        this.screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        this.font = Minecraft.getInstance().font;
        this.config = config;
        this.editMode = editMode;
        this.stateStore = stateStore;
    }

    public Minecraft getClient() {
        return client;
    }

    public ModConfig getConfig() {
        return config;
    }

    public GuiGraphicsExtractor getGuiGraphicsExtractor() {
        return guiGraphicsExtractor;
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

    public Font getFont() {
        return font;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public TriggerStateStore getStateStore() {
        return stateStore;
    }
}
