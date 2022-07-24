package git.buchard36.civilizations.npc;

import com.google.common.collect.Iterators;
import git.buchard36.civilizations.Civilizations;
import git.buchard36.civilizations.npc.actions.interfaces.StaticRepeatingAction;
import git.buchard36.civilizations.npc.interfaces.CallbackFunction;
import git.buchard36.civilizations.npc.interfaces.CallbackDoubleString;
import git.buchard36.civilizations.npc.interfaces.CivilizationsNavigationStrategy;
import git.buchard36.civilizations.utils.BlockScanner;

import net.citizensnpcs.nms.v1_19_R1.entity.EntityHumanNPC;
import net.citizensnpcs.npc.CitizensNPC;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.oli.PatheticMapper;
import xyz.oli.pathing.Pathfinder;

import javax.annotation.Nullable;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;


/**
 * Controller method to handle the NPC's behavior.
 */
public class NpcController extends NpcInventoryDecider {


    protected AtomicBoolean aStarRunning;
    protected Pathfinder pathfinder;
    public static Executor executor;
    public final Player linkedPlayer;
    protected final BlockScanner blockScanner;
    protected BukkitTask lockToTask;
    protected final List<StaticRepeatingAction> repeatingActions;
    public final NpcSoundController soundController;

    public NpcController(CitizensNPC npc, Player linkedPlayer) {
        super(npc);
        this.linkedPlayer = linkedPlayer;
        this.blockScanner = new BlockScanner();
        this.repeatingActions = new ArrayList<>();
        this.soundController = new NpcSoundController(this);
        executor = Executors.newWorkStealingPool();
        this.pathfinder = PatheticMapper.newPathfinder();
        this.aStarRunning = new AtomicBoolean(false);
    }

    @Override
    public void think() {
        if (this.needsWood()) {

        }
    }

    public void registerRepeatingAction(StaticRepeatingAction action) {
        action.startTask(this);
        this.repeatingActions.add(action);
    }

