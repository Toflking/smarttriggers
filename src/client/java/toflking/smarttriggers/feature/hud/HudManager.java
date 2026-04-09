package toflking.smarttriggers.feature.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix3x2fStack;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;
import toflking.smarttriggers.feature.hud.elements.CounterElement;
import toflking.smarttriggers.feature.hud.elements.FlagElement;
import toflking.smarttriggers.feature.hud.elements.TimerElement;
import toflking.smarttriggers.feature.trigger.state.TriggerStateStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.*;

public class HudManager {
    private static HudManager instance;
    private final MinecraftClient mc;
    private final LinkedHashMap<String, HudElement> elements = new LinkedHashMap<>();
    private final ModConfig config;
    private HudEditController hudEditController;
    private static boolean initialized;

    private final TriggerStateStore stateStore;

    public static HudManager init(ModConfig cfg, TriggerStateStore stateStore) {
        if (instance != null) return instance;
        instance = new HudManager(MinecraftClient.getInstance(), cfg, stateStore);
        if (!initialized) {
            instance.registerDefaults();
            for (HudElement e : instance.elements.values()) {
                instance.ensureElementConfigExists(e);
            }

            HudRenderCallback.EVENT.register(instance::renderAll);

            END_CLIENT_TICK.register(client -> instance.tickAll());

            initialized = true;
            return instance;
        }
        return instance;
    }

    private HudManager(MinecraftClient mc,  ModConfig cfg, TriggerStateStore stateStore) {
        this.mc = mc;
        this.config = cfg;
        this.stateStore = stateStore;
    }

    public void setHudEditController(HudEditController ctrl) {
        this.hudEditController = ctrl;
    }

    private void registerDefaults() {
        register(new CounterElement());
        register(new FlagElement());
        register(new TimerElement());
    }

    public void register(HudElement element) {
        elements.putIfAbsent(element.id(), element);
    }

    private void ensureElementConfigExists(HudElement element) {
        config.getHud().getOrCreateHudElementConfig(element);
    }

    public Map<String, HudElement> getElements() {
        return Collections.unmodifiableMap(elements);
    }

    private void tickAll() {
        if (hudEditController == null) return;
        if (mc.world == null || mc.player == null) return;
        HudRenderContext ctx = new HudRenderContext(mc, config, hudEditController.isEditMode(), stateStore);
        for (HudElement element : elements.values()) {
            if (element.isEnabled(config)) {
                element.tick(ctx);
            }
        }
        hudEditController.onClientTick(ctx);
    }

    private void renderAll(DrawContext drawContext, RenderTickCounter counter) {
        if (mc.options.hudHidden) return;
        if (hudEditController == null) return;
        HudRenderContext ctx = new HudRenderContext(mc, drawContext, counter, config, hudEditController.isEditMode(), stateStore);
        for (HudElement element : elements.values()) {
            HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(element);
            Rect bounds = HudLayout.computeBounds(element, ecfg, ctx);
            if (ecfg.isBackground() && ecfg.isEnabled()) {
                element.renderBackground(ctx, bounds, ecfg.getBackgroundColor());
            }
        }

        for (HudElement element : elements.values()) {
            HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(element);
            Rect bounds = HudLayout.computeBounds(element, ecfg, ctx);
            if (ecfg.isEnabled()) {
                int finalX = bounds.getX();
                int finalY = bounds.getY();
                float scale = ecfg.getScale();
                Matrix3x2fStack matrices = drawContext.getMatrices();
                matrices.pushMatrix();
                matrices.translate((float) finalX, (float) finalY);
                matrices.scale(scale, scale);
                element.render(ctx);
                matrices.popMatrix();
            }
        }
        if (hudEditController.isEditMode()) {
            hudEditController.renderEditOverlay(ctx);
        }
    }

    public HudElement findElementAt(int mx, int my, HudRenderContext ctx) {
        ArrayList<HudElement> elements = new ArrayList<>(this.elements.values());
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement element = elements.get(i);
            Rect bounds = HudLayout.computeInteractionBounds(element, config.getHud().getOrCreateHudElementConfig(element), ctx);
            if (bounds.contains(mx, my)) {
                return element;
            }
        }
        return null;
    }
}
