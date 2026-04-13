package toflking.smarttriggers.core.config;

import toflking.smarttriggers.feature.hud.config.HudConfig;
import toflking.smarttriggers.feature.trigger.config.TriggerConfig;

import static toflking.smarttriggers.SmartTriggersClient.LOGGER;

public class ModConfig {
    private int version;
    private HudConfig hud;
    private TriggerConfig trigger;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public HudConfig getHud() {
        return hud;
    }

    public void setHud(HudConfig hud) {
        this.hud = hud;
    }

    public TriggerConfig getTrigger() {
        return trigger;
    }

    public void setTrigger(TriggerConfig trigger) {
        this.trigger = trigger;
    }

    private static final int CURRENT_VERSION = 1;


    public static ModConfig defaultConfig() {
        LOGGER.info("Creating new config...");
        ModConfig cfg = new ModConfig();
        cfg.setVersion(CURRENT_VERSION);

        cfg.setHud(HudConfig.createDefaultHudConfig());

        cfg.setTrigger(TriggerConfig.createDefaultTriggerConfig());

        LOGGER.info("Successfully created new config");
        return cfg;
    }

    public static ModConfig ensureDefaults(ModConfig cfg) {
        if (cfg.getHud() == null) {
            cfg.setHud(HudConfig.createDefaultHudConfig());
            ConfigIO.setChanged(true);
            LOGGER.warn("Hud config missing, inserted defaults");
        }
        cfg.setHud(HudConfig.ensureDefaults(cfg.getHud()));

        if (cfg.getTrigger() == null) {
            cfg.setTrigger(TriggerConfig.createDefaultTriggerConfig());
            ConfigIO.setChanged(true);
            LOGGER.warn("Trigger config missing, inserted defaults");
        }
        cfg.setTrigger(TriggerConfig.ensureDefaultTriggerConfig(cfg.getTrigger()));

        return cfg;
    }

    public static ModConfig migrateIfNeeded(ModConfig cfg) {
        if (cfg.getVersion() < CURRENT_VERSION) {
            LOGGER.warn("Current Config version is different from Mod version, Attempting to migrate it...");
            cfg.setVersion(CURRENT_VERSION);

            ConfigIO.setChanged(true);
            LOGGER.info("Config Successfully migrated");
        }

        return cfg;
    }
}
