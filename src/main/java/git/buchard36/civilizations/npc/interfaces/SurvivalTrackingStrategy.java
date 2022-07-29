package git.buchard36.civilizations.npc.interfaces;

import lombok.NonNull;
import net.citizensnpcs.Citizens;
import org.bukkit.block.BlockFace;
import xyz.oli.Pathetic;
import xyz.oli.pathing.strategy.PathfinderStrategy;
import xyz.oli.pathing.world.chunk.SnapshotManager;
import xyz.oli.wrapper.PathBlock;
import xyz.oli.wrapper.PathBlockType;
import xyz.oli.wrapper.PathLocation;

public class SurvivalTrackingStrategy implements PathfinderStrategy {

    protected final static SnapshotManager manager = Pathetic.getSnapshotManager();

    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {
        /*if (previous != null && isOnGround(previous)) {
            if (!isOnGround(current)) return false;
        }*/


        boolean isNearCliff = isNearCliff(current);
        if (isNearCliff) {
            return false;
        }

        boolean isCliffLake = isCliffLakeOrCliff(current);
        if (isCliffLake) {
            return false;
        }

        return current.isPassable();
    }

    protected boolean isOnGround(PathBlock origin) {
        PathBlock block = manager.getBlock(origin.getPathLocation().clone().subtract(0, 1, 0));
        return block.getPathBlockType() == PathBlockType.SOLID;
    }

    protected static boolean isNearCliff(PathBlock origin) {
        PathBlock north = getBlockFacingDirection(BlockFace.NORTH, origin);
        if (isCliffLakeOrCliff(north)) return true;
        PathBlock south = getBlockFacingDirection(BlockFace.SOUTH, origin);
        if (isCliffLakeOrCliff(south)) return true;
        PathBlock east = getBlockFacingDirection(BlockFace.EAST, origin);
        if (isCliffLakeOrCliff(east)) return true;
        PathBlock west = getBlockFacingDirection(BlockFace.WEST, origin);
        return isCliffLakeOrCliff(west);
    }

    protected static boolean isCliffLakeOrCliff(PathBlock origin) {
        int maxAirAllowed = 4;
        int currentFall = 0;
        PathLocation clonesLocation = origin.getPathLocation().clone();
        for (int x = 0; x <= maxAirAllowed + 1; x++) {
            PathBlockType type = manager.getBlock(clonesLocation).getPathBlockType();
            switch (type) {
                case AIR -> {
                    currentFall++; // dont return allow next loop unless next check for currentFall is true
                }
                case LIQUID -> {
                    return true; // Dont allow the NPC to fall off cliffs into lakes
                }

                case SOLID, OTHER -> {
                    return false; //
                }
            }

            if (currentFall >= maxAirAllowed) return true;
            clonesLocation = clonesLocation.subtract(0, 1, 0);
        }

        return false;
    }

    protected static PathBlock getBlockFacingDirection(BlockFace face, PathBlock origin) {
        switch (face) {
            case NORTH: return manager.getBlock(origin.getPathLocation().clone().add(0, 0, 1));
            case SOUTH: return manager.getBlock(origin.getPathLocation().clone().subtract(0, 0, 1));
            case WEST: return manager.getBlock(origin.getPathLocation().clone().add(1, 0, 0));
            case EAST: return manager.getBlock(origin.getPathLocation().clone().subtract(1, 0, 0));
            default: return origin;
        }
    }
}
