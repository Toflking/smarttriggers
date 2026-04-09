package toflking.smarttriggers.feature.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import toflking.smarttriggers.core.config.ConfigIO;
import toflking.smarttriggers.core.config.ModConfig;
import toflking.smarttriggers.feature.hud.config.HudElementConfig;

import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.sign;

public class HudEditController {
    private final MinecraftClient mc;
    private final HudManager hudManager;
    private final ModConfig config;
    private boolean editMode;

    private HudElement hovered;
    private HudElement dragging;
    private int dragOffsetWithinElementX, dragOffsetWithinElementY;
    private int scaledX, scaledY;
    private boolean lastLmbDown;
    private boolean lastRmbDown;

    private boolean dirty;
    private long lastSaveTime;
    private static final long SAVE_DEBOUNCE_MS = 250;

    private HudEditScreen hudEditScreen;

    private HudEditController(HudManager mgr, ModConfig cfg) {
        this.hudManager = mgr;
        this.config = cfg;
        this.mc = MinecraftClient.getInstance();
    }

    public static HudEditController init(HudManager hudManager, ModConfig cfg) {
        // Init Keybinding
        return new HudEditController(hudManager, cfg);
    }

    public boolean isEditMode() {
        return editMode;
    }

   public void toggleEditMode(Screen parent) {
       this.editMode = !this.editMode;
       if (this.editMode) {
           hudEditScreen = new HudEditScreen(parent, this, this::onScroll);
           mc.setScreen(hudEditScreen);
           return;
       }

       this.hovered = null;
       this.dragging = null;
       mc.mouse.lockCursor();
       mc.setScreen(parent);
       if (dirty) {
           ConfigIO.save(config);
           dirty = false;
       }
   }

   public void onClientTick(HudRenderContext ctx) {
       if (!editMode) {
           hovered = null;
           dragging = null;
           lastLmbDown = isLmbDown();
           lastRmbDown = isRmbDown();
           return;
       }
       if (mc.currentScreen != null && !(mc.currentScreen instanceof HudEditScreen)) return;
       if (mc.currentScreen == null) {
           mc.setScreen(hudEditScreen);
       }
       if (mc.mouse.isCursorLocked()) {
           mc.mouse.unlockCursor();
       }
       int mouseX = Math.round((float) mc.mouse.getX());
       int mouseY = Math.round((float) mc.mouse.getY());
       int windowWidth = mc.getWindow().getWidth();
       int windowHeight = mc.getWindow().getHeight();
       scaledX = mouseX * ctx.getScreenWidth() / windowWidth;
       scaledY = mouseY * ctx.getScreenHeight() / windowHeight;
       hovered = hudManager.findElementAt(scaledX, scaledY, ctx);

       boolean lmbDown = isLmbDown();
       if (lmbDown && !lastLmbDown) {
           onMouseDown(ctx);
       }
       if (lmbDown && lastLmbDown) {
           onMouseDrag(ctx);
       }
       if (!lmbDown && lastLmbDown) {
           onMouseUp();
       }
       lastLmbDown = lmbDown;

       boolean rmbDown = isRmbDown();
       if  (rmbDown && !lastRmbDown) {
           onRightClick(ctx);
       } else if (!rmbDown && lastRmbDown) {
           saveIfDirtyDebounced();
       }

       lastRmbDown = rmbDown;

   }

   public void onMouseDown(HudRenderContext ctx) {
       if (!editMode) return;
       HudElement hit = hudManager.findElementAt(scaledX, scaledY, ctx);
       if (hit == null || !hit.allowDrag()) return;
       dragging = hit;
       HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(hit);
       Rect b = HudLayout.computeInteractionBounds(hit, ecfg, ctx);
       dragOffsetWithinElementX = scaledX - b.getX();
       dragOffsetWithinElementY = scaledY - b.getY();
   }

   public void onMouseDrag(HudRenderContext ctx) {
       if (dragging == null) return;
       HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(dragging);
       int newFinalX = scaledX - dragOffsetWithinElementX;
       int newFinalY = scaledY - dragOffsetWithinElementY;
       int wScaled = Math.round(dragging.width(ctx) * ecfg.getScale());
       int hScaled = Math.round(dragging.height(ctx) * ecfg.getScale());
       int newConfigX = HudLayout.unapplyAnchorX(newFinalX, wScaled, ctx.getScreenWidth(), ecfg.getAnchor());
       int newConfigY = HudLayout.unapplyAnchorY(newFinalY, hScaled, ctx.getScreenHeight(), ecfg.getAnchor());
       ecfg.setX(newConfigX);
       ecfg.setY(newConfigY);
       dirty = true;
   }

   public void onMouseUp() {
       if (dragging == null) return;
       dragging = null;
       saveIfDirtyDebounced();
   }

