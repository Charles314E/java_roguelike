package com.Entities.AttackTypes;

import com.Entities.AttackTypes.Attacks.Attack;
import com.Entities.Entity;
import com.System.Enums;

public class AttackType {
    public Attack attack;
    public String name;
    public Enums.attackType attackType;

    public AttackType() {

    }
    public AttackType(Attack attack, String name, Enums.attackType type) {
        this.attack = attack;
        this.name = name;
        this.attackType = type;
    }

    public Entity attack(int targetX, int targetY) {
        Entity target = attack.user.map.checkEntityPositions(targetX, targetY);
        attack(target);
        return target;
    }
    public Entity attack(Entity entity) {
        return null;
    }
    public Entity attack() {
        return null;
    }
}
