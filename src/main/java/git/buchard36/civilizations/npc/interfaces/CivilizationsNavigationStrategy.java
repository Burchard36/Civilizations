package git.buchard36.civilizations.npc.interfaces;

import net.citizensnpcs.api.ai.AbstractPathStrategy;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.util.NMS;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import xyz.oli.PatheticMapper;
import xyz.oli.bukkit.BukkitMapper;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.wrapper.PathLocation;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CivilizationsNavigationStrategy extends AbstractPathStrategy {

    protected final CitizensNPC npc;
    protected NavigatorParameters parameters;
    protected final AtomicReference<Location> currentPathfindingLocation;
    protected final ArrayList<Vector> currentPath;
    public boolean pathfinderRunning;
    protected final LivingEntity target;
    protected final Pathfinder pathfinder;
    public Vector currentTravelingLocation;

    public CivilizationsNavigationStrategy(NavigatorParameters parameters,
                                              LivingEntity target,
                                              CitizensNPC npc) {
        super(TargetType.LOCATION);
        this.parameters = parameters;
        this.target = target;
        this.currentPathfindingLocation = new AtomicReference<>(null);
        this.currentPath = new ArrayList<>();
        this.pathfinderRunning = false;
        this.npc = npc;
        this.pathfinder = PatheticMapper.newPathfinder();
        this.currentTravelingLocation = null;

    }

    @Override
    public Location getCurrentDestination() {
        return this.currentPathfindingLocation.get();
    }

    @Override
    public Iterable<Vector> getPath() {
        return this.currentPath;
    }

    @Override
    public Location getTargetAsLocation() {
        return this.target.getLocation();
    }

    @Override
    public void stop() {
        // I dont have anything to stop :c
        NMS.cancelMoveDestination(npc.getEntity());
        this.currentPath.clear();
    }

    @Override
    public boolean update() {
        if (this.pathfinderRunning) {
            if (this.currentPath.size() <= 0) {
                return true;
            }

            Vector nextLocation = this.getPath().iterator().next();
            if (NMS.getDestination(npc.getEntity()) == null) {
                NMS.setDestination(npc.getEntity(), nextLocation.getX(), nextLocation.getY(), nextLocation.getZ(), 1F);
                this.currentPath.remove(0);
            }
            return false;

        }

        PathLocation currentTargetLocation = BukkitMapper.toPathLocation(this.target.getLocation());
        PathLocation npcCurrentLocation = BukkitMapper.toPathLocation(this.npc.getEntity().getLocation());
        this.pathfinder.findPathAsync(npcCurrentLocation, currentTargetLocation).thenAccept((result) -> {
            // Clear current path to insert new ones :D
            CivilizationsNavigationStrategy.this.currentPath.clear();

            for (PathLocation loc : result.getPath().getLocations()) {
                Vector vector = BukkitMapper.toVector(loc.toVector());
                CivilizationsNavigationStrategy.this.currentPath.add(vector);

            }
            this.pathfinderRunning = true;
        });
        return false;
    }
}
