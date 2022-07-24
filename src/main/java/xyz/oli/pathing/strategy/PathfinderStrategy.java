package xyz.oli.pathing.strategy;

import lombok.NonNull;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.wrapper.PathBlock;

/**
 * A functional interface to modify the internal behaviour and choosing of the {@link Pathfinder}
 */
@FunctionalInterface
public interface PathfinderStrategy {
    
    /**
     * Implement the logic to see if a given location is valid for a strategy
     *
     * @param current The current {@link PathBlock} to check
     * @param previous The previous {@link PathBlock} to check or null if not available
     * @param previouser The {@link PathBlock} before the previous to check or null if not available
     */
    boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser);

}
