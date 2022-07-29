package git.buchard36.civilizations;

import com.burchard36.api.BurchAPI;
import com.hakan.core.HCore;
import git.buchard36.civilizations.npc.NpcFactory;
import org.bukkit.Bukkit;
import xyz.oli.PatheticMapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public final class Civilizations extends BurchAPI {

    public NpcFactory factory;
    public static Civilizations INSTANCE;
    public static Executor THREAD_POOL_EXECUTOR;

    @Override
    public void onPreApiEnable() {
    }

    @Override
    public void onPluginEnable() {
        INSTANCE = this;
        HCore.initialize(this);
        THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(64);
        PatheticMapper.initialize(Civilizations.INSTANCE);
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
