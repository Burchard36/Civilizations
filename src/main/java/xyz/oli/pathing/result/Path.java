package xyz.oli.pathing.result;

import lombok.NonNull;
import xyz.oli.wrapper.PathLocation;

public interface Path {

    /**
     * Interpolates the points of this Path using the de Boor's algorithm
     * @see #getLocations
     * @return a newly created Path with interpolated points formed to a cubic B-spline
     */
    Path interpolate();

    /**
     * Joins this Path with the given Path.
     * @param path which will be appended at the end.
     * @return {@link Path} the new Path
     */
    Path join(Path path);
    
    /**
     * Returns the path from the Pathfinder as a {@link Iterable} full of {@link PathLocation}
     */
    @NonNull
    Iterable<PathLocation> getLocations();
    
    /**
     * Returns the start location of the path
     * @return {@link PathLocation} The location of the start
     */
    @NonNull
    PathLocation getStart();
    
    /**
     * Returns the target location of the path
     * @return {@link PathLocation} The location of the target
     */
    @NonNull
    PathLocation getEnd();
}
