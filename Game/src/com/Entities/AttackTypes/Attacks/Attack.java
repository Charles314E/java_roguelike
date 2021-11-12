package com.Entities.AttackTypes.Attacks;

import com.Entities.AttackTypes.ATMelee;
import com.Entities.AttackTypes.AttackType;
import com.Entities.Entity;
import com.System.Enums;

public class Attack {
    public Entity user;
    public AttackType type;
    public String name;
    public Enums.damageType damageType;
    public double attackMultiplier = 1;
    public double defenseMultiplier = 1;

    public Attack() {

    }

    public Attack(Entity user) {
        this.user = user;
        this.type =  new ATMelee(this);
        this.damageType = Enums.damageType.PHYSICAL;
    }

    public Attack(Entity user, String name, Enums.damageType damageType, int attackMultiplier, int defenseMultiplier) {
        this.user = user;
        this.name = name;
        this.damageType = damageType;
        this.attackMultiplier = attackMultiplier;
        this.defenseMultiplier = defenseMultiplier;
    }

    public Entity attack() {
        return type.attack();
    }
}
