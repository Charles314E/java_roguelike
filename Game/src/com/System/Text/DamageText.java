package com.System.Text;

import java.awt.*;
import java.util.ArrayList;

public class DamageText extends FloatingText {
    //DamageText constructor. If the damage is a critical hit, change the colour.
    public DamageText(int damage, int x, int y) {
        this(damage, x, y, false);
    }
    public DamageText(int damage, int x, int y, boolean critical) {
        super("-" + damage, x, y);
        setColours(Color.RED);
        if (critical) {
            setColours(Color.YELLOW);
        }
    }

    //Format the damage text to be a subtraction operation.
    public void setDamage(int damage) {
        setText("-" + damage);
    }
}
