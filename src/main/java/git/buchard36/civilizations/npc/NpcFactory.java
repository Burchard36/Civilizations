package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.actions.TntTrollAction;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class NpcFactory {

    public static Random random = new Random();

    protected final Civilizations civs;
    protected List<CitizensNPC> npcs;

    public NpcFactory(Civilizations civs) {
        this.civs = civs;
        this.npcs = new ArrayList<>();
    }

    public void createNpc(Player player) {

        for (int x = 0; x <= 200; x++) {
            CitizensNPC npc = (CitizensNPC) CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LEEROY JENKINS" + x);
            npc.spawn(player.getLocation().add(random.nextInt(10), 1, random.nextInt(10)));
            final NpcController controller = new NpcController(npc, player);
            controller.lockToOwner();
            npcs.add(npc);
            //controller.registerRepeatingAction(new TntTrollAction());
        }

        //TODO this gonna cause mem leaks on reloads.

        /*Bukkit.getScheduler().runTaskTimer(this.civs, () -> {
            npc.getNavigator().setTarget(player.getLocation().add(2, 0, 2));
        }, 60L, 60L);*/
    }

    public void destroy() {
        this.npcs.forEach(CitizensNPC::destroy);
    }

}
