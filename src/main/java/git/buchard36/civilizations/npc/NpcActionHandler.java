package git.buchard36.civilizations.npc;

import git.buchard36.civilizations.npc.actions.interfaces.NpcAction;

import java.util.ArrayList;
import java.util.List;

public class NpcActionHandler {

    protected final List<NpcAction> actions;

    public NpcActionHandler(NpcController controller) {
        this.actions = new ArrayList<>();
    }

}
