package net.citizensnpcs.nms.v1_19_R1.util;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.npc.ai.NPCHolder;
import org.bukkit.entity.Entity;

public interface ForwardingNPCHolder extends NPCHolder, Entity {
    @Override
    default NPC getNPC() {
        net.minecraft.world.entity.Entity handle = NMSImpl.getHandle(this);
        if (!(handle instanceof NPCHolder)) {
            if (Messaging.isDebugging()) {
                Messaging.debug("ForwardingNPCHolder with an improper bukkit entity", this, handle);
            }
            return null;
        }
        return ((NPCHolder) handle).getNPC();
    }
}
