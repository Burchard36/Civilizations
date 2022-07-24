package xyz.oli;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.oli.material.MaterialParser;
import xyz.oli.pathing.world.chunk.SnapshotManager;
import xyz.oli.model.world.chunk.SnapshotManagerImpl;
import xyz.oli.model.world.material.ModernMaterialParser;
import xyz.oli.util.BukkitVersionUtil;

import java.util.logging.Logger;

@UtilityClass
public class Pathetic {

    private static JavaPlugin instance;
    private static Logger logger;

    // We maybe dont want them here.
    private static MaterialParser materialParser;
    private static SnapshotManager snapshotManager;

    /**
     * @throws IllegalStateException If an attempt is made to initialize more than 1 time
     */
    public static void initialize(JavaPlugin javaPlugin) {

        if(instance != null)
            throw new IllegalStateException("Can't be initialized twice");

        instance = javaPlugin;
        logger = javaPlugin.getLogger();

        if (BukkitVersionUtil.isUnder(13)) materialParser = new LegacyMaterialParser();
        else materialParser = new ModernMaterialParser();

        snapshotManager = new SnapshotManagerImpl();

        logger.info("PatheticAPI successfully initialized");
    }
    
    public static JavaPlugin getPluginInstance() {
        return instance;
    }

    public static Logger getPluginLogger() {
        return logger;
    }

    @Deprecated // for removal
    public static MaterialParser getMaterialParser() {
        return materialParser;
    }

    @Deprecated // for removal
    public static SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }
}
