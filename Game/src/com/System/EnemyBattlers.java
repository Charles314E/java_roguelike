package com.System;

import com.Entities.Enemies.*;
import com.Entities.Enemy;

import java.util.HashMap;

public class EnemyBattlers {
    public static HashMap<String, Class<?>> battlers = new HashMap<>();                                     //A list of all enemy battlers.
    public static Enums.enemyBehaviourType passive = Enums.enemyBehaviourType.RANDOM;                       //Enemy behaviour types.
    public static Enums.enemyBehaviourType neutral = Enums.enemyBehaviourType.RANDOM_UNTIL_APPROACHED;
    public static Enums.enemyBehaviourType hostile = Enums.enemyBehaviourType.HOSTILE;

    //Creates battlers (definitions) for all enemies in the game.
    public static void compileEnemyDictionary() {
        createNewEnemyBattler("Slime", "BTLSlime");
        createNewEnemyBattler("Bat", "BTLBat");
    }

    public static void createNewEnemyBattler(String name, String className) {
        try {
            battlers.put(name, Class.forName("com.Entities.Enemies." + className));
        }
        catch (ClassNotFoundException e) {

        }
    }
}
