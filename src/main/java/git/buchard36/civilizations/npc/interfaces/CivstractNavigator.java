package git.buchard36.civilizations.npc.interfaces;

import org.bukkit.util.Vector;

public interface CivstractNavigator extends PacketFactory{

    /**
     * Should attempt to cancel any and all outgoing entity move
     * packets immediately, however may not always be possible
     * so take note when using
     */
    void stopNavigation();

    /**
     * Returns true if the controller is sending entity move packets to players
     * @return true if npc is navigating
     */
    boolean isCurrentlyNavigating();

    /*
     * If ticks are running for a jump, this returns true
     */
    boolean isJumping();

    /**
     * The update rate of which to send the
     * @return long value, this typically effects the speed of the moving entity
     */
    long tickRate();

    /**
     * Returns false is navigation should cancel
     * @return true if navigation has more steps to complete
     */
    boolean tickNavigator();

    void moveEntityTo(Vector vector, CallbackFunction onCompletion);
    void moveEntityTo(double x, double y, double z, CallbackFunction onCompletion);

    void jump();

}
