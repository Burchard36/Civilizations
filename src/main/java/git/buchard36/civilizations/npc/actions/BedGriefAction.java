package git.buchard36.civilizations.npc.actions;

import git.buchard36.civilizations.npc.NpcController;
import git.buchard36.civilizations.npc.actions.interfaces.StaticRepeatingAction;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import org.bukkit.event.Listener;

public class BedGriefAction extends StaticRepeatingAction implements Listener {

    public BedGriefAction() {
        super(40L);

    }

    @Override
    public boolean shouldTaskFire() {
        return false;
    }

    @Override
    public void task(NpcController controller, CallbackFunction function) {

    }
}