    private void saveIfDirtyDebounced() {
        if (dirty && System.currentTimeMillis() - lastSaveTime >= SAVE_DEBOUNCE_MS) {
            ConfigIO.save(config);
            dirty = false;
            lastSaveTime = System.currentTimeMillis();
        }
    }

    public void onScroll(double delta) {
        if (!editMode) return;
       HudElement target;
       if (dragging != null) {
           target = dragging;
       } else if (hovered != null) {
           target = hovered;
       } else {
           return;
       }
       HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(target);
       float step = isShiftDown() ? 0.05f : 0.10f;
       float newScale = ecfg.getScale() + sign(delta)*step;
       newScale = clamp(newScale, 0.1f, 5f);
       ecfg.setScale(newScale);
       dirty = true;
   }

   /* Old Anchor System
   public void onRightClick(HudRenderContext ctx) {
       HudElement target;
       if (dragging != null) {
           target = dragging;
       } else if (hovered != null) {
           target = hovered;
       } else {
           return;
       }
       if (!editMode) return;
       HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(target);
       HudElementConfig.Anchor anchor = swapAnchor(ecfg);
       Rect b = HudLayout.computeInteractionBounds(target, ecfg, ctx);
       int newX = HudLayout.unapplyAnchorX(b.getX(), b.getWidth(),  ctx.getScreenWidth(), anchor);
       int newY = HudLayout.unapplyAnchorY(b.getY(), b.getHeight(),  ctx.getScreenHeight(), anchor);
       ecfg.setAnchor(anchor);
       ecfg.setX(newX);
       ecfg.setY(newY);
       dirty = true;
   }

   private HudElementConfig.Anchor swapAnchor(HudElementConfig ecfg) {
        HudElementConfig.Anchor anchor = ecfg.getAnchor();
        if (anchor == HudElementConfig.Anchor.TOP_LEFT) {
            anchor = HudElementConfig.Anchor.TOP_RIGHT;
        } else if (anchor == HudElementConfig.Anchor.TOP_RIGHT) {
            anchor = HudElementConfig.Anchor.BOTTOM_LEFT;
        } else if (anchor == HudElementConfig.Anchor.BOTTOM_LEFT) {
            anchor = HudElementConfig.Anchor.BOTTOM_RIGHT;
        }  else if (anchor == HudElementConfig.Anchor.BOTTOM_RIGHT) {
            anchor = HudElementConfig.Anchor.TOP_LEFT;
        }
        return anchor;
    }
*/

    public void onRightClick(HudRenderContext ctx) {
        HudElement target;
        if (dragging != null) {
            target = dragging;
        } else if (hovered != null) {
            target = hovered;
        } else {
            return;
        }
        if (!editMode) return;
        HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(target);
        ecfg.setEnabled(!ecfg.isEnabled());
        dirty = true;
    }

    public void renderEditOverlay(HudRenderContext ctx) {
       if (mc.currentScreen != null && !(mc.currentScreen instanceof HudEditScreen)) return;
       ctx.getDrawContext().drawText(ctx.getTextRenderer(), "Press Esc to exit, Left click to move, Right click to enable/disable", 5, 5, 0xFFFFFFFF, false);
       for (HudElement element : hudManager.getElements().values()) {
           HudElementConfig ecfg = config.getHud().getOrCreateHudElementConfig(element);
           Rect b = HudLayout.computeInteractionBounds(element, ecfg, ctx);
           int thickness;
           int color;
           String enabled = "";

           if (element == dragging) {
               color = 0xFFFFAA00;
               thickness = 3;
           } else if (element == hovered) {
               color = 0xFF00FFFF;
               thickness = 2;
           } else {
               color = 0x88FFFFFF;
               thickness = 1;
           }

           if (!ecfg.isEnabled()) {
               enabled = " (Disabled)";
           }
           ctx.getDrawContext().fill(b.getX(), b.getY(), b.getX() + b.getWidth(), b.getY() + thickness, color);
           ctx.getDrawContext().fill(b.getX(), b.getY(), b.getX() + thickness, b.getY() + b.getHeight(), color);
           ctx.getDrawContext().fill(b.getX(), b.getY() + b.getHeight() - thickness, b.getX() + b.getWidth(), b.getY() + b.getHeight(), color);
           ctx.getDrawContext().fill(b.getX() + b.getWidth() - thickness, b.getY(), b.getX() + b.getWidth(), b.getY() + b.getHeight(), color);
           ctx.getDrawContext().drawText(ctx.getTextRenderer(), element.displayName() + enabled, b.getX(), b.getY() - mc.textRenderer.fontHeight, color, false);
       }
   }

    public boolean isLmbDown() {
        long handle = mc.getWindow().getHandle();
        return GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    public boolean isRmbDown() {
       long handle = mc.getWindow().getHandle();
       return GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
    }

    public boolean isShiftDown() {
        return mc.options.sneakKey.isPressed();
    }

}
