package xyz.oli.model.strategy;

import lombok.NonNull;
import xyz.oli.pathing.strategy.PathfinderStrategy;
import xyz.oli.wrapper.PathBlock;

/**
 * A {@link PathfinderStrategy} to find the direct path to a given endpoint
 */
public class DirectPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {
        return current.isPassable();
    }

}
