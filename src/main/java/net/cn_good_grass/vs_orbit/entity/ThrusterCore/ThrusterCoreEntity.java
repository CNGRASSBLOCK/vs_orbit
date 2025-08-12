
package net.cn_good_grass.vs_orbit.entity.ThrusterCore;

import net.cn_good_grass.vs_orbit.block.VSOrbitModBlocks;
import net.cn_good_grass.vs_orbit.block.block_entities.JumpEngineControllerBlockEntity;
import net.cn_good_grass.vs_orbit.entity.VSOrbitModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class ThrusterCoreEntity extends PathfinderMob implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ThrusterCoreEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ThrusterCoreEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ThrusterCoreEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ThrusterCoreEntity(PlayMessages.SpawnEntity packet, Level world) { this(VSOrbitModEntities.THRUSTER_CORE.get(), world); }

	public ThrusterCoreEntity(EntityType<ThrusterCoreEntity> type, Level world) {
		super(type, world);
		setNoAi(true);
		//this.setAnimation("spend");
		this.noPhysics = true;
		this.setNoGravity(true);
		this.noCulling = true;
		this.entityData.define(engine_pos, new BlockPos(0, 0, 0));
	}

	@Override
	public void baseTick() {
		BlockState blockState = this.level().getBlockState(this.entityData.get(engine_pos));
		if (!blockState.is(VSOrbitModBlocks.jump_engine_controller.get())) { this.discard(); } else { JumpEngineControllerBlockEntity blockEntity = (JumpEngineControllerBlockEntity) this.level().getBlockEntity(this.entityData.get(engine_pos)); if (blockEntity == null) { this.discard(); } else { if (!blockEntity.structure_state.equals("right")) { this.discard(); } } }
	}

	@Override public boolean removeWhenFarAway(double distance) { return false; }
	@Override public boolean isPersistenceRequired() { return true; }
	@Override public boolean shouldRenderAtSqrDistance(double distance) { return true; }
	@Override public MobType getMobType() { return MobType.UNDEFINED; }
	@Override public boolean causeFallDamage(float l, float d, DamageSource source) { return false; }
	@Override public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL))
			return super.hurt(source, amount);
		return false;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();;
		builder = builder.add(Attributes.MAX_HEALTH, 1024.0);
		return builder;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
		this.setNoGravity(true);
	}

	public static void init() {}
	//变量
	public static final EntityDataAccessor<Float> scare = SynchedEntityData.defineId(ThrusterCoreEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<BlockPos> engine_pos = SynchedEntityData.defineId(ThrusterCoreEntity.class, EntityDataSerializers.BLOCK_POS);
	public void setScare(Float this_scare) { this.entityData.set(scare, this_scare); }
	public Float getScare() { return this.entityData.get(scare); }
	//数据同步
	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.entityData.get(TEXTURE));
		compound.putString("Animation", this.entityData.get(ANIMATION));
		compound.putFloat("scare", this.getScare());
		compound.putIntArray("engine_pos", new ArrayList<>(List.of(this.entityData.get(engine_pos).getX(), this.entityData.get(engine_pos).getY(), this.entityData.get(engine_pos).getZ())));
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture")) this.entityData.set(TEXTURE, compound.getString("Texture"));
		if (compound.contains("Animation")) this.entityData.set(ANIMATION, compound.getString("Animation"));
		if (compound.contains("scare")) this.setScare(compound.getFloat("scare"));
		if (compound.contains("engine_pos") && compound.getIntArray("scare").length == 3)  this.entityData.set(engine_pos, new BlockPos(compound.getIntArray("scare")[0], compound.getIntArray("scare")[1], compound.getIntArray("scare")[2]));
	}

	//下面是geckolib的

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(scare, 1f);

		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "main");
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.entityData.get(ANIMATION).isEmpty()) return event.setAndContinue(RawAnimation.begin().thenLoop("spend"));
		return PlayState.STOP;
	}

	private PlayState procedurePredicate(AnimationState event) {
		event.getController().setAnimation(RawAnimation.begin().thenPlay(this.entityData.get(ANIMATION)));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	@Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}
