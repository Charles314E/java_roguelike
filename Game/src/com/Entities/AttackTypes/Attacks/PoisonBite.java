package com.Entities.AttackTypes.Attacks;

import com.Entities.Afflictions.PoisonAffliction;
import com.Entities.AttackTypes.ATMelee;
import com.Entities.Entity;
import com.System.Enums;

public class PoisonBite extends Attack {
    public PoisonBite(Entity user) {
        super(user, "Venomous Bite", Enums.damageType.PHYSICAL, 1, 1);
        this.type = new ATMelee(this);
    }

    public Entity attack() {
        Entity target = super.attack();
        if (target != null) {
            if (!target.isAfflicted("Poisoned")) {
                target.afflictions.add(new PoisonAffliction(target, 1, 5));
            }
        }
        return target;
    }
}
