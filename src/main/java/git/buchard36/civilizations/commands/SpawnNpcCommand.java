package git.buchard36.civilizations.commands;

import com.burchard36.api.command.ApiCommand;
import com.burchard36.api.command.annotation.RegisterCommand;
import git.buchard36.civilizations.Civilizations;

@RegisterCommand(name = "spawnnpc", description = "Spawns a NPC", usageMessage = "/spawnnpc")
public class SpawnNpcCommand extends ApiCommand {

    public SpawnNpcCommand(Civilizations civs) {
        this.onPlayerSender((playerSent) -> {
            civs.getNpcFactory().createNpc(playerSent.player());
            playerSent.player().sendMessage("Spawning NPC");
        });
    }


}
