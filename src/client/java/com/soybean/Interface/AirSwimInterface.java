package com.soybean.Interface;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface AirSwimInterface {

    Event<AirSwimInterface> EVENT = EventFactory.createArrayBacked(AirSwimInterface.class,
            (listeners) -> {
                return (entity) -> {
                    for (AirSwimInterface listener : listeners) {
                        listener.onSwimming(entity);
                    }
                };
            });

    void onSwimming(Entity entity);
}
