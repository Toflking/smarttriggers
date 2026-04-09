package toflking.smarttriggers.feature.hud.config;

import toflking.smarttriggers.core.config.ConfigIO;
import toflking.smarttriggers.feature.hud.HudElement;

import java.util.HashMap;
import java.util.Map;

import static toflking.smarttriggers.feature.hud.config.HudElementConfig.*;

public class HudConfig {
    private Map<String, HudElementConfig> elements;

    public Map<String, HudElementConfig> getElements() {
        return elements;
    }

    public void setElements(Map<String, HudElementConfig> elements) {
        this.elements = elements;
    }

    public static HudConfig createDefaultHudConfig() {
        HudConfig cfg = new HudConfig();
        cfg.setElements(new HashMap<>());

        cfg.getElements().put("counter", createDefaultCounter());
        cfg.getElements().put("flag", createDefaultFlag());
        cfg.getElements().put("timer", createDefaultTimer());

        return cfg;
    }

    public static HudConfig ensureDefaults(HudConfig cfg) {
        if (cfg.getElements() == null) {
            cfg.setElements(new HashMap<>());
            ConfigIO.setChanged(true);
        }
        if (!cfg.getElements().containsKey("counter")) {
            cfg.getElements().put("counter", createDefaultCounter());
            ConfigIO.setChanged(true);
        }
        if (!cfg.getElements().containsKey("flag")) {
            cfg.getElements().put("flag", createDefaultFlag());
            ConfigIO.setChanged(true);
        }
        if (!cfg.getElements().containsKey("timer")) {
            cfg.getElements().put("timer", createDefaultTimer());
            ConfigIO.setChanged(true);
        }

        for (HudElementConfig ecfg : cfg.getElements().values()) {
            HudElementConfig.ensureDefaultHudElementConfig(ecfg);
        }
        return cfg;
    }

    public HudElementConfig getOrCreateHudElementConfig(HudElement element) {
        if (getElements().containsKey(element.id())) {
            return getElements().get(element.id());
        }
        HudElementConfig cfg = element.createDefaultConfig();
        getElements().put(element.id(), cfg);
        return cfg;
    }
}
