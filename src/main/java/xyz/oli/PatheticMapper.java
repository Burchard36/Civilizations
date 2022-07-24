package xyz.oli;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.world.chunk.SnapshotManager;
import xyz.oli.model.finder.PathfinderImpl;

public final class PatheticMapper {

    /**
     * Initializes the Lib. If the lib is not initialized yet but is used anyways, this will lead to many boooooms.
     *
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     * @param javaPlugin the JavaPlugin which initializes the lib
     */
    public static void initialize(JavaPlugin javaPlugin) {
        Pathetic.initialize(javaPlugin);
    }

    public static @NonNull SnapshotManager getSnapshotManager() {
        return Pathetic.getSnapshotManager();
    }

    public static @NonNull MaterialParser getMaterialParser() {
        return Pathetic.getMaterialParser();
    }

    public static @NonNull Pathfinder newPathfinder() {
        return new PathfinderImpl();
    }

    private PatheticMapper() {
    }

}
