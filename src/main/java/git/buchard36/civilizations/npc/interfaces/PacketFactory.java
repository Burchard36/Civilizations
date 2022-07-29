package git.buchard36.civilizations.npc.interfaces;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;

public interface PacketFactory {

    ClientboundPlayerInfoPacket craftInfoAddPacket();
    ClientboundPlayerInfoPacket craftInfoRemovePacket();

}
