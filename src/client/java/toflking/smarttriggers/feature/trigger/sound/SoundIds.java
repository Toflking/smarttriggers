package toflking.smarttriggers.feature.trigger.sound;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class SoundIds {
    private static final List<String> SOUND_IDS = Registries.SOUND_EVENT.getIds().stream()
            .map(Identifier::toString)
            .sorted()
            .toList();

    public static List<String> getAllSoundIds() {
        return SOUND_IDS;
    }
}
