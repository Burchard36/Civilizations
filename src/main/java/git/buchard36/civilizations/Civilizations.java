package git.buchard36.civilizations;

import com.burchard36.api.BurchAPI;
import git.buchard36.civilizations.npc.NpcFactory;
import org.bukkit.Bukkit;
import xyz.oli.PatheticMapper;


public final class Civilizations extends BurchAPI {

    public NpcFactory factory;
    public static Civilizations INSTANCE;

    @Override
    public void onPreApiEnable() {
    }

    @Override
    public void onPluginEnable() {
        INSTANCE = this;
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
