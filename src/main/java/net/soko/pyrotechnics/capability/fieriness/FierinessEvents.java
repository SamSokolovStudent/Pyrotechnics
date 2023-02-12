package net.soko.pyrotechnics.capability.fieriness;

import net.minecraftforge.event.TickEvent;

public class FierinessEvents {

    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        FierinessManager manager = FierinessManager.get(event.level);
        manager.tick(event.level);
    }
}
