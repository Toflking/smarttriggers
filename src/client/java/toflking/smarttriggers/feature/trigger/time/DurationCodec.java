package toflking.smarttriggers.feature.trigger.time;

import toflking.smarttriggers.feature.trigger.enums.TimerFormat;

public final class DurationCodec {
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long MILLIS_PER_MINUTE = 60_000L;
    private static final long MILLIS_PER_HOUR = 3_600_000L;

    private DurationCodec() {
    }

    public static long parseToMillis(String value, TimerFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Timer format is required");
        }
        if (value == null) {
            throw new IllegalArgumentException("Timer value is required");
        }

        String[] parts = value.trim().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Timer value must match the selected timer format");
        }

        long left = Long.parseLong(parts[0].trim());
        long right = Long.parseLong(parts[1].trim());
        if (left < 0 || right < 0) {
            throw new IllegalArgumentException("Timer value must be non-negative");
        }

        validateRightHandValue(right, format);

        return switch (format) {
            case SECONDS -> (left * MILLIS_PER_SECOND) + right;
            case MINUTES -> (left * MILLIS_PER_MINUTE) + (right * MILLIS_PER_SECOND);
            case HOURS -> (left * MILLIS_PER_HOUR) + (right * MILLIS_PER_MINUTE);
        };
    }

    public static FormattedDuration formatFromMillis(long millis) {
        long normalizedMillis = Math.max(0L, millis);

        if (normalizedMillis >= MILLIS_PER_HOUR) {
            long hours = normalizedMillis / MILLIS_PER_HOUR;
            long remainingMinutes = (normalizedMillis % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
            return new FormattedDuration(hours + ":" + remainingMinutes, TimerFormat.HOURS);
        }

        if (normalizedMillis >= MILLIS_PER_MINUTE) {
            long minutes = normalizedMillis / MILLIS_PER_MINUTE;
            long remainingSeconds = (normalizedMillis % MILLIS_PER_MINUTE) / MILLIS_PER_SECOND;
            return new FormattedDuration(minutes + ":" + remainingSeconds, TimerFormat.MINUTES);
        }

        long seconds = normalizedMillis / MILLIS_PER_SECOND;
        long remainingMilliseconds = normalizedMillis % MILLIS_PER_SECOND;
        return new FormattedDuration(seconds + ":" + remainingMilliseconds, TimerFormat.SECONDS);
    }

    public static void validateRightHandValue(long right, TimerFormat format) {
        switch (format) {
            case SECONDS -> {
                if (right > 999) {
                    throw new IllegalArgumentException("Milliseconds must be between 0 and 999");
                }
            }
            case MINUTES, HOURS -> {
                if (right > 59) {
                    throw new IllegalArgumentException("The right-hand timer value must be between 0 and 59");
                }
            }
        }
    }

    public record FormattedDuration(String value, TimerFormat format) {
    }
}
