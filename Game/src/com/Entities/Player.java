package com.Entities;

import com.Entities.AttackTypes.Attacks.Attack;
import com.GUI.ScoreboardTransition;
import com.GUI.Transition;
import com.System.Enums;
import com.System.Maps.Map;
import com.System.Scoreboard.Score;
import com.System.Scoreboard.Scoreboard;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;

public class Player extends Entity {
    public Enums.key key = null;                                            //The currently pressed key.
    public String password = "password";                                    //The user's password.
    private boolean dead;                                                   //Whether the player is dead.
    public int experience = 0;                                              //Attributes dealing with experience, score and level.
    private int levelThreshold = 10;
    public int toLevelUp;
    public int totalExperience = 0;
    public int usableLevels = 0;
    public int extraTime = 0;                                               //How much extra time the player has to complete each level.
    public int difficulty = 0;                                              //How many levels each enemy is buffed by.
    public HashMap<Character, Integer> extraStats = new HashMap<>();        //How the player's stats will increase on this level up

    //Player constructor. Fetches their sprite.
    public Player(String name, int level) {
        this.spritePath = spriteBaseFolder + "Player/";
        this.name = name;
        this.level = level;
        this.space = new int[][] {{2}};
        this.maxHealth = 25;
        this.health = this.maxHealth;
        this.toLevelUp = level * levelThreshold;
        this.attacks = new Attack[] { new Attack(this) };
        getSprite();
    }
    public Player(String name, int level, Map map) {
        this(name, level);
        this.map = map;
    }
    public Player(String name, int level, Map map, int x, int y) {
        this(name, level, map);
        teleport(x, y);
    }

    //If the player is dead, record their score and send them to the game over screen.
    public void checkHealth() {
        if (health == 0) {
            if (!dead) {
                Score score = Scoreboard.writePlayerScore(this);
                Collections.sort(Scoreboard.scoreboard);
                int rank = Scoreboard.scoreboard.indexOf(score) + 1;
                Scoreboard.printScoreboard();
                Transition gameOverScreen = new Transition(map.frame, 1200, true, false, Color.RED, Color.WHITE, "Game Over", "You gained " + score.score + " total experience, and obtained a rank of " + rank + ".") {
                    @Override
                    public void transition() {
                        super.transition();
                        if (transitionTime == transitionLength && active) {
                            frame.transitions.add(new ScoreboardTransition(this.frame, backColour, textColour));
                            active = false;
                        }
                    }
                };
                map.frame.transitions.add(gameOverScreen);
                dead = true;
            }
        }
    }
    //Grant the player experience. Level them up if appropriate.
    public boolean gainExperience(int exp) {
        return gainExperience(exp, true);
    }
    public boolean gainExperience(int exp, boolean levelUp) {
        if (map != null) {
            map.monsterExperience += exp;
            map.frame.textStores.get("damage").addExperienceText(exp, x, y);
        }
        else {
            totalExperience += exp;
            experience += exp;
        }
        if (levelUp) {
            return levelUp();
        }
        return false;
    }

    //Increase the player's level, and overflow any remaining experience onto the next level.
    public boolean levelUp() {
        if (experience >= toLevelUp) {
            level += 1;
            usableLevels += 1;
            experience = experience - toLevelUp;
            this.toLevelUp = level * levelThreshold;
            return true;
        }
        return false;
    }

    //Apply the current upgrades to the player's stats and then remove them from the map.
    public void increaseStats() {
        attack += extraStats.getOrDefault('A', 0);
        defense += extraStats.getOrDefault('D', 0);
        difficulty += extraStats.getOrDefault('E', 0);
        extraTime += extraStats.getOrDefault('T', 0);
        extraStats.remove('A');
        extraStats.remove('D');
        extraStats.remove('E');
        extraStats.remove('T');
    }

    //Change the player's current map. Alone this won't work. For a full removal, the GUI's unlinkRoom() function will need to be called.
    public void setMap(Map map) {
        this.map = map;
    }
}
