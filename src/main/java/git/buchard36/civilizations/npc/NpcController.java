package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.world.InteractionHand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.concurrent.CompletableFuture;

/**
 * Controller method to handle the NPC's behavior.
 */
public class NpcController extends NpcBrain {

    public NpcController(NPC npc) {
        super(npc);
    }

    protected void placeBlockAsNpc(Location placingLocation,
                                   Material typeToPlace,
                                   CallbackFunction onCompletion) {
        placingLocation.getBlock().setType(typeToPlace);
        placingLocation.getWorld().playSound(placingLocation, Sound.BLOCK_GRASS_PLACE, 1, 1); // Make better sound processing later
        this.nmsNpc.swing(InteractionHand.MAIN_HAND);
    }

    protected void navigateNpcTo(Location location,
                                 float atBaseSpeed,
                                 boolean useSprintingAnimation,
                                 CallbackFunction onCompletion) {
        double distance = location.distance(this.player.getLocation());
        this.npcNavigator.getDefaultParameters().range((float) (distance * 2F));
        float initialBaseSpeed = this.npcNavigator.getDefaultParameters().baseSpeed();
        this.npcNavigator.getDefaultParameters().baseSpeed(atBaseSpeed);
        this.nmsNpc.setSprinting(useSprintingAnimation);
        CompletableFuture.runAsync(() -> { // begin NPC waiting on a separate thread, so we don't halt the main thread
            while (npc.getNavigator().isNavigating()) {
                try {
                    Thread.sleep(50); // block while NPC is navigating
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Bukkit.getScheduler().runTask(Civilizations.INSTANCE, () -> {
                this.nmsNpc.setSpeed(initialBaseSpeed);
                onCompletion.onComplete();
            }); // Run completions operations back on main thread
        });
    }
}
