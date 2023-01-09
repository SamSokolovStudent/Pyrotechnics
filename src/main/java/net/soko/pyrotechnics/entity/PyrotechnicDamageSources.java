package net.soko.pyrotechnics.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Fireball;

import javax.annotation.Nullable;

public class PyrotechnicDamageSources {
    public static DamageSource firework(Entity entity) {
        if (entity == null) {
            return new DamageSource("firework").setScalesWithDifficulty().setExplosion();
        }
        return new EntityDamageSource("firework.player", entity).setScalesWithDifficulty().setExplosion();
    }

    public static DamageSource firework(DamageSource source) {
        if (source instanceof EntityDamageSource) {
            return firework(((EntityDamageSource) source).getEntity());
        }
        return firework((Entity) null);
    }

    public static DamageSource fireball(Fireball pFireball, @Nullable Entity pIndirectEntity) {
        return pIndirectEntity == null ? (new IndirectEntityDamageSource("onFire", pFireball, pFireball)).setIsFire().setProjectile() : (new IndirectEntityDamageSource("large_fireball", pFireball, pIndirectEntity)).setIsFire().setProjectile();
    }
}
