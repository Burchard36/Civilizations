package xyz.oli.event;

import lombok.NonNull;
import xyz.oli.pathing.result.PathfinderResult;

/**
 * An event called when a pathfinder finishes pathing. Therefore, the result does not matter.
 * Means that the event is called even if the pathing fails.
 */
public class PathingFinishedEvent extends PathingEvent {

    private final PathfinderResult pathfinderResult;

    public PathingFinishedEvent(@NonNull PathfinderResult result) {
        this.pathfinderResult = result;
    }

    /**
     * Gets the pathfinder result
     * @return {@link PathfinderResult} the pathfinder result
     */
    @NonNull
    public PathfinderResult getPathfinderResult() {
        return this.pathfinderResult;
    }
}
