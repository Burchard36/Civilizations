package net.citizensnpcs.nms.v1_19_R1.network;

import io.netty.util.concurrent.GenericFutureListener;
import net.citizensnpcs.nms.v1_19_R1.util.NMSImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

import java.io.IOException;

public class EmptyNetworkManager extends Connection {
    public EmptyNetworkManager(PacketFlow flag) throws IOException {
        super(flag);
        NMSImpl.initNetworkManager(this);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet packet, GenericFutureListener genericfuturelistener) {
    }
}