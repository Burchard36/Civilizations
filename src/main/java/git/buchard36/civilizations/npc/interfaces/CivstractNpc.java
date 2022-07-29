package git.buchard36.civilizations.npc.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface CivstractNpc extends CivstractNavigator{

    void spawnIn(Location location, CallbackFunction function);

    void tickEntity(CallbackFunction function);

    boolean existsInServer();

    List<Player> getOnlinePlayers();

    Integer getEntityId();

    UUID getEntityUuid();
}
