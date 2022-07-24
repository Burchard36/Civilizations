package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import git.buchard36.civilizations.npc.interfaces.Suspendable;
import net.citizensnpcs.api.hpastar.Direction;
import net.minecraft.world.InteractionHand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class NpcBlockController {

    protected final NpcController controller;

    public NpcBlockController(NpcController controller) {
        this.controller = controller;
    }

    /**

     *
     * @param start Start location to start breaking blocks at
     * @param direction direction to break blocks to
     * @param distance distance the npc should break blocks for
     * @param instaBreak should blocks insta break, or break depending on the tool in the NPC's hands?
     */
    public CompletableFuture<Suspendable> breakInLineFromTo(Location start,
                                                            BlockFace direction,
                                                            int distance,
                                                            long delay,
                                                            boolean instaBreak,
                                                            @Nullable CallbackFunction function) {
        Object lock = new Object();

        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger timesRan = new AtomicInteger(0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (timesRan.get() > distance) {
                        this.cancel();
                        return;
                    }
                    incrementLocationByDirection(start, direction);
                    controller.navigateNpcTo(start, 2F, true, () -> {
                        if (instaBreak) {
                            controller.nmsNpc.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            controller.sendChatMessage("I break block tee-hee");
                        } else throw new IllegalArgumentException("NotImplemented: instaBreak = false");
                    });
                    timesRan.incrementAndGet();
                }
            }.runTaskTimerAsynchronously(Civilizations.INSTANCE, 0L, delay);

            // Cancel the task so it doesnt stay running
            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }, (delay * distance) + 20L);

            /* Wait for the schedular to cancel task then finish the function (Should be completed by now) */
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Suspendable(function);
        });
    }

    protected void incrementLocationByDirection(Location theLoc, BlockFace direction) {
        switch (direction) {
            case NORTH: theLoc.subtract(0, 0, 1);
            case SOUTH: theLoc.add(0, 0, 1);
            case EAST: theLoc.subtract(1, 0, 0);
            case WEST: theLoc.add(1, 0, 0);
            case DOWN: theLoc.subtract(0, 1, 0);
            default: throw new IllegalArgumentException("Invalid direction for NPC to travel, place, or break block! Direction: " + direction.name());
        }
    }

}
