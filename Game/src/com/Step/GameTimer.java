package com.Step;

import com.Entities.Player;
import com.GUI.GUI;
import com.System.Text.TextStore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameTimer implements ActionListener {
    GUI frame;                                  //The frame the timer belongs to.
    Player player;                              //The player the timer is tracking.
    int minutes;                                //How much time the player has to find the floor's exit.
    int seconds;
    public int time = 0;
    public int timeAllowed;
    public int timeLeft;
    int decisecond = 0;                         //The number of GameTimer ticks it takes for a second to pass.
    int timeGap = 40;

    //GameTimer constructor.
    public GameTimer(GUI frame, Player player) {
        this.frame = frame;
        this.player = player;
    }
    public GameTimer(GUI frame, Player player, int time) {
        this(frame, player);
        if (time > -1) {
            this.timeAllowed = time + player.extraTime;
            setTime(timeAllowed);
        }
    }
    public GameTimer(GUI frame, Player player, int minutes, int seconds) {
        this(frame, player);
        this.minutes = minutes;
        this.seconds = seconds;
        if (minutes > -1 && seconds > -1) {
            this.timeAllowed = (minutes * 60) + seconds + player.extraTime;
            setTime(timeAllowed);
        }
    }

    //Set the floor countdown to display in the title bar.
    public void setTime(int time) {
        if (player.map != null) {
            timeLeft = time;
            frame.setTitle(frame.title + " - " + (timeLeft / 60) + ":" + String.format("%02d", timeLeft % 60) + " - " + player.totalExperience + " score, " + player.map.enemyList.size() + " enemies");
        }
    }

    //Every GameTimer tick, flush all of the unused floating text out of the frame's stores and continue counting down the clock.
    public void actionPerformed(ActionEvent e) {
        frame.textStores.get("damage").flushStore();
        for (int i = 0; i < frame.textStores.get("damage").floatingText.size(); i += 1) {
            frame.textStores.get("damage").floatingText.get(i).moveText();
        }
        if (player.map.timeThreshold > -1) {
            decisecond += 1;
            if (decisecond == timeGap) {
                decisecond = 0;
                if (time < timeAllowed) {
                    time += 1;
                    setTime(timeAllowed - time);
                } else {
                    System.exit(0);
                }
            }
        }
    }
}
