package com.Entities.Enemies;

import com.Entities.AttackTypes.Attacks.*;
import com.Entities.Enemy;
import com.System.Enums;
import com.System.Maps.Map;

public class BTLSlime extends Enemy {
    public BTLSlime(Map map) {
        super("Slime", 1, map,
                3, 0,
                3, 0.5, 10, 1.0, 5,
                false, false,
                2,
                Enums.enemyBehaviourType.RANDOM_UNTIL_APPROACHED);

        attacks = new Attack[] {
                new Attack(this),
        };
    }
}
