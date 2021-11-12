package com.System.Text;

import java.awt.*;

public class ExperienceText extends FloatingText {
    //ExperienceText constructor. Make the text flash yellow and green.
    public ExperienceText(int experience, int x, int y) {
        super(experience + " EXP", x, y);
        addColours(Color.YELLOW, new Color(55, 205, 0));
    }

    //Give the text the 'EXP' suffix.
    public void setExperience(int experience) {
        setText(experience + " EXP");
    }
}
