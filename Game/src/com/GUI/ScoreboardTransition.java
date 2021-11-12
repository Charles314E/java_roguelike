package com.GUI;

import com.System.Scoreboard.Scoreboard;

import java.awt.*;

public class ScoreboardTransition extends Transition {
    //LevelTransition constructor. Set the distinct transition visuals of the room transition.
    public ScoreboardTransition(GUI frame, Color backColour, Color textColour) {
        super(frame, 1200, false, false, backColour, textColour, "Scoreboard", "");
        String[] scoreboardList = new String[5];
        for (int i = 0; i < scoreboardList.length; i += 1) {
            scoreboardList[i] = Scoreboard.scoreboard.get(i).name + " " + Scoreboard.scoreboard.get(i).score;
        }
        setSubtitles(scoreboardList);
    }

    //Develops the super function, creates a upgrades screen if the player has leveled up.
    public void transition() {
        super.transition();
        if (transitionTime == transitionLength && active) {
            System.exit(0);
            active = false;
        }
    }
}
