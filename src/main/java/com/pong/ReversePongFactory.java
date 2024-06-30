package com.pong;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

public class ReversePongFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        return FXGL
                .entityBuilder(data)
                .view("ball.png")
                .build();
    }
}
