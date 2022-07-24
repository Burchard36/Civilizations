package net.citizensnpcs.nms.v1_19_R1.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import net.citizensnpcs.util.NMS;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.*;

import java.lang.invoke.MethodHandle;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class CustomEntityRegistry extends DefaultedRegistry {
    private final BiMap<ResourceLocation, EntityType> entities = HashBiMap.create();
    private final BiMap<EntityType, ResourceLocation> entityClasses = this.entities.inverse();
    private final Map<EntityType, Integer> entityIds = Maps.newHashMap();
    private final MappedRegistry<EntityType<?>> wrapped;

    @SuppressWarnings("unchecked")
    public CustomEntityRegistry(DefaultedRegistry<EntityType<?>> original) throws Throwable {
        super(original.getDefaultKey().getNamespace(),
                (ResourceKey<Registry<EntityType<?>>>) IREGISTRY_RESOURCE_KEY.invoke(original),
                (Lifecycle) IREGISTRY_LIFECYCLE.invoke(original),
                (Function) IREGISTRY_CUSTOM_HOLDER_PROVDER.invoke(original));
        this.wrapped = original;
    }

    @Override
    public Object byId(int var0) {
        return this.wrapped.byId(var0);
    }

    public EntityType findType(Class<?> search) {
        return minecraftClassMap.inverse().get(search);
        /*
        for (Object type : wrapped) {
            if (minecraftClassMap.get(type) == search) {
                return (EntityTypes) type;
            }
        }
        return null;
        */
    }

    @Override
    public EntityType get(ResourceLocation key) {
        if (entities.containsKey(key)) {
            return entities.get(key);
        }

        return wrapped.get(key);
    }

    @Override
    public int getId(Object key) {
        if (entityIds.containsKey(key)) {
            return entityIds.get(key);
        }

        return wrapped.getId((EntityType) key);
    }

    @Override
    public ResourceLocation getKey(Object value) {
        if (entityClasses.containsKey(value)) {
            return entityClasses.get(value);
        }

        return wrapped.getKey((EntityType) value);
    }

    @Override
    public Optional getOptional(ResourceLocation var0) {
        if (entities.containsKey(var0)) {
            return Optional.of(entities.get(var0));
        }

        return this.wrapped.getOptional(var0);
    }

    @Override
    public Optional getRandom(RandomSource paramRandom) {
        return wrapped.getRandom(paramRandom);
    }

    public MappedRegistry<EntityType<?>> getWrapped() {
        return wrapped;
    }

    @Override
    public Iterator<Object> iterator() {
        return (Iterator) wrapped.iterator();
    }

    @Override
    public Set<Object> keySet() {
        return (Set) wrapped.keySet();
    }

    public void put(int entityId, ResourceLocation key, EntityType entityClass) {
        entities.put(key, entityClass);
        entityIds.put(entityClass, entityId);
    }

    private static final MethodHandle IREGISTRY_CUSTOM_HOLDER_PROVDER = NMS.getFirstGetter(MappedRegistry.class,
            Function.class);
    private static final MethodHandle IREGISTRY_LIFECYCLE = NMS.getFirstGetter(Registry.class, Lifecycle.class);
    // replace regex .*?> ([A-Z_]+).*?of\((.*?)::new.*?$ minecraftClassMap.put(EntityType.\1, \2.class);
    private static final MethodHandle IREGISTRY_RESOURCE_KEY = NMS.getGetter(Registry.class, "bT");
    private static final BiMap<EntityType, Class<?>> minecraftClassMap = HashBiMap.create();
    static {
        minecraftClassMap.put(EntityType.ALLAY, Allay.class);
        minecraftClassMap.put(EntityType.AREA_EFFECT_CLOUD, AreaEffectCloud.class);
        minecraftClassMap.put(EntityType.ARMOR_STAND, ArmorStand.class);
        minecraftClassMap.put(EntityType.ARROW, Arrow.class);
        minecraftClassMap.put(EntityType.AXOLOTL, Axolotl.class);
        minecraftClassMap.put(EntityType.BAT, Bat.class);
        minecraftClassMap.put(EntityType.BEE, Bee.class);
        minecraftClassMap.put(EntityType.BLAZE, Blaze.class);
        minecraftClassMap.put(EntityType.BOAT, Boat.class);
        minecraftClassMap.put(EntityType.CHEST_BOAT, ChestBoat.class);
        minecraftClassMap.put(EntityType.CAT, Cat.class);
        minecraftClassMap.put(EntityType.CAVE_SPIDER, CaveSpider.class);
        minecraftClassMap.put(EntityType.CHICKEN, Chicken.class);
        minecraftClassMap.put(EntityType.COD, Cod.class);
        minecraftClassMap.put(EntityType.COW, Cow.class);
        minecraftClassMap.put(EntityType.CREEPER, Creeper.class);
        minecraftClassMap.put(EntityType.DOLPHIN, Dolphin.class);
        minecraftClassMap.put(EntityType.DONKEY, Donkey.class);
        minecraftClassMap.put(EntityType.DRAGON_FIREBALL, DragonFireball.class);
        minecraftClassMap.put(EntityType.DROWNED, Drowned.class);
        minecraftClassMap.put(EntityType.ELDER_GUARDIAN, ElderGuardian.class);
        minecraftClassMap.put(EntityType.END_CRYSTAL, EndCrystal.class);
        minecraftClassMap.put(EntityType.ENDER_DRAGON, EnderDragon.class);
        minecraftClassMap.put(EntityType.ENDERMAN, EnderMan.class);
        minecraftClassMap.put(EntityType.ENDERMITE, Endermite.class);
        minecraftClassMap.put(EntityType.EVOKER, Evoker.class);
        minecraftClassMap.put(EntityType.EVOKER_FANGS, EvokerFangs.class);
        minecraftClassMap.put(EntityType.EXPERIENCE_ORB, ExperienceOrb.class);
        minecraftClassMap.put(EntityType.EYE_OF_ENDER, EyeOfEnder.class);
        minecraftClassMap.put(EntityType.FALLING_BLOCK, FallingBlockEntity.class);
        minecraftClassMap.put(EntityType.FIREWORK_ROCKET, FireworkRocketEntity.class);
        minecraftClassMap.put(EntityType.FOX, Fox.class);
        minecraftClassMap.put(EntityType.FROG, Frog.class);
        minecraftClassMap.put(EntityType.GHAST, Ghast.class);
        minecraftClassMap.put(EntityType.GIANT, Giant.class);
        minecraftClassMap.put(EntityType.GLOW_ITEM_FRAME, GlowItemFrame.class);
        minecraftClassMap.put(EntityType.GLOW_SQUID, GlowSquid.class);
        minecraftClassMap.put(EntityType.GOAT, Goat.class);
        minecraftClassMap.put(EntityType.GUARDIAN, Guardian.class);
        minecraftClassMap.put(EntityType.HOGLIN, Hoglin.class);
        minecraftClassMap.put(EntityType.HORSE, Horse.class);
        minecraftClassMap.put(EntityType.HUSK, Husk.class);
        minecraftClassMap.put(EntityType.ILLUSIONER, Illusioner.class);
        minecraftClassMap.put(EntityType.IRON_GOLEM, IronGolem.class);
        minecraftClassMap.put(EntityType.ITEM, ItemEntity.class);
        minecraftClassMap.put(EntityType.ITEM_FRAME, ItemFrame.class);
        minecraftClassMap.put(EntityType.FIREBALL, LargeFireball.class);
        minecraftClassMap.put(EntityType.LEASH_KNOT, LeashFenceKnotEntity.class);
        minecraftClassMap.put(EntityType.LIGHTNING_BOLT, LightningBolt.class);
        minecraftClassMap.put(EntityType.LLAMA, Llama.class);
        minecraftClassMap.put(EntityType.LLAMA_SPIT, LlamaSpit.class);
        minecraftClassMap.put(EntityType.MAGMA_CUBE, MagmaCube.class);
        minecraftClassMap.put(EntityType.MARKER, Marker.class);
        minecraftClassMap.put(EntityType.MINECART, Minecart.class);
        minecraftClassMap.put(EntityType.CHEST_MINECART, MinecartChest.class);
        minecraftClassMap.put(EntityType.COMMAND_BLOCK_MINECART, MinecartCommandBlock.class);
        minecraftClassMap.put(EntityType.FURNACE_MINECART, MinecartFurnace.class);
        minecraftClassMap.put(EntityType.HOPPER_MINECART, MinecartHopper.class);
        minecraftClassMap.put(EntityType.SPAWNER_MINECART, MinecartSpawner.class);
        minecraftClassMap.put(EntityType.TNT_MINECART, MinecartTNT.class);
        minecraftClassMap.put(EntityType.MULE, Mule.class);
        minecraftClassMap.put(EntityType.MOOSHROOM, MushroomCow.class);
        minecraftClassMap.put(EntityType.OCELOT, Ocelot.class);
        minecraftClassMap.put(EntityType.PAINTING, Painting.class);
        minecraftClassMap.put(EntityType.PANDA, Panda.class);
        minecraftClassMap.put(EntityType.PARROT, Parrot.class);
        minecraftClassMap.put(EntityType.PHANTOM, Phantom.class);
        minecraftClassMap.put(EntityType.PIG, Pig.class);
        minecraftClassMap.put(EntityType.PIGLIN, Piglin.class);
        minecraftClassMap.put(EntityType.PIGLIN_BRUTE, PiglinBrute.class);
        minecraftClassMap.put(EntityType.PILLAGER, Pillager.class);
        minecraftClassMap.put(EntityType.POLAR_BEAR, PolarBear.class);
        minecraftClassMap.put(EntityType.TNT, PrimedTnt.class);
        minecraftClassMap.put(EntityType.PUFFERFISH, Pufferfish.class);
        minecraftClassMap.put(EntityType.RABBIT, Rabbit.class);
        minecraftClassMap.put(EntityType.RAVAGER, Ravager.class);
        minecraftClassMap.put(EntityType.SALMON, Salmon.class);
        minecraftClassMap.put(EntityType.SHEEP, Sheep.class);
        minecraftClassMap.put(EntityType.SHULKER, Shulker.class);
        minecraftClassMap.put(EntityType.SHULKER_BULLET, ShulkerBullet.class);
        minecraftClassMap.put(EntityType.SILVERFISH, Silverfish.class);
        minecraftClassMap.put(EntityType.SKELETON, Skeleton.class);
        minecraftClassMap.put(EntityType.SKELETON_HORSE, SkeletonHorse.class);
        minecraftClassMap.put(EntityType.SLIME, Slime.class);
        minecraftClassMap.put(EntityType.SMALL_FIREBALL, SmallFireball.class);
        minecraftClassMap.put(EntityType.SNOW_GOLEM, SnowGolem.class);
        minecraftClassMap.put(EntityType.SNOWBALL, Snowball.class);
        minecraftClassMap.put(EntityType.SPECTRAL_ARROW, SpectralArrow.class);
        minecraftClassMap.put(EntityType.SPIDER, Spider.class);
        minecraftClassMap.put(EntityType.SQUID, Squid.class);
        minecraftClassMap.put(EntityType.STRAY, Stray.class);
        minecraftClassMap.put(EntityType.STRIDER, Strider.class);
        minecraftClassMap.put(EntityType.TADPOLE, Tadpole.class);
        minecraftClassMap.put(EntityType.EGG, ThrownEgg.class);
        minecraftClassMap.put(EntityType.ENDER_PEARL, ThrownEnderpearl.class);
        minecraftClassMap.put(EntityType.EXPERIENCE_BOTTLE, ThrownExperienceBottle.class);
        minecraftClassMap.put(EntityType.POTION, ThrownPotion.class);
        minecraftClassMap.put(EntityType.TRIDENT, ThrownTrident.class);
        minecraftClassMap.put(EntityType.TRADER_LLAMA, TraderLlama.class);
        minecraftClassMap.put(EntityType.TROPICAL_FISH, TropicalFish.class);
        minecraftClassMap.put(EntityType.TURTLE, Turtle.class);
        minecraftClassMap.put(EntityType.VEX, Vex.class);
        minecraftClassMap.put(EntityType.VILLAGER, Villager.class);
        minecraftClassMap.put(EntityType.VINDICATOR, Vindicator.class);
        minecraftClassMap.put(EntityType.WANDERING_TRADER, WanderingTrader.class);
        minecraftClassMap.put(EntityType.WARDEN, Warden.class);
        minecraftClassMap.put(EntityType.WITCH, Witch.class);
        minecraftClassMap.put(EntityType.WITHER, WitherBoss.class);
        minecraftClassMap.put(EntityType.WITHER_SKELETON, WitherSkeleton.class);
        minecraftClassMap.put(EntityType.WITHER_SKULL, WitherSkull.class);
        minecraftClassMap.put(EntityType.WOLF, Wolf.class);
        minecraftClassMap.put(EntityType.ZOGLIN, Zoglin.class);
        minecraftClassMap.put(EntityType.ZOMBIE, Zombie.class);
        minecraftClassMap.put(EntityType.ZOMBIE_HORSE, ZombieHorse.class);
        minecraftClassMap.put(EntityType.ZOMBIE_VILLAGER, ZombieVillager.class);
        minecraftClassMap.put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglin.class);
        minecraftClassMap.put(EntityType.FISHING_BOBBER, FishingHook.class);
    }
}