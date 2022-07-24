package xyz.oli.pathing.result;

public enum PathfinderSuccess {
    
    /**
     * The Path was successfully found for a given strategy
     */
    FOUND,
    /**
     * The Path wasn't found, either it reached its max search depth or it couldn't find more locations
     */
    FAILED,

}
