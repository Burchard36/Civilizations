package git.buchard36.civilizations.npc.actions;

import git.buchard36.civilizations.npc.NpcController;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;

import java.util.Random;

public class TntTrollAction extends StaticRepeatingAction {

    protected final Random random;
    public TntTrollAction() {
        super(40);
        this.random = new Random();
    }


    @Override
    public boolean shouldTaskFire() {
        final int chanceToFire = this.random.nextInt((20) + 1);
        Bukkit.broadcastMessage("Chance for TntTrollAction to fire: " + chanceToFire);
        final int predictedChance = this.random.nextInt(100);
        Bukkit.broadcastMessage("Chance calculated: " + predictedChance);
        return chanceToFire >= predictedChance;
    }

    @Override
    public void task(NpcController controller, CallbackFunction function) {
        Bukkit.broadcastMessage("Firing task!");
        controller.stopLockingTask();
        controller.sendChatMessage("Hey... I really wana tell you something <3");


        controller.navigateNpcToPlayer(controller.linkedPlayer, 1.5F, true, () -> {
            controller.sendChatMessage("You know, over the time I've spent with you, i believe im starting to feel something" +
                    "special between us *blushes*");

            final Location playerLocation = controller.linkedPlayer.getLocation();
            if (playerLocation.distance(controller.bukkitPlayer.getLocation()) >= 7) {
                controller.sendChatMessage("THE FUCK DID I JUST SAY STOP RUNNING");
                this.task(controller, function);
                return;
            }
            controller.placeBlockAsNpc(playerLocation, Material.COBWEB, () -> {
                controller.sendChatMessage("I like cobwebs hehe, i hope you do too ;)");

                final Location tntLocation = playerLocation.clone().add(2, 0, 2);
                controller.placeBlockAsNpc(tntLocation, Material.TNT, () -> {
                    controller.makeNpcEquipItem(Material.FLINT_AND_STEEL);

                    controller.runLater(() -> {
                        TNTPrimed primed = controller.fakeIgniteTnt(tntLocation);
                        primed.setFuseTicks(2000);
                        controller.sendChatMessage("LOLOLOL Fucking dumbass");
                        final Location pussyRunAwayLocation = tntLocation.clone().add(15, 0 ,15);
                        final int highestY = pussyRunAwayLocation.getWorld().getHighestBlockYAt(
                                (int) pussyRunAwayLocation.getX(),
                                (int) pussyRunAwayLocation.getZ()
                        );
                        pussyRunAwayLocation.setY(highestY);
                        controller.navigateNpcTo(pussyRunAwayLocation, 3, true, () -> {
                            controller.sendChatMessage("KABOOOM!");
                            primed.setFuseTicks(0);
                            controller.lockTo(controller.linkedPlayer);
                            function.onComplete(); // Start the
                        });
                    }, 5, null);
                });
            });
        }, () -> {
            controller.sendChatMessage("Quit running you little cunt.");
        });
    }
}
