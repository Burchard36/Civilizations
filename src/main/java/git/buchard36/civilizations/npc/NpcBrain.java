package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.utils.BlockScanner;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public abstract class NpcBrain  {

    protected final BlockScanner blockScanner;
    protected final NPC citizensNpc;
    public BukkitTask runningThinkingTask;
    protected final ServerPlayer nmsNpc;
    protected final Player bukkitPlayer;
    protected final Navigator npcNavigator;

    public NpcBrain(NPC npc) {
        this.citizensNpc = npc;
        this.bukkitPlayer = (Player) npc.getEntity();
        this.nmsNpc = ((CraftPlayer) this.bukkitPlayer).getHandle();
        this.npcNavigator = npc.getNavigator();
        this.blockScanner = new BlockScanner();
        this.startThinking();
    }

    public void stopThinking() {
        this.runningThinkingTask.cancel();
    }

    public void startThinking() {
        this.runningThinkingTask = Bukkit
                .getScheduler()
                .runTaskTimer(
                        Civilizations.INSTANCE,
                        this::think,
                        100L,
                        100L
                );
    }

    /**
     * Calls ever few ticks, process tasks for the NPC to do in this method.
     */
    public abstract void think();

    /*public void thinkPlease() {
        /*if (this.needsWood()) {
            this.runningThinkingTask.cancel();
            /*final List<BlockReferance> blocks = this.getReferancesTo(this.player.getLocation().getChunk(),
                    Material.ACACIA_LOG,
                    Material.OAK_LOG);*/


            /*final Player targetPlayer = Bukkit.getPlayer("Burchard36");
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
                });
            });
        } else {
            this.player.chat("I DONT NEED WOOD FUCK OUT MY FACE");
        }*/
    //}

}
