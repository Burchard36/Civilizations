package git.buchard36.civilizations.npc;

import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.PathStrategy;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.astar.pathfinder.MinecraftBlockExaminer;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.AStarNavigationStrategy;
import net.citizensnpcs.npc.ai.FlyingAStarNavigationStrategy;
import net.citizensnpcs.npc.ai.MCNavigationStrategy;
import net.citizensnpcs.npc.ai.MCTargetStrategy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import xyz.oli.PatheticMapper;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.wrapper.PathLocation;
import xyz.oli.wrapper.PathWorld;

import java.util.*;

public class RushFollowStrategy implements PathStrategy, EntityTarget {

    protected int updateCounter;
    protected final NPC citizensNpc;
    protected final LivingEntity linkedTarget;
    protected CancelReason cancelReason;
    protected RushFollowStrategy.Targeter navigator;
    protected final NavigatorParameters parameters;
    protected Iterable<Vector> currentGoals;

    public RushFollowStrategy(NPC citizensNpc,
                              LivingEntity linkedEntity,
                              NavigatorParameters parameters) {
        this.citizensNpc = citizensNpc;
        this.linkedTarget = linkedEntity;
        this.navigator = new Targeter();
        this.parameters = parameters;
        this.updateCounter = -1;

        this.parameters.updatePathRate(100);
    }

    @Override
    public Entity getTarget() {
        return this.linkedTarget;
    }

    @Override
    public boolean isAggressive() {
        return false;
    }

    @Override
    public void clearCancelReason() {
        this.cancelReason = null;
    }

    @Override
    public CancelReason getCancelReason() {
        return this.cancelReason;
    }

    @Override
    public Location getCurrentDestination() {
        return this.getTarget().getLocation();
    }

    @Override
    public Iterable<Vector> getPath() {
        return this.navigator.getPath();
    }

    @Override
    public Location getTargetAsLocation() {
        return this.getTarget().getLocation();
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.ENTITY;
    }

    @Override
    public void stop() {
        this.navigator.stop();
    }

    @Override
    public boolean update() {
        if (this.updateCounter == -1 || this.updateCounter++ > this.parameters.updatePathRate()) {
            this.updateCounter = 0;
        }

        this.navigator.update();
        this.citizensNpc.getNavigator().setTarget(this.getPath());
        return false;
    }

    private class Targeter implements MCNavigationStrategy.MCNavigator {

        Pathfinder pathfinder;
        List<Vector> goals;


        public Targeter() {

            this.pathfinder = PatheticMapper.newPathfinder();
            this.update();
        }

        @Override
        public CancelReason getCancelReason() {
            return null;
        }

        @Override
        public Iterable<Vector> getPath() {
            return this.goals;
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean update() {
            Location npcLocation = RushFollowStrategy.this.citizensNpc.getEntity().getLocation();
            Location targetLocation = RushFollowStrategy.this.getTargetAsLocation();
            PathWorld world = new PathWorld(
                    npcLocation.getWorld().getUID(),
                    npcLocation.getWorld().getName(),
                    30,
                    200
            );
            PathLocation npcPath = new PathLocation(
                    world,
                    npcLocation.getX(),
                    npcLocation.getY(),
                    npcLocation.getZ()
            );
            PathLocation targetPath = new PathLocation(
                    world,
                    targetLocation.getX(),
                    targetLocation.getY(),
                    targetLocation.getZ()
            );

            this.goals = new ArrayList<>();
            for (PathLocation pathLocation : this.pathfinder.findPath(npcPath, targetPath).getPath().getLocations()) {
                this.goals.add(new Vector(pathLocation.getX(), pathLocation.getY(), pathLocation.getZ()));

            }
            Bukkit.broadcastMessage("Successfully calculated: " + this.goals.size() + " goals!");
            return true;
        }

    }

    private class AStarTargeter implements MCTargetStrategy.TargetNavigator {
        private int failureTimes;
        private PathStrategy strategy;

        private AStarTargeter() {
            this.failureTimes = 0;
        }

        public Location getCurrentDestination() {
            return this.strategy == null ? null : this.strategy.getCurrentDestination();
        }

        public Iterable<Vector> getPath() {
            return this.strategy.getPath();
        }

        public void setPath() {
            this.setStrategy();
            this.strategy.update();
            CancelReason subReason = this.strategy.getCancelReason();
            if (subReason == CancelReason.STUCK) {
                if (this.failureTimes++ > 10) {
                    RushFollowStrategy.this.cancelReason = this.strategy.getCancelReason();
                }
            } else {
                this.failureTimes = 0;
                RushFollowStrategy.this.cancelReason = this.strategy.getCancelReason();
            }

        }

        private void setStrategy() {
            Location location = RushFollowStrategy.this.parameters.entityTargetLocationMapper().apply(RushFollowStrategy.this.linkedTarget);
            if (location == null) {
                throw new IllegalStateException("mapper should not return null");
            } else {
                if (!RushFollowStrategy.this.citizensNpc.isFlyable()) {
                    Block block = location.getBlock();

                    while(!MinecraftBlockExaminer.canStandOn(block.getRelative(BlockFace.DOWN))) {
                        block = block.getRelative(BlockFace.DOWN);
                        if (block.getY() <= 0) {
                            block = location.getBlock();
                            break;
                        }
                    }

                    location = block.getLocation();
                }

                this.strategy = (RushFollowStrategy.this.citizensNpc.isFlyable() ? new FlyingAStarNavigationStrategy(RushFollowStrategy.this.citizensNpc, location, RushFollowStrategy.this.parameters) : new AStarNavigationStrategy(RushFollowStrategy.this.citizensNpc, location, RushFollowStrategy.this.parameters));
            }
        }

        public void stop() {
            if (this.strategy != null) {
                this.strategy.stop();
            }

        }

        public void update() {
            this.strategy.update();
        }
    }
}
