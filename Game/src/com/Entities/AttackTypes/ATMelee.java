package com.Entities.AttackTypes;

import com.Entities.AttackTypes.Attacks.Attack;
import com.Entities.Entity;
import com.System.Enums;
import com.methods;

public class ATMelee extends AttackType {
    public ATMelee(Attack attack) {
        super(attack, "Melee", Enums.attackType.MELEE);
    }

    //Attack entities or coordinates.
    public Entity attack(Entity entity) {
        Entity hit = null;
        attack.user.attacking = true;
        if (attack.user.subimage == attack.user.attackSubimage) {
            if (entity != null) {
                if (!entity.invulnerable) {
                    int damage = Math.max(1, (attack.user.attack - entity.defense) + (attack.user.level - 1));
                    entity.damageHealth(damage, attack.attackMultiplier, attack.defenseMultiplier);
                    System.out.println(methods.tuple(attack.user.xDir, attack.user.yDir) + " Attacked " + entity + ", dealing " + damage + " damage.");
                    hit = entity;
                }
            } else {
                System.out.println(methods.tuple(attack.user.xDir, attack.user.yDir) + " Missed the attack.");
            }
        }
        attack.user.setAttackSubimage();
        return hit;
    }
    public Entity attack() {
        int x = attack.user.x + attack.user.xDir;
        int y = attack.user.y + attack.user.yDir;
        return attack(x, y);
    }
}
