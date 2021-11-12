package com.GUI;

import com.System.Enums;
import com.System.Fonts;
import com.System.Maps.Map;
import com.methods;

import java.awt.*;

public class LevelTransition extends Transition {
    Map room;                               //The room the player is transitioning to.
    boolean leveledUp = false;              //Whether the player leveled up this transition.
    boolean switched = false;               //Whether the screen has switched from level up to the upgrade screen.
    boolean calculatingTime = false;        //Attributes regarding the totaling of time left on a level.
    boolean calculatedTime = false;
    int time = 0;
    int timeThreshold;
    public LevelUp levelEnd = null;         //Depending on whether the player has leveled up, this screen will show.
    public int monsterExperience;           //How much experience the player gained from killing enemies on this floor.

    //LevelTransition constructor. Set the distinct transition visuals of the room transition.
    public LevelTransition(GUI frame, Map room, int monsterExperience, boolean fadeIn) {
        super(frame, 1200, fadeIn, true, Color.BLACK, Color.WHITE, room.name, "");
        this.room = room;
        this.timeThreshold = room.timeThreshold;
        this.monsterExperience = monsterExperience;
        if (room.floor != 0) {
            this.subtitles[0] = "Floor " + Math.abs(room.floor);
            if (room.floor < 0) {
                this.subtitles[0] = "Sublevel " + Math.abs(room.floor);
            }
        }
    }

    //Develops the super function, creates a upgrades screen if the player has leveled up.
    public void transition() {
        super.transition();
        if (transitionTime > transitionLength * 0.15 && fadeIn && !(paused || leveledUp)) {
            paused = true;
            char[][] buttonGrid = new char[][] {
                    {'A', 'D'},
                    {'E', 'T'},
                    {'@', '@'}};
            Object[][] buttonList = new Object[][] {
                    {"Increase your attack to deal more damage.", 'A', Enums.statButton.ATTACK},
                    {"Increase your defense to take less damage from attacks.", 'D', Enums.statButton.DEFENSE},
                    {"Make enemies more powerful, gaining more experience in return.", 'E', Enums.statButton.DIFFICULTY},
                    {"Gain extra time to complete a floor. Does not count towards experience earned.", 'T', Enums.statButton.EXTRA_TIME},
                    {"Continue", '@', Enums.statButton.CONTINUE}};
            int[][] buttonAttributes = new int[][] {
                    {0, 1,  192,            frame.h / 5,            64,             64},
                    {0, 1,  frame.w - 256,  frame.h / 5,            64,             64},
                    {0, 1,  192,            (frame.h * 2) / 5,      64,             64},
                    {0, 10, frame.w - 256,  (frame.h * 2) / 5,      64,             64},
                    {0, -1, frame.w / 2,    (frame.h * 4) / 5,      frame.w - 256,  64}};
            levelEnd = new LevelUp(frame, room, buttonGrid, buttonList, buttonAttributes);
        }
        if (transitionTime > transitionLength * 0.5 && !(paused || calculatingTime || calculatedTime) && timeThreshold > -1) {
            levelEnd = null;
            paused = true;
            calculatingTime = true;
        }
        if (transitionTime > transitionLength * 0.75 && !switched) {
            frame.linkRoom(room);
            switched = true;
        }
        if (transitionTime == transitionLength && active) {
            frame.finishRoomSwitch(room);
            active = false;
        }
    }

    //Develops the super function. Includes time allocated to the new room.
    public void paintTransition(Graphics g) {
        super.paintTransition(g);
        if (active) {
            if (calculatingTime || calculatedTime) {
                if (calculatingTime) {
                    time += 1;
                    if (time >= timeThreshold) {
                        calculatingTime = false;
                        calculatedTime = true;
                        paused = false;
                    }
                }
                String timeFormatted = (time / 60) + ":" + String.format("%02d", (time % 60));
                int textAlignment = frame.getNewTextAlignment(g, timeFormatted, Enums.alignment.MIDDLE, Fonts.font.SUBTITLE);
                methods.drawString(g, timeFormatted, (frame.w / 2) - textAlignment, (frame.h * 4) / 5, Fonts.font.SUBTITLE, new Color(255, 255, 255, subtitleOpacities[0]));
                if (calculatedTime && frame.player.extraTime > 0) {
                    methods.drawString(g, " + " + frame.player.extraTime, (frame.w / 2) + textAlignment, (frame.h * 4) / 5, Fonts.font.SUBTITLE, new Color(55, 255, 0, subtitleOpacities[0]));
                }
            }
        }
    }
}
