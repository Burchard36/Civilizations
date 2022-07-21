package git.buchard36.civilizations.npc.interfaces;

import java.util.HashSet;

@FunctionalInterface
public interface CallbackDoubleString {
    void onComplete(String st1, String st2);
}
