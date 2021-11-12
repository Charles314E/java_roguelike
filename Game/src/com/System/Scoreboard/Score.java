package com.System.Scoreboard;

public class Score implements Comparable<Score> {
    public String name;                         //The player's username.
    public String password;                     //The user's password.
    public int floor;                           //The floor the player has managed to reach.
    public int score;                           //Player score, levels, experience.
    public int experience;
    public int level;
    public int usableLevels;
    public int health;                          //Player stats.
    public int maxHealth;
    public int attack;
    public int defense;
    public int extraTime;
    public int difficulty;

    //Score constructor.
    public Score(String name, String password, int floor, int score, int experience, int level, int usableLevels, int health, int maxHealth, int attack, int defense, int extraTime, int difficulty) {
        this.name = name;
        this.password = password;
        this.floor = floor;
        this.score = score;
        this.experience = experience;
        this.level = level;
        this.usableLevels = usableLevels;
        this.health = health;
        this.maxHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.extraTime = extraTime;
        this.difficulty = difficulty;
    }

    //Sort Score instances using their score values.
    public int compareTo(Score score) {
        return score.score - this.score;
    }
}
