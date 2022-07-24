package git.buchard36.civilizations.npc.interfaces;

public class Suspendable {

    protected CallbackFunction function;

    public Suspendable(CallbackFunction function) {
        this.function = function;
    }

    /**
     * Completes the stored function, then returns a new Suspendable for the next one
     * @param function
     * @return
     */
    Suspendable andThen(CallbackFunction function) {
        if (this.function == null) throw new IllegalArgumentException("Invalid constructor called to call this method");
        this.function.onComplete();
        return new Suspendable(function);
    }

    void completeNow() {
        this.function.onComplete();
    }

}
