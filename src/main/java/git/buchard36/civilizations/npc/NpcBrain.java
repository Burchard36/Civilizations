package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.interfaces.OnHittingComplete;
import git.buchard36.civilizations.npc.interfaces.OnPathfindComplete;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static git.buchard36.civilizations.Civilizations.around;

public class NpcBrain extends NpcInventoryDecider {

    public BukkitTask runningThinkingTask;
    protected final ServerPlayer nmsNpc;
    protected final Navigator npcNavigator;

    public NpcBrain(NPC npc) {
        super(npc);

        CraftPlayer craftPlayer = (CraftPlayer) this.player; // player inherits from NpcInventoryDecider
        this.nmsNpc = craftPlayer.getHandle();
        this.npcNavigator = npc.getNavigator();

        this.runningThinkingTask = Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            think();
        }, 100L, 100L);
    }


    public void think() {
        if (this.needsWood()) {
            this.runningThinkingTask.cancel();
            /*final List<BlockReferance> blocks = this.getReferancesTo(this.player.getLocation().getChunk(),
                    Material.ACACIA_LOG,
                    Material.OAK_LOG);*/


            final Player targetPlayer = Bukkit.getPlayer("Burchard36");
            final CraftPlayer targetCraftPlayer = (CraftPlayer) targetPlayer;
            final ServerPlayer nmsTarget = targetCraftPlayer.getHandle();
            this.npc.getNavigator().setTarget(targetPlayer.getLocation().clone().add(5, 0, 5));
            this.waitForPathfind(() -> {
                this.player.chat("Heyy UwU, can you help a gamer girl out :((");
                Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                    npc.getNavigator().getDefaultParameters().range(700F);
                    this.npc.getNavigator().setTarget(targetPlayer.getLocation().clone().add(2, 0, 2));
                    this.waitForPathfind(() -> {
                        this.player.getEquipment().setItemInMainHand(new ItemStack(Material.COBWEB));
                        targetPlayer.getLocation().getBlock().setType(Material.COBWEB);
                        this.nmsNpc.swing(InteractionHand.MAIN_HAND);
                        this.player.chat("Hehe i like spiders! Do you? UwU :3 <3<3");
                        Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                            this.player.getEquipment().setItemInMainHand(new ItemStack(Material.TNT));
                            this.nmsNpc.swing(InteractionHand.MAIN_HAND);
                            Location tntLocation = targetPlayer.getLocation().clone().add(1, 0, 1);
                            tntLocation.getBlock().setType(Material.TNT);
                            targetPlayer.getWorld().playSound(targetPlayer.getLocation(), Sound.BLOCK_GRASS_PLACE, 1, 1);
                            this.player.chat("Oh it looks like you dropped something tee-hee");
                            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                                this.npc.faceLocation(tntLocation);
                                this.player.getEquipment().setItemInMainHand(new ItemStack(Material.FLINT_AND_STEEL));
                                this.nmsNpc.swing(InteractionHand.MAIN_HAND);
                                targetPlayer.getWorld().playSound(targetPlayer.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                                tntLocation.getBlock().setType(Material.AIR);
                                TNTPrimed primed = (TNTPrimed) tntLocation.getWorld().spawnEntity(tntLocation, EntityType.PRIMED_TNT);
                                primed.setFuseTicks(20000);
                                targetPlayer.getWorld().playSound(targetPlayer.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
                                this.nmsNpc.setSpeed(5);
                                long highestYat = tntLocation.getWorld().getHighestBlockYAt((int) (tntLocation.getX() + 15), (int) (tntLocation.getZ() + 15));
                                this.npc.getNavigator().setTarget(targetPlayer.getLocation().clone().add(15, highestYat, 15));
                                this.waitForPathfind(() -> {
                                    this.player.chat("Lol fucking simp get shit on");
                                    primed.setFuseTicks(5);
                                });
                            }, 35);
                        }, 35);

                    });
                }, 15);

            /*blocks.stream().filter(block -> block.blockY == player.getLocation().getBlockY()).findFirst().ifPresent(block -> {


                    /*Bukkit.broadcastMessage("Pathfinding complete!");
                    this.waitAndHit(nmsPlayer, nmsTarget, 30, () -> {
                        Location b = block.getBlock().getLocation();
                        nmsPlayer.connection.chat("The wood is at X:" + b.getX() + " Y: " + b.getY() + " Z: " + b.getZ(), false);


                        npc.getNavigator().getDefaultParameters().range(700F);
                        npc.getNavigator().setTarget(b.add(1, 0, 1));
                        npc.faceLocation(b);
                        this.waitForPathfind(() -> {
                            npc.faceLocation(b);
                            nmsPlayer.connection.chat("Here is the wood i found :)", false);
                        });
                    });
                });*/
            });
        } else {
            this.player.chat("I DONT NEED WOOD FUCK OUT MY FACE");
        }
    }

    public void waitAndHit(ServerPlayer attacker,
                                              ServerPlayer victim,
                                              long delay,
                                              OnHittingComplete onHittingComplete) {
        BlockingQueue<Void> pause = new ArrayBlockingQueue<Void>(3);
        TargetingConditions conditions = TargetingConditions.forCombat();
        for (int x = 0; x <= 3; x++) {
            try {
                pause.poll(delay, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (!conditions.test(victim, attacker)) {
                attacker.connection.chat("Your lucky i cant fucking hit you fuckface", false);
                continue;
            }
            attacker.connection.chat("Take this, asshole!", false);
            attacker.attack(victim);
            attacker.swing(InteractionHand.MAIN_HAND);
        }
        Bukkit.getScheduler().runTask(Civilizations.INSTANCE, onHittingComplete::onComplete);
    }

    protected List<BlockReferance> getReferancesTo(Chunk origin, Material... types) {
        final List<BlockReferance> result = new ArrayList<>();
        Collection<ChunkSnapshot> chunks = around(this.player.getLocation().getChunk(), 2);
        for (ChunkSnapshot snapshot : chunks) {
            Bukkit.broadcastMessage("Searching chunks");
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    for (int y = 40; y <= 200; y++) {
                        Material type = snapshot.getBlockType(x, y, z);
                        if (type == Material.AIR) continue;
                        for (Material aType : types) {
                            if (aType == type) {
                                result.add(new BlockReferance(snapshot, x, y, z, aType));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    protected CompletableFuture<Void> waitForPathfind(OnPathfindComplete creationCallback) {
        return CompletableFuture.runAsync(() -> {
            while (npc.getNavigator().isNavigating()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Bukkit.getScheduler().runTask(Civilizations.INSTANCE, creationCallback::onComplete);
        });
    }

}
