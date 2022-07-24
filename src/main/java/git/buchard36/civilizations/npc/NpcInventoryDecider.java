package git.buchard36.civilizations.npc;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.CitizensNPC;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class NpcInventoryDecider extends NpcBrain {
    public NpcInventoryDecider(CitizensNPC npc) {
        super(npc);
    }

    public boolean needsWood() {
        final PlayerInventory inv = this.bukkitPlayer.getInventory();
        for (ItemStack stack : inv.getContents()) {
            if (stack == null) continue;
            if (stack.getType().name().endsWith("WOOD") || stack.getType().name().endsWith("PLANKS")) {
                return false;
            }
        }
        return true;
    }

}
