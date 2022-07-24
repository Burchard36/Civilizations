package xyz.oli.model.strategy;

import lombok.NonNull;
import xyz.oli.Pathetic;
import xyz.oli.pathing.strategy.PathfinderStrategy;
import xyz.oli.pathing.world.chunk.SnapshotManager;
import xyz.oli.wrapper.PathBlock;
import xyz.oli.wrapper.PathBlockType;
import xyz.oli.wrapper.PathLocation;

public class WalkablePathfinderStrategy implements PathfinderStrategy {
    
    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {
    
        PathLocation below = current.getPathLocation().clone().subtract(0, 1, 0);
        PathLocation above = current.getPathLocation().clone().add(0, 1, 0);
        PathLocation aboveAbove = above.clone().add(0, 1, 0);
    
        SnapshotManager snapshotManager = Pathetic.getSnapshotManager();
        return current.isPassable()
                && snapshotManager.getBlock(below).getPathBlockType() == PathBlockType.SOLID
                && snapshotManager.getBlock(above).isPassable()
                && snapshotManager.getBlock(aboveAbove).isPassable();
    }
}
