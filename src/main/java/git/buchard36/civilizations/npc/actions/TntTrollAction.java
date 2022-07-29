package git.buchard36.civilizations.npc.actions;

import git.buchard36.civilizations.npc.NpcController;
import git.buchard36.civilizations.npc.actions.interfaces.StaticRepeatingAction;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import git.buchard36.civilizations.npc.interfaces.SurvivalTrackingStrategy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;

public class TntTrollAction extends StaticRepeatingAction {

    public TntTrollAction() {
        super(40);
    }


    //TODO Disabled until pathfinding methods are refactored
    @Override
    public boolean shouldTaskFire() {
        final int chanceToFire = this.random.nextInt((30) + 1);
        final int predictedChance = this.random.nextInt(100);
        return chanceToFire >= predictedChance;
        //return false;
    }

    @Override
    public void task(NpcController controller, CallbackFunction function) {
        controller.sendChatMessage("Hey... I really wana tell you something <3");
        controller.liveTrackToTargetPlayer( 2.25F, false, () -> {
            controller.sendChatMessage("You know, over the time I've spent with you, i believe im starting to feel something" +
                    "special between us *blushes*");
            final Location playerLocation = controller.linkedPlayer.getLocation();
            controller.placeBlockAsNpc(playerLocation, Material.COBWEB, () -> {
                controller.sendChatMessage("I like cobwebs hehe, i hope you do too ;)");

                final Location tntLocation = playerLocation.clone().add(2, 0, 2);
                controller.placeBlockAsNpc(tntLocation, Material.TNT, () -> {
                    controller.makeNpcEquipItem(Material.FLINT_AND_STEEL);

                    controller.runLater(() -> {
                        TNTPrimed primed = controller.fakeIgniteTnt(tntLocation);
                        primed.setFuseTicks(2000);
                        controller.sendChatMessage("LOLOLOL Fucking dumbass");
                        Location pussyRunAwayLocation = controller.getRandomSafeLocationNear(
                                SurvivalTrackingStrategy.class,
                                -15,
                                15,
                                controller.getCurrentOwnerLocation());
                        controller.makeNpcSayLeeroyJenkins();
                        if (pussyRunAwayLocation == null) {
                            controller.sendChatMessage("FUCK FUCK FUCK I CANT FIND A PLACE TO MOOOVE");
                            primed.setFuseTicks(0);
                            controller.lockToOwner();
                            function.onComplete();
                            return;
                        }
                        controller.navigateNpcTo(pussyRunAwayLocation, 3, true, () -> {
                            controller.sendChatMessage("KABOOOM!");
                            primed.setFuseTicks(0);
                            controller.lockToOwner();
                            function.onComplete(); // Start the
                        });
                    }, 5, null);
                });
            });
        });
    }
}
