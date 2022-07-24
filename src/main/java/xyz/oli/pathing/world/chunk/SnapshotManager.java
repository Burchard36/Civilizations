package xyz.oli.pathing.world.chunk;

import lombok.NonNull;
import xyz.oli.wrapper.PathBlock;
import xyz.oli.wrapper.PathLocation;

public interface SnapshotManager {

    /**
     * Gets the block at a location
     * @param location the location to check
     * @return {@link PathBlock} the block
     */
    @NonNull
    PathBlock getBlock(PathLocation location);
}
