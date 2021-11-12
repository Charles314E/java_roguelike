package com.Entities.Enemies;

import com.Entities.AttackTypes.Attacks.*;
import com.Entities.Enemy;
import com.System.Enums;
import com.System.Maps.Map;

public class BTLBat extends Enemy {
    public BTLBat(Map map) {
        super("Bat", 1, map,
                1, 0,
                2, 1, 6, 1.0, 4,
                true, false,
                1,
                Enums.enemyBehaviourType.HOSTILE);

        attacks = new Attack[] {
                new PoisonBite(this),
        };
    }
}
