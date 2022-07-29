package git.buchard36.civilizations.npc.interfaces;

import org.bukkit.util.Vector;
import xyz.oli.wrapper.PathLocation;

import java.util.Iterator;

@FunctionalInterface
public interface CallbackFunctionResult {
    void onComplete(Iterator<PathLocation> vectors);
}
