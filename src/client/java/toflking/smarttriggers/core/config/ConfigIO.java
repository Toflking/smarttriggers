package toflking.smarttriggers.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static toflking.smarttriggers.SmartTriggersClient.LOGGER;
import static toflking.smarttriggers.core.config.ModConfig.*;

public class ConfigIO {
    static final int CURRENT_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean changed;

    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("smarttriggers.json");
    }

    public static ModConfig loadOrCreateDefaultConfig() {
        ModConfig cfg;
        try {
            Path p = getConfigPath();
            if (!Files.exists(p)) {
                LOGGER.warn("Config file not found");
                cfg = defaultConfig();
                save(cfg);
                return cfg;
            }
            String json = Files.readString(p);
            if (json.isBlank()) {
                LOGGER.warn("Config file empty");
                cfg = defaultConfig();
                save(cfg);
                return cfg;
            }
            cfg = GSON.fromJson(json, ModConfig.class);
            if (cfg == null) {
                LOGGER.warn("Config file contains invalid JSON");
                ModConfig cfg2 = defaultConfig();
                save(cfg2);
                return cfg2;
            }
            cfg = ensureDefaults(cfg);
            cfg = migrateIfNeeded(cfg);
            if (changed) {
                save(cfg);
            }
            LOGGER.info("Config Successfully Loaded");
            return cfg;
        } catch (Exception exception) {
            LOGGER.error("Failed to load or Create new config file", exception);
            cfg = defaultConfig();
            save(cfg);
            return cfg;
        }
    }

    public static void save(ModConfig cfg) {
        try {
            String json = GSON.toJson(cfg);
            Files.writeString(getConfigPath(), json);
        } catch (Exception exception) {
            LOGGER.error("Failed to save config file",  exception);
        }
    }

    public static void setChanged(boolean changed) {
        ConfigIO.changed = changed;
    }
}