package com.System;

public class Enums {
    //Types of valid key presses.
    public enum key {
        LEFT, RIGHT, UP, DOWN, ATTACK, CANCEL
    }
    //Types of enemy attacks.
    public enum attackType {
        MELEE, PROJECTILE, ARTILLERY_AIMED, ARTILLERY_RANDOM, ALTER_TERRAIN, SUMMON
    }
    //The types of damage an attack can deal.
    public enum damageType {
        PHYSICAL, MAGICAL, FIRE, COLD, LIGHTNING, POISON, LIGHT, DARKNESS
    }
    //Types of enemy behaviour patterns.
    public enum enemyBehaviourType {
        RANDOM, RANDOM_UNTIL_APPROACHED, HOSTILE
    }
    //Types of text alignment.
    public enum alignment {
        LEFT, MIDDLE, RIGHT
    }
    //What happens when an animation ends.
    public enum animationEnd {
        DESTROY, REPEAT
    }
    //Types of buttons on the upgrades screen.
    public enum statButton {
        ATTACK, DEFENSE, DIFFICULTY, EXTRA_TIME, CONTINUE
    }
    //Whether a map is randomly generated or static.
    public enum mapLayout {
        RANDOMLY_GENERATED, STATIC
    }
}