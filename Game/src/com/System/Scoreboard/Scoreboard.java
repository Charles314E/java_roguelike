package com.System.Scoreboard;

import com.Entities.Player;
import com.methods;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Scoreboard {
    public static ArrayList<Score> scoreboard = new ArrayList<>();          //The game's scoreboard.
    public static int printTime = 0;

    //Read the scoreboard file from a filepath.
    public static ArrayList<String[]> readScoreFile(String filePath, boolean errorMessage) {
        ArrayList<String[]> scoreList = new ArrayList<>();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = fileReader.readLine()) != null) {
                scoreList.add(line.split(", "));
            }
            System.out.println(scoreList.size());
            updateScoreboard(scoreList);
            return scoreList;
        }
        catch (IOException e) {
            System.out.println("[DBG]: Could not find the file.");
            return null;
        }
    }

    //Write to the scoreboard file, creating a new score if one hasn't already been added.
    public static void writeScoreFile(String filePath, Score newScore, boolean errorMessage) {
        ArrayList<String[]> scores = readScoreFile(filePath, false);
        try {
            if (scores != null) {
                for (Score score : scoreboard) {
                    int line = -1;
                    for (int i = 0; i < scores.size(); i += 1) {
                        if (score.name.equals(scores.get(i)[0]) && Integer.parseInt(scores.get(i)[4]) > 0) {
                            line = i;
                        }
                    }
                    Object[] scoreList = new Object[]{score.name, score.password, score.floor, score.score, score.experience, score.level, score.usableLevels, score.health, score.maxHealth, score.attack, score.defense, score.extraTime, score.difficulty};
                    if (line > -1) {
                        if (!Arrays.equals(scoreList, scores.get(line))) {
                            scores.set(line, methods.changeListTypeToString(scoreList));
                        }
                    } else {
                        scores.add(methods.changeListTypeToString(scoreList));
                    }
                }
                System.out.println("[DBG]: Reading to '" + filePath + "' with " + scores.size() + " entries...");
                File file = new File(filePath);
                if (file.exists()) {
                    System.out.println("[DBG]: File found.");
                    BufferedWriter fileWriter;
                    if (newScore == null) {
                        fileWriter = new BufferedWriter(new FileWriter(filePath, false));
                        for (String[] score : scores) {
                            fileWriter.write(methods.concencateList(score, ", "));
                            fileWriter.newLine();
                            System.out.println("Hello");
                        }
                    }
                    else {
                        fileWriter = new BufferedWriter(new FileWriter(filePath, true));
                        Object[] scoreList = new Object[]{newScore.name, newScore.password, newScore.floor, newScore.score, newScore.experience, newScore.level, newScore.usableLevels, newScore.health, newScore.maxHealth, newScore.attack, newScore.defense, newScore.extraTime, newScore.difficulty};
                        fileWriter.write(methods.concencateList(scoreList, ", "));
                        fileWriter.newLine();
                    }
                    fileWriter.close();
                }
            }
            else {
                addFile(new File(filePath), null);
            }
        }
        catch (IOException e) {
            System.out.println("[DBG]: Could not write to the file.");
            e.printStackTrace();
        }
    }

    public static void updateScoreboard(ArrayList<String[]> scoreStrings) {
        for (int i = 0; i < scoreStrings.size(); i += 1) {
            String[] scoreString = scoreStrings.get(i);
            String[] userNames = new String[2];
            int[] userStats = new int[scoreString.length - 2];
            userNames[0] = scoreString[0];
            userNames[1] = scoreString[0];
            for (int j = 2; j < scoreString.length; j += 1) {
                userStats[j - 2] = Integer.parseInt(scoreString[j]);
            }
            scoreboard.add(new Score(userNames[0], userNames[1], userStats[0], userStats[1], userStats[2], userStats[3], userStats[4], userStats[5], userStats[6], userStats[7], userStats[8], userStats[9], userStats[10]));
        }
    }

    //Create a new scoreboard file and attempt to write to it.
    public static File addFile(File file, Score score) throws IOException {
        if (file.createNewFile()) {
            System.out.println("[DBG]: Created new file at '" + file.getAbsolutePath() + "'.");
            writeScoreFile(file.getAbsolutePath(), score, false);
            return file;
        }
        System.out.println("[DBG]: Could not create a new file.");
        writeScoreFile(file.getAbsolutePath(), score, false);
        return null;
    }

    //Add a new score to the scoreboard.
    public static Score updateScore(String name, String password, int floor, int score, int experience, int level, int usableLevels, int health, int maxHealth, int attack, int defense, int extraTime, int difficulty) {
        Score newScore = new Score(name, password, floor, score, experience, level, usableLevels, health, maxHealth, attack, defense, extraTime, difficulty);
        scoreboard.add(newScore);
        return newScore;
    }

    //Create a new score using the player's stats.
    public static Score recordPlayerStats(Player player) {
        System.out.println("[DBG]: Recorded player score.");
        return updateScore(player.name, player.password, Math.abs(player.map.floor), player.totalExperience, player.experience, player.level, player.usableLevels, player.health, player.maxHealth, player.attack, player.defense, player.extraTime, player.difficulty);
    }

    //Write the player's stats to the scoreboard file.
    public static Score writePlayerScore(Player player) {
        Score score = recordPlayerStats(player);
        writeScoreFile("scores/scoreboard.txt", score, false);
        return score;
    }

    public static void printScoreboard() {
        System.out.println("SCOREBOARD");
        Collections.sort(scoreboard);
        for (int i = 0; i < Math.min(5, scoreboard.size()); i += 1) {
            System.out.println(methods.tuple(i + 1, scoreboard.get(i).name, scoreboard.get(i).score));
        }
    }
}
