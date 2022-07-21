package git.buchard36.civilizations.npc;

import org.bukkit.Location;
import org.bukkit.SoundCategory;

public class NpcSoundController {

    protected final NpcController controller;

    public NpcSoundController(NpcController controller) {
        this.controller = controller;
    }

    public void makeNpcPlaySound(String soundName) {
        Location npcLocation = this.controller.bukkitPlayer.getLocation();
        npcLocation.getWorld().playSound(npcLocation, soundName, SoundCategory.VOICE, 1F, 1F);
    }

}
