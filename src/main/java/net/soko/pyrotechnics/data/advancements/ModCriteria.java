package net.soko.pyrotechnics.data.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;
import net.soko.pyrotechnics.PyroTechnics;

public class ModCriteria {
    public static final PlayerTrigger BARTER_PYRITE = register(new PlayerTrigger(new ResourceLocation(PyroTechnics.MOD_ID, "barter_pyrite")));

    public static void init(){
        // NO-OP
    }

    public static <T extends CriterionTrigger<?>> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }
}
