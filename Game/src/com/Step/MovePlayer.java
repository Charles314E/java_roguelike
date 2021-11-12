package com.Step;

import com.Entities.Player;
import com.GUI.GUI;
import com.GUI.ScoreboardTransition;
import com.GUI.Transition;
import com.System.Enums;
import com.System.Maps.Map;
import com.System.Scoreboard.Score;
import com.System.Scoreboard.Scoreboard;
import com.methods;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;

import static com.System.Scoreboard.Scoreboard.scoreboard;

public class MovePlayer implements ActionListener {
    GUI frame;                          //The frame the timer belongs to.
    Player player;                      //The player the timer is controlling.
    Transition gameEndScreen;           //The congratulations screen for making it through the game.

    //MovePlayer constructor.
    public MovePlayer(GUI frame, Player player) {
        this.frame = frame;
        this.player = player;
    }

    //Every game tick, move the player or make him attack. If the player is interacting with a floor exit, display the appropriate transition screen.
    public void actionPerformed(ActionEvent e) {
        if (player.map != null && frame.roomTransition == null) {
            for (int i = 0; i < player.afflictions.size(); i += 1) {
                player.afflictions.get(i).applyEffect();
            }
            int sightRadius = Math.min(frame.camera.w, frame.camera.h) / 2;
            methods.createCircle(player.map.visited, true, player.x, player.y, sightRadius);
            if (player.key != null) {
                if (!(player.key == Enums.key.ATTACK || player.attacking)) {
                    int[] move = player.move(player.key);
                    player.xDir = move[0];
                    player.yDir = move[1];
                    player.setSubimage();
                    if (!player.map.isWall(player.x + player.xDir, player.y + player.yDir)) {
                        System.out.println(methods.tuple(player.x, player.y) + " " + methods.tuple(player.xDir, player.yDir));
                    }
                    //player.map.display();
                    System.out.println();
                } else {
                    if (player.map.map[player.y][player.x] == 4 && Arrays.equals(player.map.exitDir, new int[]{player.xDir, player.yDir})) {
                        System.out.println("Leaving map...");
                        try {
                            int i = frame.rooms.indexOf(player.map);
                            Map room = frame.rooms.get(i + 1);
                            if ((Math.abs(room.floor) == Math.abs(player.map.floor) + 1) && room.name.equals(player.map.name)) {
                                frame.startRoomSwitch(room, true);
                            } else {
                                System.exit(0);
                            }
                        } catch (IndexOutOfBoundsException ex) {
                            if (gameEndScreen == null) {
                                GameTimer timer = (GameTimer) frame.timePlayer.getActionListeners()[0];
                                player.gainExperience(frame.player.map.monsterExperience + (int) (methods.doubleDivision(timer.timeLeft, timer.timeAllowed) * frame.player.map.w));
                                Score score = Scoreboard.writePlayerScore(player);
                                Collections.sort(Scoreboard.scoreboard);
                                Scoreboard.printScoreboard();
                                int rank = Scoreboard.scoreboard.indexOf(score) + 1;
                                gameEndScreen = new Transition(frame, true, false, Color.WHITE, Color.BLACK, "Congratulations!", "You gained " + score.score + " total experience, and achieved a rank of " + rank + "!") {
                                    @Override
                                    public void transition() {
                                        super.transition();
                                        if (transitionTime == transitionLength && active) {
                                            frame.transitions.add(new ScoreboardTransition(this.frame, backColour, textColour));
                                            active = false;
                                        }
                                    }
                                };
                                frame.transitions.add(gameEndScreen);
                            }
                        }
                    } else {
                        player.attacks[0].attack();
                    }
                }
            }
            player.setBoundingBox();
        }
    }
}
