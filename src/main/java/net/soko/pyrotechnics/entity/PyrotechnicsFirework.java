package net.soko.pyrotechnics.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PyrotechnicsFirework extends FireworkRocketEntity {
    Vec3 loopVector, loopVelocity;
    public Vec3 originalVelocity;
    public float radianSpeed = 0.7f;
    public double loopScale = 0.25;
    public boolean shouldLoop = false;

    public PyrotechnicsFirework(EntityType<? extends FireworkRocketEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PyrotechnicsFirework(Level pLevel, double pX, double pY, double pZ, ItemStack pStack) {
        super(pLevel, pX, pY, pZ, pStack);
        registerLoop();
    }

    public PyrotechnicsFirework(Level pLevel, double pX, double pY, double pZ, ItemStack pStack, double loopScale, float radianSpeed) {
        this(pLevel, pX, pY, pZ, pStack);
        this.loopScale = loopScale;
        this.radianSpeed = radianSpeed;
        this.shouldLoop = true;
    }

    public PyrotechnicsFirework(Level pLevel, @Nullable Entity pShooter, double pX, double pY, double pZ, ItemStack pStack) {
        super(pLevel, pShooter, pX, pY, pZ, pStack);
    }

    public PyrotechnicsFirework(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        super(pLevel, pStack, pShooter);
    }

    public PyrotechnicsFirework(Level pLevel, ItemStack pStack, double pX, double pY, double pZ, boolean pShotAtAngle) {
        super(pLevel, pStack, pX, pY, pZ, pShotAtAngle);
    }

    public PyrotechnicsFirework(Level pLevel, ItemStack pStack, Entity pShooter, double pX, double pY, double pZ, boolean pShotAtAngle) {
        super(pLevel, pStack, pShooter, pX, pY, pZ, pShotAtAngle);
    }

    public void registerLoop() {
        loopVector = new Vec3(
                (Math.random() - 0.5)*radianSpeed,
                (Math.random() - 0.5)*radianSpeed,
                (Math.random() - 0.5)*radianSpeed);
        loopVelocity = new Vec3(
                (Math.random() - 0.5),
                (Math.random() - 0.5),
                (Math.random() - 0.5)
        );
        originalVelocity = this.getDeltaMovement();
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldLoop) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.01, 0));
            loopVelocity = loopVelocity.add(loopVelocity.cross(loopVector));
            loopVelocity = loopVelocity.normalize();
            this.setDeltaMovement(originalVelocity.add(loopVelocity.scale(loopScale)));
            Vec3 entityPosition = this.position().add(this.getDeltaMovement());
            double d0 = entityPosition.horizontalDistance();
            this.setYRot((float)(Mth.atan2(entityPosition.x, entityPosition.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(entityPosition.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
    }
}

