package xyz.oli.model.finder;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import xyz.oli.pathing.result.Path;
import xyz.oli.pathing.result.PathfinderResult;
import xyz.oli.pathing.result.PathfinderSuccess;

@AllArgsConstructor
public class PathfinderResultImpl implements PathfinderResult {
    
    private final PathfinderSuccess pathfinderSuccess;
    private final Path path;

    @Override
    public boolean successful() {
        return pathfinderSuccess == PathfinderSuccess.FOUND;
    }

    @NonNull
    @Override
    public PathfinderSuccess getPathfinderSuccess() {
        return this.pathfinderSuccess;
    }

    @NonNull
    @Override
    public Path getPath() {
        return this.path;
    }
}
