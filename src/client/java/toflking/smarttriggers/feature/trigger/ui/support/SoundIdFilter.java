package toflking.smarttriggers.feature.trigger.ui.support;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;

public class SoundIdFilter {
    private static final int MAX_RESULTS = 8;
    private static final List<String> SOUND_IDS = Registries.SOUND_EVENT.getIds().stream()
            .map(Identifier::toString)
            .sorted()
            .toList();

    public static List<String> getSoundIds() {
        return SOUND_IDS;
    }

    public static List<String> filterSoundIds(String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return List.of();
        }

        return SOUND_IDS.stream()
                .map(id -> new RankedSoundId(id, score(id, normalizedQuery)))
                .filter(ranked -> ranked.score() >= 0)
                .sorted(Comparator
                        .comparingInt(RankedSoundId::score)
                        .thenComparing(RankedSoundId::id))
                .limit(MAX_RESULTS)
                .map(RankedSoundId::id)
                .toList();
    }

    private static int score(String id, String query) {
        String normalizedId = normalize(id);
        String path = pathPart(normalizedId);
        if (normalizedId.equals(query)) {
            return 0;
        }
        if (normalizedId.startsWith(query)) {
            return 1;
        }
        if (path.startsWith(query)) {
            return 2;
        }
        if (hasSegmentPrefix(path, query)) {
            return 3;
        }
        if (normalizedId.contains(query)) {
            return 4;
        }
        return -1;
    }

    private static String pathPart(String id) {
        int separator = id.indexOf(':');
        return separator >= 0 ? id.substring(separator + 1) : id;
    }

    private static boolean hasSegmentPrefix(String path, String query) {
        for (String segment : path.split("[.]")) {
            if (segment.startsWith(query)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private record RankedSoundId(String id, int score) {
    }
}
