package git.buchard36.civilizations.npc.actions;

import git.buchard36.civilizations.Civilizations;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class StaticRepeatingAction {

    protected BukkitTask repeatingTask;
    protected final long delayBetweenTasks;

    public StaticRepeatingAction(long delayBetweenTasks) {
        this.delayBetweenTasks = delayBetweenTasks;
    }

    public void startTask() {
        this.repeatingTask = Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            if (!this.shouldTaskFire()) return;
            this.task();
        }, 0, delayBetweenTasks);
    }

    /**
     * This calculates wether or not that task should run for this tick (Runs every delayBetweenTasks variable)
     * @return
     */
    public abstract boolean shouldTaskFire();

    public abstract void task();

}
