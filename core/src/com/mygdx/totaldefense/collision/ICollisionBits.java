package com.mygdx.totaldefense.collision;

/**
 * Created by dubforce on 10/4/15.
 */
public interface ICollisionBits {
    short PLAYER = 0x1;
    short WALL = PLAYER << 1;
    short PROJECTILE = WALL << 1;
    short ENEMY = PROJECTILE << 1;
    short ENEMY_PROJECTILE = ENEMY << 1;
    short AUDIO_TRIGGER = ENEMY_PROJECTILE << 1;
    short DOOR_TRIGGER = AUDIO_TRIGGER << 1;
    short LIGHT = DOOR_TRIGGER << 1;
    short STATIC_OBJECT = LIGHT << 1;
}
