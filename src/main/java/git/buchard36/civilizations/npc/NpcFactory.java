package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.actions.TntTrollAction;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
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

        //TODO this gonna cause mem leaks on reloads.
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LEEROY JENKINS");
        npc.spawn(player.getLocation().add(5, 1, 5));
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        final NpcController controller = new NpcController(npc, player);
        controller.getTextureAndSig("https://www.minecraftskins.com/uploads/skins/2020/02/20/leeroy-jenkins-13881382.png?v510",
                skinTrait::setTexture);
        controller.lockToOwner();
        controller.registerRepeatingAction(new TntTrollAction());
        /*Bukkit.getScheduler().runTaskTimer(this.civs, () -> {
            npc.getNavigator().setTarget(player.getLocation().add(2, 0, 2));
        }, 60L, 60L);*/
    }

    public void destroy() {
        this.npc.destroy();
    }

}
