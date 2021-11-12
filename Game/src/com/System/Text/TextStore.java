package com.System.Text;

import com.System.Enums;

import java.awt.*;
import java.util.ArrayList;

public class TextStore {
    public ArrayList<FloatingText> floatingText = new ArrayList<>();    //A list of all of the game's floating text.

    //Add new floating text.
    public void addText(String text, int x, int y) {
        floatingText.add(new FloatingText(text, x, y));
    }
    public void addText(String text, int x, int y, Color... colours) {
        floatingText.add(new FloatingText(text, x, y, colours));
    }

    //Add different types of floating text.
    public void addText(FloatingText text) {
        floatingText.add(text);
    }
    public void addDamageText(int damage, int x, int y) {
        addText(new DamageText(damage, x, y));
    }
    public void addExperienceText(int experience, int x, int y) {
        addText(new ExperienceText(experience, x, y));
    }

    //Flush any unused floating text from the store.
    public void flushStore() {
        for (int i = 0; i < floatingText.size(); i += 1) {
            FloatingText text = floatingText.get(i);
            if (!text.active) {
                floatingText.remove(text);
            }
        }
    }
}
