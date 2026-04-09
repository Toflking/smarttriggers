package toflking.smarttriggers.feature.trigger.ui.meta;

import toflking.smarttriggers.feature.trigger.enums.ActionType;

import java.util.List;

import static toflking.smarttriggers.feature.trigger.ui.meta.ActionFieldSpec.ActionFieldType.*;

public final class ActionUiMeta {

    public static List<ActionFieldSpec> getFieldSpecs(ActionType type) {
        return switch (type) {
            case CHAT, TITLE, ACTIONBAR, COMMAND ->
                    List.of(new ActionFieldSpec("text", "Text", TEXT, true));

            case SOUND ->
                    List.of(new ActionFieldSpec("soundId", "Sound", TEXT, true));

            case SET_FLAG ->
                    List.of(
                            new ActionFieldSpec("key", "Flag", TEXT, true),
                            new ActionFieldSpec("value", "Value", BOOLEAN, true)
                    );

            case TOGGLE_FLAG, REMOVE_FLAG, STOP_TIMER, RESET_TIMER, REMOVE_TIMER, RESET_COUNTER, REMOVE_COUNTER ->
                    List.of(new ActionFieldSpec("key", "Key", TEXT, true));

            case SET_COUNTER, INCREMENT_COUNTER, START_TIMER ->
                    List.of(
                            new ActionFieldSpec("key", "Key", TEXT, true),
                            new ActionFieldSpec("value", "Value", INTEGER, true)
                    );
        };
    }

}
