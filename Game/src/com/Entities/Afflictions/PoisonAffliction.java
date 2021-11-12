package com.Entities.Afflictions;

import com.Entities.Entity;

import java.awt.*;

public class PoisonAffliction extends Affliction {
    public int damage;

    public PoisonAffliction(Entity target, int level, int duration, int effectGap) {
        name = "Poisoned";
        setAttributes(target, level, duration, effectGap);
        setDamage();
    }

    public PoisonAffliction(Entity target, int level, int duration) {
        this(target, level, duration, 10);
    }

    public void setDamage() {
        damage = level;
    }

    public void startEffect() { }
    public void continueEffect() {
        target.damageHealth(damage, new Color(0, 155, 0));
    }
    public void endEffect() { }
}
