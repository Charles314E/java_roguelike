package com.Step;

import com.System.Enums;
import com.System.Maps.Map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpawnEnemy implements ActionListener {
    Map map;                                //The map the enemies are spawning into.
    int entityCap;                          //The amount of entities allowed to exist in a room at one time.
    int radius;                             //Enemies cannot spawn any closer to the player than this radius.

    //SpawnEnemy constructor.
    public SpawnEnemy(Map map, int entityCap, int radius) {
        this.map = map;
        this.entityCap = entityCap;
        this.radius = radius;
    }

    //Every game tick, wait to spawn an enemy.
    public void actionPerformed(ActionEvent e) {
        if (map.layout == Enums.mapLayout.RANDOMLY_GENERATED) {
            map.spawnInitialEnemies(1, radius, entityCap);
        }
    }
}
