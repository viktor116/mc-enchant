package com.soybean.Interface;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface RambleInterface {

    Event<RambleInterface> EVENT = EventFactory.createArrayBacked(RambleInterface.class,
            (listeners) -> {
                return (entity) -> {
                    for (RambleInterface listener : listeners) {
                        listener.onJump(entity);
                    }
                };
            });

    void onJump(Entity entity);
}
