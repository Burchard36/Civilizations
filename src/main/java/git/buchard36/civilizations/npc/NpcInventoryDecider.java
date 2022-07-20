package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.Civilizations;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NpcInventoryDecider {
    protected final NPC npc;
    protected final Player player;
    public NpcInventoryDecider(NPC npc) {
        this.npc = npc;

        this.player = (Player) npc.getEntity();
    }

    public boolean needsWood() {
        final PlayerInventory inv = this.player.getInventory();
        for (ItemStack stack : inv.getContents()) {
            if (stack == null) continue;
            if (stack.getType().name().endsWith("WOOD") || stack.getType().name().endsWith("PLANKS")) {
                return false;
            }
        }
        return true;
    }

}
