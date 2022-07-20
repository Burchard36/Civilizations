package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class NpcFactory {

    protected final Civilizations civs;
    protected NPC npc;

    public NpcFactory(Civilizations civs) {
        this.civs = civs;
    }

    public void createNpc(Player player) {
        if (this.npc != null) {
            this.npc.destroy();
        }
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LEEROY JENKINS");
        npc.spawn(player.getLocation().add(5, 1, 5));
        new NpcBrain(npc);
        /*Bukkit.getScheduler().runTaskTimer(this.civs, () -> {
            npc.getNavigator().setTarget(player.getLocation().add(2, 0, 2));
        }, 60L, 60L);*/
    }

    public void destroy() {
        this.npc.destroy();
    }

}
