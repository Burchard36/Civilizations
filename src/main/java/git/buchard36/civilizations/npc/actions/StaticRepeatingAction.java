package git.buchard36.civilizations.npc.actions;

import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.NpcController;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * This class is a static repeating action, meaning that this task will run ever x ticks, and
 * attemp to trigger a task overridden in this class
 */
public abstract class StaticRepeatingAction {

    protected BukkitTask repeatingTask;
    protected final long delayBetweenTasks;

    public StaticRepeatingAction(long delayBetweenTasks) {
        this.delayBetweenTasks = delayBetweenTasks;
    }

    public void startTask(NpcController controller) {
        this.repeatingTask = Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            if (!this.shouldTaskFire()) return;
            this.repeatingTask.cancel();
            this.task(controller, () -> {
                this.startTask(controller);
            });
        }, 0, delayBetweenTasks);
    }

    /**
     * This calculates wether or not that task should run for this tick (Runs every delayBetweenTasks variable)
     * @return
     */
    public abstract boolean shouldTaskFire();

    public abstract void task(NpcController controller, CallbackFunction function);

}
