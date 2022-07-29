package git.buchard36.civilizations.npc;

import com.hakan.core.HCore;
import com.hakan.core.npc.HNPC;
import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import git.buchard36.civilizations.npc.interfaces.CallbackFunctionResult;
import git.buchard36.civilizations.npc.interfaces.SurvivalTrackingStrategy;
import net.citizensnpcs.npc.CitizensNPC;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oli.PatheticMapper;
import xyz.oli.bukkit.BukkitMapper;
import xyz.oli.wrapper.PathLocation;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class NpcFactory {

    public static Random random = new Random();

    protected final Civilizations civs;
    protected List<CitizensNPC> npcs;

    public NpcFactory(Civilizations civs) {
        this.civs = civs;
        this.npcs = new ArrayList<>();
    }

    protected void generateAsync(Location start, Location forLocation, CallbackFunctionResult results) {
        PatheticMapper.newPathfinder().findPathAsync(
                BukkitMapper.toPathLocation(start),
                BukkitMapper.toPathLocation(forLocation),
                SurvivalTrackingStrategy.class).thenAccept((pathfinderResult -> {
                    results.onComplete(pathfinderResult.getPath().getLocations().iterator());
        }));
    }

    public void createNpc(Player player) {

        HNPC npc = HCore.npcBuilder("test")
                .showEveryone(true)
                .lines("name")
                .skin("test")
                .location(player.getLocation())
                .forceBuild();
        npc.expire(1, TimeUnit.DAYS);
        npc.setEquipment(HNPC.EquipmentType.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
        npc.setEquipment(HNPC.EquipmentType.LEGS, new ItemStack(Material.LEATHER_LEGGINGS));
        Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            Bukkit.broadcastMessage("Can everyone see me!? " + npc.canEveryoneSee());
            Bukkit.broadcastMessage("Im at"
                    + " X: "
                    + npc.getLocation().getBlockX()
                    + " Y: "
                    + npc.getLocation().getBlockY()
                    + " Z: "
                    + npc.getLocation().getBlockZ()
            );
        }, 0L, 100L);
        this.pathFindTo(player, npc); // endless recursive async function

        /*CivNpc npc = new CivNpc();
        npc.spawnIn(player.getLocation(), () -> {
            Bukkit.broadcastMessage("Starting pathfinding!");
            pathFindTo(player, npc);
        });*/
        /*this.destroy();
        for (int x = 0; x <= 1; x++) {
            CitizensNPC npc = (CitizensNPC) CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LEEROY JENKINS" + x);
            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                npc.spawn(player.getLocation().clone().add(
                        ThreadLocalRandom.current().nextInt(-20, 20),
                        0, ThreadLocalRandom.current().nextInt(-20, 20))
                );
            }, x);
            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                final NpcController controller = new NpcController(npc, player);
                controller.lockToOwner();
                npcs.add(npc);

                controller.registerRepeatingAction(new TntTrollAction());
            }, x * 2);

        }*/
    }

    public void pathFindTo(Player player, HNPC npc) {
        Bukkit.broadcastMessage("Calculating path. . .");
        this.generateAsync(
                npc.getLocation(),
                player.getLocation().clone()/*.add(
                        ThreadLocalRandom.current().nextInt(-40, 40),
                        0,
                        ThreadLocalRandom.current().nextInt(-40, 40)*/
                , (locations) -> {
                Bukkit.broadcastMessage("Calculated!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage("Moving!!!!");
                        processMove(npc, locations, () -> {
                            Bukkit.broadcastMessage("Finished! Recalculating. . .");
                            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE,
                                    () -> pathFindTo(player, npc), 100L);
                        });
                    }
                }.runTaskAsynchronously(Civilizations.INSTANCE);

        });
    }

    protected void processMove(HNPC npc, Iterator<PathLocation> locations, CallbackFunction onCompletion) {
        if (locations.hasNext()) {
            PathLocation location = locations.next();
            Location loc = BukkitMapper.toLocation(location);
            //Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                npc.walk(loc, 2.5F);
                processMove(npc, locations, onCompletion);
            //}, 6L);
        } else onCompletion.onComplete();
    }

    public void destroy() {
        this.npcs.forEach(CitizensNPC::despawn);
        this.npcs.forEach(CitizensNPC::destroy);
        this.npcs.clear();
    }

}
