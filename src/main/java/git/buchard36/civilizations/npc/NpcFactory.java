package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.actions.TntTrollAction;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class NpcFactory {

    public static Random random = new Random();

    protected final Civilizations civs;
    protected List<CitizensNPC> npcs;

    public NpcFactory(Civilizations civs) {
        this.civs = civs;
        this.npcs = new ArrayList<>();
    }

    public void createNpc(Player player) {

        for (int x = 0; x <= 50; x++) {
            CitizensNPC npc = (CitizensNPC) CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LEEROY JENKINS" + x);
            npc.spawn(player.getLocation().clone().add(ThreadLocalRandom.current().nextInt(-20, 20), 1, ThreadLocalRandom.current().nextInt(-20, 20)));
            final NpcController controller = new NpcController(npc, player);
            controller.lockToOwner();
            npcs.add(npc);
            //controller.registerRepeatingAction(new TntTrollAction());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Collections.shuffle(npcs);
                Entity ent = npcs.get(0).getEntity();
                Bukkit.broadcastMessage(ent.getCustomName() + " >> "
                + "I'm at X: "
                + ent.getLocation().getBlockX()
                + " Y: "
                + ent.getLocation().getBlockY()
                + " Z: "
                + ent.getLocation().getBlockZ());
            }
        }.runTaskTimer(Civilizations.INSTANCE, 0, 100L);

        //TODO this gonna cause mem leaks on reloads.

        /*Bukkit.getScheduler().runTaskTimer(this.civs, () -> {
            npc.getNavigator().setTarget(player.getLocation().add(2, 0, 2));
        }, 60L, 60L);*/
    }

    public void destroy() {
        this.npcs.forEach(CitizensNPC::destroy);
    }

}
