package git.buchard36.civilizations.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import git.buchard36.civilizations.npc.interfaces.CivstractNpc;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static git.buchard36.civilizations.Civilizations.THREAD_POOL_EXECUTOR;

public class CivNpc extends ServerPlayer implements CivstractNpc {
    protected AtomicBoolean existsToAll;
    protected UUID entityUuid;
    protected Integer entityId;
    protected final AtomicBoolean isJumping;
    protected AtomicLong jumpTicksLeft = new AtomicLong(0);
    public CivNpc() {
        super(MinecraftServer.getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), new GameProfile(UUID.randomUUID(), "Test"), null);
        this.existsToAll = new AtomicBoolean(false);
        this.entityId = ThreadLocalRandom.current().nextInt(1, 100000);
        this.entityUuid = this.uuid;
        this.isJumping = new AtomicBoolean(false);
        this.collides = true;
        //this.setSkin(
          //      "ewogICJ0aW1lc3RhbXAiIDogMTY1ODkxNzg1MjczNywKICAicHJvZmlsZUlkIiA6ICI2NTc3OGE5YWUzYTE0MTI5ODVlN2RjNTdhMzc3NTE1YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXJ0b3BoIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVjNTY5MmUwZTUzNTRiM2VmYjUwYWM3MTc0NmY0OWVlMDljZTVhNWQ0NmJhMWE3ZmI1YmE1MWYyOTNhYmNiMjgiCiAgICB9CiAgfQp9",
        //        ""
      //  );
    }

    @Override
    public void spawnIn(Location location, CallbackFunction function) {
        this.collides = true;
        this.checkInsideBlocks();

        CompletableFuture.runAsync(() -> {
            if (!this.existsInServer()) {
                ClientboundPlayerInfoPacket infoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this);
                //ClientboundMoveEntityPacket posPacket = new ClientboundMoveEntityPacket.Pos(this.getId(), (short) 4096, (short) 0, (short) 32768, true);
                ClientboundRotateHeadPacket headRotation = new ClientboundRotateHeadPacket(this, (byte) ((this.getYHeadRot() * 256f) / 360f)); //
                ClientboundPlayerInfoPacket playerInfoRemove = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER);

                this.getOnlinePlayers().forEach((player) -> {
                    ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                    nmsPlayer.connection.send(infoPacket);
                    this.setPos(location.getX(), location.getY(), location.getZ());
                    ClientboundAddPlayerPacket playerSpawn = new ClientboundAddPlayerPacket(this);
                    Bukkit.getScheduler().runTask(Civilizations.INSTANCE, () -> {
                        Bukkit.broadcastMessage("NPC Spawning at: X: " + playerSpawn.getX() + "Y: " + playerSpawn.getY() + "Z: " + playerSpawn.getZ());
                        Bukkit.broadcastMessage("Should spawn at: X: " + player.getLocation().getX() + "Y: " + player.getLocation().getY() + "Z: " + player.getLocation().getZ());
                        nmsPlayer.connection.send(playerSpawn);
                        nmsPlayer.connection.send(headRotation);
                        nmsPlayer.connection.send(playerInfoRemove);
                        Bukkit.getScheduler().runTask(Civilizations.INSTANCE, function::onComplete);
                        //nmsPlayer.connection.send(posPacket);


                        /*Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
                            moveEntityTo(nmsPlayer.getX(), nmsPlayer.getY(), nmsPlayer.getZ());
                        }, 0L, 20L * 10L);*/
                    });
                });
            }
        }, THREAD_POOL_EXECUTOR);
    }

    @Override
    public void tickEntity(CallbackFunction function) {

    }

    @Override
    public boolean existsInServer() {
        return existsToAll.get();
    }

    @Override
    public List<org.bukkit.entity.Player> getOnlinePlayers() {
        return List.copyOf(Bukkit.getOnlinePlayers());
    }

    @Override
    public Integer getEntityId() {
        return this.entityId;
    }

    public Vec3 collideWith(Vec3 vec3d) {
        AABB axisalignedbb = this.getBoundingBox();
        List<VoxelShape> list = this.level.getEntityCollisions(this, axisalignedbb.expandTowards(vec3d));
        Vec3 vec3d1 = vec3d.lengthSqr() == 0.0 ? vec3d : collideBoundingBox(this, vec3d, axisalignedbb, this.level, list);
        boolean flag = vec3d.x != vec3d1.x;
        boolean flag1 = vec3d.y != vec3d1.y;
        boolean flag2 = vec3d.z != vec3d1.z;
        boolean flag3 = this.onGround || flag1 && vec3d.y < 0.0;
        if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec3d2 = collideBoundingBox(this, new Vec3(vec3d.x, (double)this.maxUpStep, vec3d.z), axisalignedbb, this.level, list);
            Vec3 vec3d3 = collideBoundingBox(this, new Vec3(0.0, (double)this.maxUpStep, 0.0), axisalignedbb.expandTowards(vec3d.x, 0.0, vec3d.z), this.level, list);
            if (vec3d3.y < (double)this.maxUpStep) {
                Vec3 vec3d4 = collideBoundingBox(this, new Vec3(vec3d.x, 0.0, vec3d.z), axisalignedbb.move(vec3d3), this.level, list).add(vec3d3);
                if (vec3d4.horizontalDistanceSqr() > vec3d2.horizontalDistanceSqr()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.horizontalDistanceSqr() > vec3d1.horizontalDistanceSqr()) {
                return vec3d2.add(collideBoundingBox(this, new Vec3(0.0, -vec3d2.y + vec3d.y, 0.0), axisalignedbb.move(vec3d2), this.level, list));
            }
        }

        return vec3d1;
    }

    @Override
    public UUID getEntityUuid() {
        return this.entityUuid;
    }

    public void setSkin(String... skin) {
        String texture = skin[0];
        String signature = skin[1];

        this.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        // Enable the second layer of the ServerPlayer's skin
        this.getEntityData().set(Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0xFF);
    }

    @Override
    public void stopNavigation() {

    }

    @Override
    public boolean isCurrentlyNavigating() {
        return false;
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public long tickRate() {
        return 4L;
    }

    @Override
    public boolean tickNavigator() {
        return false;
    }

    @Override
    public void moveEntityTo(Vector vector, CallbackFunction onCompletion) {
        moveEntityTo(vector.getX(), vector.getY(), vector.getZ(), onCompletion);
    }

    @Override
    public void moveEntityTo(double x, double y, double z, CallbackFunction completed) {
        double blocksPerTick = .30;
        Vector vector = new Vector(x, y, z);
        Vector vector2 = new Vector(this.getX(), this.getY(), this.getZ());
        Vector difference = vector.clone().subtract(vector2);
        double distance =  vector2.distance(vector);
        if (distance > 8) throw new IllegalArgumentException("Overall distance may not be over 8!");
        /* This is the real packet math here */

        ClientboundMoveEntityPacket posPacket = new ClientboundMoveEntityPacket.Pos(
                this.getId(),
                (short)(blocksPerTick * (difference.getX() / distance) * 4096),
                (short)(blocksPerTick * (difference.getY() / distance) * 4096),
                (short)(blocksPerTick * (difference.getZ() / distance) * 4096),
                true);
        Bukkit.broadcastMessage("Distance between target and location: " + distance);
        int iterations = (int) Math.round(distance / blocksPerTick);
        Bukkit.broadcastMessage("Times to fire " + iterations);
        this.getOnlinePlayers().forEach((player) -> {
            ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            for (int xx = 0; xx <= iterations; xx++ ) {
                if (xx >= iterations) {
                    Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                        nmsPlayer.connection.send(posPacket);
                        Bukkit.broadcastMessage("Final fired!");
                        this.setPos(x, y, z);
                        ClientboundTeleportEntityPacket packett = new ClientboundTeleportEntityPacket(this);
                        ClientboundPlayerPositionPacket packet = new ClientboundPlayerPositionPacket(
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                this.getXRot(),
                                this.getYRot(),
                                new HashSet<>(),
                                this.entityId,
                                false
                        );
                        nmsPlayer.connection.send(packett);
                        completed.onComplete();
                    }, xx);
                    return;
                }
                final int currentX = xx;
                Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                    nmsPlayer.connection.send(posPacket);

                    Bukkit.broadcastMessage("Iteration: " + currentX);
                }, xx);
            }
        });
    }

    @Override
    public void jump() {
    }

    @Override
    public ClientboundPlayerInfoPacket craftInfoAddPacket() {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this);
    }
    @Override
    public ClientboundPlayerInfoPacket craftInfoRemovePacket() {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this);
    }


}