    protected void makePlayerAttack(LivingEntity entity,
                                    int times,
                                    long delayBetweenHits,
                                    @Nullable CallbackFunction callback) {
        AtomicInteger timesRan = new AtomicInteger(0);
        net.minecraft.world.entity.LivingEntity nmsEntity = ((CraftLivingEntity) entity).getHandle();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            if (timesRan.get() >= times) return; // Make sure the task doesn't run more than the times specified.
            if (!this.combatTargetConditions.test(nmsEntity, this.nmsNpc)) {
                this.sendChatMessage("Your lucky i cant fucking hit you fuckface");
                return;
            }
            this.sendChatMessage("Take this, asshole!");
            this.nmsNpc.attack(nmsEntity);
            this.nmsNpc.swing(InteractionHand.MAIN_HAND);
            this.npcNavigator.getDefaultParameters();
            timesRan.incrementAndGet();
        }, 0, delayBetweenHits);

        Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
            task.cancel();
            if (callback != null) callback.onComplete();
        }, (delayBetweenHits * times) + 5); // Add 5 ticks to account for the delay between each hit & processing time
    }



    /**
     * Sets the main hand to the typeToPlace, waits, faces torwards placing locations, waits again, and then places item with sound
     * @param placingLocation Location to place block at
     * @param typeToPlace Material type to place
     * @param onCompletion Callback function to run when the block is placed
     */
    public void placeBlockAsNpc(Location placingLocation,
                                   Material typeToPlace,
                                   @Nullable CallbackFunction onCompletion) {
        final ItemStack stack = new ItemStack(typeToPlace);
        Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
            this.citizensNpc.faceLocation(placingLocation); // 4 ticks later, face towards the block to place
            Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                this.bukkitPlayer.getEquipment().setItemInMainHand(stack); // 3 ticks later set the item in the NPC's hand

                Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
                    this.nmsNpc.swing(InteractionHand.MAIN_HAND);
                    Objects.requireNonNull(placingLocation.getWorld())
                            .playSound(placingLocation, Sound.BLOCK_GRASS_PLACE, 1, 1); // Make better sound processing later
                    placingLocation.getBlock().setType(typeToPlace);
                    //this.bukkitPlayer.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                    if (onCompletion != null) onCompletion.onComplete();
                }, 0L);
            }, 0L);
        }, 0L);
    }

    public TNTPrimed fakeIgniteTnt(Location tntLocation) {
        this.citizensNpc.faceLocation(tntLocation);
        this.makeNpcEquipItem(Material.FLINT_AND_STEEL);
        this.nmsNpc.swing(InteractionHand.MAIN_HAND);
        tntLocation.getWorld().playSound(tntLocation, Sound.ITEM_FLINTANDSTEEL_USE, 1F, 1F);
        tntLocation.getBlock().setType(Material.AIR);
        return (TNTPrimed) tntLocation.getWorld().spawnEntity(tntLocation, EntityType.PRIMED_TNT);
    }

    public void makeNpcEquipItem(Material material) {
        this.bukkitPlayer.getEquipment().setItemInMainHand(new ItemStack(material));
    }

    public void liveTrackToTargetPlayer(float baseSpeed,
                                  boolean sprint,
                                  @Nullable CallbackFunction function) {
        this.assignRange(250F);
        this.assignSpeed(baseSpeed);
        this.setSprinting(sprint);
        this.lockToOwner();
        CompletableFuture.supplyAsync(() -> {
            Bukkit.broadcastMessage("Waitingg");
            stallThread(40L); // stall for 40 ticks
            Bukkit.broadcastMessage("Finished!");
            runLater(() -> { // attempt sync checks then process sync callback
                Location newTargetLocation = this.linkedPlayer.getLocation();
                float distanceCurrent = (float) newTargetLocation.distance(this.bukkitPlayer.getLocation());

                if (distanceCurrent >= 50) {
                    runLater(() -> {
                        this.sendChatMessage("THATS IT, IVE HAD IT YOU ASS");
                        this.creepyTeleportToOwner(false);
                        this.liveTrackToTargetPlayer(baseSpeed * 4F, true, function);
                    }, 0L, null);
                } else if (distanceCurrent >= 4) {
                    runLater(() -> {
                        this.sendChatMessage("Quit running you little shit!");
                        this.liveTrackToTargetPlayer(baseSpeed + 2F, true, function);
                    }, 0L, null);
                } else {
                    if (function != null) runLater(function::onComplete, 0L, null);
                }
            }, 0L, null);

            return null;
        }, this.executor);

    }

    public void creepyTeleportToOwner(boolean followAfterwards) {
        final Location offsetOwnerLocation = this.linkedPlayer.getLocation().add(35, 0, 30);
        final int highestY = Objects.requireNonNull(offsetOwnerLocation.getWorld())
                .getHighestBlockYAt(offsetOwnerLocation.getBlockX(),
                offsetOwnerLocation.getBlockZ());
        offsetOwnerLocation.setY(highestY);
        this.citizensNpc.teleport(offsetOwnerLocation.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
        if (followAfterwards) this.lockToOwner();
    }

    public void navigateNpcTo(Location location,
                                 float atBaseSpeed,
                                 boolean useSprintingAnimation,
                                 @Nullable CallbackFunction onCompletion) {
        final Location currentLocation = this.bukkitPlayer.getLocation();
        float distance = (float) currentLocation.distance(location);
        this.assignRange(distance);
        float initialBaseSpeed = this.npcNavigator.getDefaultParameters().baseSpeed();
        this.npcNavigator.getDefaultParameters().baseSpeed(atBaseSpeed);
        this.nmsNpc.setSprinting(useSprintingAnimation);
        this.npcNavigator.setTarget(location.add(1, 0, 1));
        CompletableFuture.runAsync(() -> { // begin NPC waiting on a separate thread, so we don't halt the main thread
            this.waitForNavigator();

            Bukkit.getScheduler().runTask(Civilizations.INSTANCE, () -> {
                this.nmsNpc.setSpeed(1F);
                this.nmsNpc.setSprinting(false);
                if (onCompletion != null) onCompletion.onComplete();
            }); // Run completions operations back on main thread
        });
    }

    public void sendChatMessage(String msg) {
        this.nmsNpc.connection.chat(msg, false);
    }

    public void sendMessageTo(LivingEntity entity, String msg) {
        entity.sendMessage(msg);
    }

    public void lockToOwner() {
        this.assignSpeed(2F);
        this.setSprinting(false);
        //this.creepyTeleportToOwner(false);
        this.assignRange(100);
        this.setTargetAndFaceDirection(this.linkedPlayer);
    }

    public void lockTo(LivingEntity entity) {
        this.setTargetAndFaceDirection(entity);
    }

    /**
     * THIS WILL BLOCK THE MAIN THREAD, ONLY CALL IT ASYNC
     */
    protected void waitForNavigator() {
        while (this.npcNavigator.isNavigating()) {
            try {
                Thread.sleep(50); // block while NPC is navigating
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void stallThread(long ticks) {
        if (!Bukkit.isPrimaryThread()) throw new RuntimeException("You cannot stallThread on the Bukkit thread!");
        try {
            TimeUnit.MILLISECONDS.sleep(50 * ticks);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex); // should NEVER fail
        }
    }

    /**
     * Tells us if the NpcNavigator is currently running
     */
    public boolean isNavigatorRunning() {
        return this.npcNavigator.isNavigating();
    }

    public void makeNpcSayLeeroyJenkins() {
        this.soundController.makeNpcPlaySound("minecraft:leeroy_jenkins");
    }

    protected void setTargetAndFaceDirection(LivingEntity entity) {
        this.lockToTask = Bukkit.getScheduler().runTaskTimer(Civilizations.INSTANCE, () -> {
            if (this.isNavigatorRunning()) return;
            this.npcNavigator.setTarget(this.linkedPlayer.getLocation().add(2, 0, 2),
                    params -> new CivilizationsNavigationStrategy(params, entity, citizensNpc));
        }, 0L, 1L);
    }

    public void stopLockingTask() {
       // this.lockToTask.cancel();
        this.npcNavigator.cancelNavigation();
    }

    public void assignRange(float distanceBeforeTeleporting) {
        this.npcNavigator.getDefaultParameters().range(distanceBeforeTeleporting * 3F);
    }

    public void assignSpeed(float speed) {
        this.npcNavigator.getDefaultParameters().speedModifier(speed);
    }

    public void setSprinting(boolean sprinting) {
        this.nmsNpc.setSprinting(sprinting);
    }

    public Location getCurrentNpcLocation() {
        return this.citizensNpc.getEntity().getLocation();
    }

    public Location getCurrentOwnerLocation() {
        return this.linkedPlayer.getLocation();
    }

    public void runLater(Runnable runnable, long delay, @Nullable CallbackFunction callback) {
        Bukkit.getScheduler().runTaskLater(Civilizations.INSTANCE, () -> {
            runnable.run();
            if (callback !=null) callback.onComplete();
        }, delay);
    }

    /**
     * Copy pasted from Citizens2 /npc skin -url command, sorry for the try catches
     * but java is stupid and wont accept the throws in the method signature
     * @param skinTextureUrl URL Link to the texture
     * @param function Calbackfunction profiging the texture and signature
     */
    /**
     * TODO: Broken
     * @param skinTextureUrl
     * @param function
     */
    public void getTextureAndSig(String skinTextureUrl, CallbackDoubleString function) {
        CompletableFuture.runAsync(() -> {
            DataOutputStream out = null;
            BufferedReader reader = null;
            URL target = null;
            try {
                target = new URL("https://api.mineskin.org/generate/url");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) target.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            con.setDoOutput(true);
            con.setConnectTimeout(1000);
            con.setReadTimeout(30000);
            try {
                out = new DataOutputStream(con.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                out.writeBytes("url=" + URLEncoder.encode(skinTextureUrl, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject output;

            try {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                output = (JSONObject) new JSONParser().parse(reader);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
            JSONObject data = (JSONObject) output.get("data");
            String uuid = (String) data.get("uuid");
            JSONObject texture = (JSONObject) data.get("texture");
            String textureEncoded = (String) texture.get("value");
            String signature = (String) texture.get("signature");
            Bukkit.getScheduler().runTask(Civilizations.INSTANCE, () -> function.onComplete(textureEncoded, signature)); // make sure to set back to main thread
        });
    }
}
