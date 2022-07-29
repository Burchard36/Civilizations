package xyz.oli.model.finder;

import git.buchard36.civilizations.Civilizations;
import lombok.NonNull;
import org.bukkit.*;
import xyz.oli.Pathetic;
import xyz.oli.PatheticMapper;
import xyz.oli.bukkit.BukkitMapper;
import xyz.oli.pathing.Pathfinder;
import xyz.oli.pathing.result.Path;
import xyz.oli.pathing.result.PathfinderResult;
import xyz.oli.pathing.result.PathfinderSuccess;
import xyz.oli.pathing.strategy.PathfinderStrategy;
import xyz.oli.model.strategy.DirectPathfinderStrategy;
import xyz.oli.pathing.world.chunk.SnapshotManager;
import xyz.oli.wrapper.PathVector;
import xyz.oli.model.PathImpl;
import xyz.oli.wrapper.PathLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class PathfinderImpl implements Pathfinder {

    private static final Class<? extends PathfinderStrategy> DEFAULT_STRATEGY_TYPE = DirectPathfinderStrategy.class;

    private static final StrategyRegistry STRATEGY_REGISTRY = new StrategyRegistry();
    private static final Set<PathLocation> EMPTY_LINKED_HASHSET = Collections.unmodifiableSet(new LinkedHashSet<>());

    public static final Executor FORK_JOIN_POOL = Executors.newWorkStealingPool();

    private static final PathVector[] OFFSETS = {
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    };

    private static @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, Class<? extends PathfinderStrategy> strategyType) {

        /*
        TODO: 27/04/2022 Re-add all the event calling, Bstats
            - 20/07/2022 Verification done. Don't implement BStats here, we can listen for our own events.
         */

        if(!start.getPathWorld().equals(target.getPathWorld()))
            return new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET));

        if(start.equals(target)) // could be too accurate
            return new PathfinderResultImpl(PathfinderSuccess.FOUND, new PathImpl(start, target, Collections.singleton(start)));

        Node startNode = new Node(start.toIntegers(), start.toIntegers(), target.toIntegers(), 0);

        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathLocation> examinedLocations = new HashSet<>();

        PathfinderStrategy strategy = STRATEGY_REGISTRY.attemptRegister(strategyType);

        int depth = 1;
        int maxDepth = (int) (100 * start.distance(target));

        while (!nodeQueue.isEmpty() && depth <= maxDepth) {

            Node currentNode = nodeQueue.poll();

            if (currentNode.hasReachedEnd())
                return new PathfinderResultImpl(PathfinderSuccess.FOUND, retracePath(currentNode));

            evaluateNewNodes(nodeQueue, examinedLocations, strategy, currentNode);
            depth++;
        }

        return new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET));
    }

    private static Path retracePath(@NonNull Node node) {

        List<PathLocation> path = new ArrayList<>();

        Node currentNode = node;
        while(currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }

        path.add(node.getStart());
        Collections.reverse(path);

        return new PathImpl(node.getStart(), node.getTarget(), path);
    }

    private static void evaluateNewNodes(PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy, Node currentNode) {

        for (Node neighbourNode : getNeighbours(currentNode)) {
            if (nodeIsValid(neighbourNode, currentNode, nodeQueue, examinedLocations, strategy)) {
                nodeQueue.add(neighbourNode);
            }
        }
    }

    private static Collection<Node> getNeighbours(Node currentNode) {

        final Set<Node> newNodes = new HashSet<>(OFFSETS.length);

        for (PathVector offset : OFFSETS) {

            Node newNode = new Node(currentNode.getLocation().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);
            newNodes.add(newNode);
        }

        return newNodes;
    }

    private static boolean nodeIsValid(Node node, Node parentNode, PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy) {

        if (examinedLocations.contains(node.getLocation())) {
            return false;
        }

        if (nodeQueue.contains(node)) {
            return false;
        }

        if (!isWithinWorldBounds(node.getLocation())) {
            return false;
        }

        SnapshotManager snapshotManager = Pathetic.getSnapshotManager();
        if (!strategy.isValid(snapshotManager.getBlock(node.getLocation()),
                snapshotManager.getBlock(parentNode.getLocation()),
                snapshotManager.getBlock((node.getParent() == null ? node : node.getParent()).getLocation()))) {
            return false;
        }

        return examinedLocations.add(node.getLocation());

    }

    private static boolean isWithinWorldBounds(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }

    @NonNull
    @Override
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPath(start, target, DEFAULT_STRATEGY_TYPE);
    }

    @NonNull
    @Override
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType) {
        return seekPath(start, target, strategyType);
    }

    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPathAsync(start, target, DEFAULT_STRATEGY_TYPE);
    }
    
    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, strategyType), FORK_JOIN_POOL);
    }
}
