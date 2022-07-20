package git.buchard36.civilizations.npc.interfaces;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface OnPathfindComplete {
    void onComplete() ;
}
