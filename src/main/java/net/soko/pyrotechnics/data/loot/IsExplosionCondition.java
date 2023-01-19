package net.soko.pyrotechnics.data.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class IsExplosionCondition implements LootItemCondition {

    public static final IsExplosionCondition INSTANCE = new IsExplosionCondition();

    @Override
    public LootItemConditionType getType() {
        return null;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return lootContext.hasParam(ModLootContextParams.EXPLOSION_BLOCK_INTERACTION);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.EXPLOSION_RADIUS);
    }

    public static class ExplosionSerializer implements Serializer<IsExplosionCondition> {

        @Override
        public void serialize(JsonObject pJson, IsExplosionCondition pValue, JsonSerializationContext pSerializationContext) {
        }

        @Override
        public IsExplosionCondition deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
            return IsExplosionCondition.INSTANCE;
        }
    }
}
