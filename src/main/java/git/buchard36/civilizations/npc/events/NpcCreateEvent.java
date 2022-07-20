package git.buchard36.civilizations.npc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NpcCreateEvent extends Event {

    public static HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
