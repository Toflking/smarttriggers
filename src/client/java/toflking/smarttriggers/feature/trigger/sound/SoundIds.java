package toflking.smarttriggers.feature.trigger.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SoundIds {
    private static final List<String> SOUND_IDS = BuiltInRegistries.SOUND_EVENT.keySet().stream()
            .map(Identifier::toString)
            .sorted()
            .toList();

    public static List<String> getAllSoundIds() {
        return SOUND_IDS;
    }
}
