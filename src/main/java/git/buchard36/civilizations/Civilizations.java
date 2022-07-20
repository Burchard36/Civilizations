package git.buchard36.civilizations;

import com.burchard36.api.BurchAPI;
import git.buchard36.civilizations.npc.NpcFactory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Civilizations extends BurchAPI {

    public NpcFactory factory;
    public static Civilizations INSTANCE;

    @Override
    public void onPreApiEnable() {

    }

    @Override
    public void onPluginEnable() {
        INSTANCE = this;
        this.factory = new NpcFactory(this);
    }

    @Override
    public void onPluginDisable() {
        this.factory.destroy();
        Bukkit.getLogger().info("Destroying npc. . .");
    }

    public NpcFactory getNpcFactory() {
        return factory;
    }

}
