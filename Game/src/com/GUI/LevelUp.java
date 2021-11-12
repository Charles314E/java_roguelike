package com.GUI;

import com.Step.GameTimer;
import com.System.Enums;
import com.System.Fonts;
import com.System.Maps.Map;
import com.methods;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelUp {
    GUI frame;                                          //The frame this screen is assigned to.
    char[][] buttonGrid;                                //Attributes dealing with how the buttons display and work.
    Object[][] buttonList;
    int[][] buttonAttributes;
    BufferedImage[] buttonSprites;
    String[][] buttonDescriptions;
    int[] playerStatistics;                             //The player's current statistics.
    int cursorX = 0;                                    //The location of the cursor on the buttonGrid.
    int cursorY = 0;
    boolean clicked = false;                            //Whether a button has been clicked. To prevent accidental button overreach when held down.
    GameTimer timer;                                    //The GameTimer that stores the countdown timer for the previous floor.
    int time;                                           //The current balance of time, used for experience totalling.
    boolean active = true;                              //Whether this screen is visible.
    boolean started = false;                            //Whether the experience has started to be totalled up.
    boolean waiting = false;                            //Whether the experience has been finished totalling up.
    boolean finished = false;                           //Whether the entire screen is done and waiting to be pushed into the next floor's transition.
    boolean leveledUp = false;                          //Whether the player leveled up this floor.
    boolean gainingMonsterExperience = false;           //Whether the battle experience is drained.
    boolean gainingTimeExperience = false;              //Whether the experience is drained from time left on the previous floor.
    boolean canIncreaseStats = false;                   //Whether the player can upgrade their stats.
    int startGap = 0;                                   //The timer between the appearance of the screen and when experience starts totalling up.
    int gainedExperience = 0;                           //How much experience has been gained.
    int gainGap = 0;                                    //The speed at which experience is totalled up.
    int monsterExperience;                              //The amount of experience a player gained from battles in the current floor.

    //LevelUp constructor. Fetches button sprites and sets the bill on the countdown clock.
    public LevelUp(GUI frame, Map room, char[][] buttonGrid, Object[][] buttonList, int[][] buttonAttributes) {
        this.frame = frame;
        this.buttonGrid = buttonGrid;
        this.buttonList = buttonList;
        this.buttonAttributes = buttonAttributes;
        getButtonSprites();
        getButtonDescriptions();
        getPlayerStatistics();
        timer = (GameTimer) frame.timePlayer.getActionListeners()[0];
        time = (int) (methods.doubleDivision(timer.timeLeft, timer.timeAllowed) * room.w);
        System.out.println(time);
        monsterExperience = frame.roomTransition.monsterExperience;
        System.out.println(methods.tuple(buttonGrid[0].length, buttonGrid.length));
    }

    //Fetches the sprites for each of the buttons based on their character designations and stores them in a list.
    public void getButtonSprites() {
        buttonSprites = new BufferedImage[buttonList.length];
        for (int i = 0; i < buttonSprites.length; i += 1) {
            char buttonChar = (char) buttonList[i][1];
            buttonSprites[i] = methods.getSprite("sprites/Icons/" + buttonChar);
        }
    }

    //Stores the descriptions for each of the buttons.
    public void getButtonDescriptions() {
        buttonDescriptions = new String[][] {
                {"Increase the damage you deal to enemies you attack."},
                {"Reduce the damage you take from enemy attacks."},
                {"Strengthen the monsters within each floor, but also", "increase their experience yield."},
                {"Increase the time you have to complete each floor."}
        };
    }

    //Stores the descriptions for each of the buttons.
    public void getPlayerStatistics() {
        playerStatistics = new int[] {
                frame.player.attack,
                frame.player.defense,
                frame.player.difficulty,
                frame.player.extraTime
        };
    }

    //Obtain the button the player currently has selected.
    public char getSelectedButton() {
        return buttonGrid[cursorY][cursorX];
    }

    //Check whether a button is selected.
    public boolean getSelected(char button) {
        if (getSelectedButton() == button) {
            return true;
        }
        return false;
    }

    //Find a button's index in the list.
    public int findButtonIndex(char button) {
        for (int i = 0; i < buttonList.length; i += 1) {
            if (button == (char) buttonList[i][1]) {
                return i;
            }
        }
        return -1;
    }

    //Return a button's action command.
    public Enums.statButton getSelectedCommand() {
        for (Object[] button : buttonList) {
            if (getSelectedButton() == (char) button[1]) {
                System.out.println(methods.tuple("button", button[1], button[2]));
                return (Enums.statButton) button[2];
            }
        }
        return null;
    }

    //Activate the selected button's action command to either increase stats or continue to the next floor.
    public void confirmButton() {
        Enums.statButton command = getSelectedCommand();
        System.out.println(command == Enums.statButton.CONTINUE);
        if (command == Enums.statButton.CONTINUE) {
            finished = true;
        }
        else {

            int i = findButtonIndex(getSelectedButton());
            if (i != -1) {
                if (frame.player.usableLevels > 0) {
                    int value = Math.max(0, buttonAttributes[i][0] + buttonAttributes[i][1]);
                    buttonAttributes[i][0] = value;
                    frame.player.usableLevels -= 1;
                }
            }
        }
    }

    //Activate the selected button's cancel command to reduce stats, if applicable.
    public void cancelButton() {
        Enums.statButton command = getSelectedCommand();
        if (command != Enums.statButton.CONTINUE) {
            int i = findButtonIndex(getSelectedButton());
            if (i != -1) {
                if (buttonAttributes[i][0] > 0) {
                    int value = Math.max(0, buttonAttributes[i][0] - buttonAttributes[i][1]);
                    buttonAttributes[i][0] = value;
                    frame.player.usableLevels += 1;
                }
            }
        }
    }

    //Draw the level up screen, in both its parts. In addition, deal with all of the keyboard input as well, checking whether the player has finished
    //with each screen and allowing them to move between buttons on the upgrades screen.
    public void paintSelf(Graphics g) {
        if (!finished) {
            if (frame.player.key == Enums.key.ATTACK) {
                if (!leveledUp) {
                    if (waiting) {
                        leveledUp = true;
                        clicked = true;
                        waiting = false;
                    }
                }
            }
            if (clicked) {
                if (frame.player.key == null) {
                    clicked = false;
                }
            }
            paintLevelUp(g);
            if (leveledUp) {
                paintStatUp(g);
                if (!clicked && frame.player.key != null) {
                    if (frame.player.key == Enums.key.ATTACK) {
                        confirmButton();
                    } else if (frame.player.key == Enums.key.CANCEL) {
                        cancelButton();
                    }
                    else {
                        switch (frame.player.key) {
                            case LEFT:
                                cursorX = Math.max(0, cursorX - 1);
                                break;
                            case RIGHT:
                                cursorX = Math.min(buttonGrid[0].length - 1, cursorX + 1);
                                break;
                            case UP:
                                cursorY = Math.max(0, cursorY - 1);
                                break;
                            case DOWN:
                                cursorY = Math.min(buttonGrid.length - 1, cursorY + 1);
                                break;
                        }
                    }
                    clicked = true;
                }
            }
        }
        else {
            if (active) {
                for (int i = 0; i < buttonList.length; i += 1) {
                    if (buttonAttributes[i][1] != -1) {
                        char button = (char) buttonList[i][1];
                        frame.player.extraStats.put(button, buttonAttributes[i][0]);
                        System.out.println(methods.tuple(button, frame.player.extraStats.get(button)));
                    }
                    frame.player.increaseStats();
                }
                if (frame.roomTransition != null) {
                    frame.roomTransition.paused = false;
                    frame.roomTransition.leveledUp = true;
                }
            }
            active = false;
        }
    }

    //Paint the level up screen, drawing a purple experience bar and listing the reward values for completing the stage. The experience bar gradually
    //fills up as the reward values are drained. If the player finishes with this screen, they are then moved to the upgrades screen or, if they have
    //no available levels with which to earn upgrades, automatically skipped along to the floor transition as the level up screen is deactivated.
    public void paintLevelUp(Graphics g) {
        if (active && !leveledUp) {
            if (started) {
                gainGap += 1;
                if (gainGap == 10) {
                    if (gainedExperience < monsterExperience) {
                        gainGap = 0;
                        gainedExperience += 1;
                        gainingMonsterExperience = true;
                        frame.player.gainExperience(1);
                    }
                    else {
                        if (gainedExperience < time + monsterExperience) {
                            gainGap = 0;
                            gainedExperience += 1;
                            gainingTimeExperience = true;
                            frame.player.gainExperience(1);
                        }
                        else {
                            waiting = true;
                            if (frame.player.usableLevels > 0) {
                                canIncreaseStats = true;
                            }
                        }
                    }
                }
            }
            else {
                startGap += 1;
                if (startGap >= 120) {
                    started = true;
                }
            }
            int monsterExperienceDrained = (int) Math.max(0, monsterExperience * (1 - methods.doubleDivision(gainedExperience, monsterExperience)));
            int timeDrained = (int) (timer.timeLeft * (1 - methods.doubleDivision(gainedExperience - monsterExperience, time)));
            String timeFormatted = (timeDrained / 60) + ":" + String.format("%02d", (timeDrained % 60));
            frame.setTitle(frame.title + " - " + timeFormatted + " - " + frame.player.totalExperience + " score");
            Color backColour = new Color(25, 0, 25, 255);
            Color barColour = new Color(105, 0, 155, 255);
            int statX1 = 64;
            int statX2 = frame.w - statX1;
            int statY = frame.h / 5;
            int barX = 64;
            int barY = (frame.h * 3) / 5;
            int barWidth = frame.w - (barX * 2);
            int barHeight = 32;
            int textAlignment;

            if (gainingMonsterExperience) {
                methods.drawString(g, "Battle Experience", statX1, statY, Fonts.font.SUBTITLE, Color.WHITE);
                textAlignment = frame.getNewTextAlignment(g, Integer.toString(monsterExperienceDrained), Enums.alignment.RIGHT, Fonts.font.SUBTITLE);
                methods.drawString(g, Integer.toString(monsterExperienceDrained), statX2 - textAlignment, statY, Fonts.font.SUBTITLE, Color.WHITE);
            }
            if (gainingTimeExperience) {
                methods.drawString(g, "Time Left", statX1, statY + 76, Fonts.font.SUBTITLE, Color.WHITE);
                textAlignment = frame.getNewTextAlignment(g, timeFormatted, Enums.alignment.RIGHT, Fonts.font.SUBTITLE);
                methods.drawString(g, timeFormatted, statX2 - textAlignment, statY + 76, Fonts.font.SUBTITLE, Color.WHITE);
            }

            methods.drawString(g, Integer.toString(frame.player.experience), barX, barY, Fonts.font.SUBTITLE, Color.WHITE);
            textAlignment = frame.getNewTextAlignment(g, Integer.toString(frame.player.toLevelUp), Enums.alignment.RIGHT, Fonts.font.SUBTITLE);
            methods.drawString(g, Integer.toString(frame.player.toLevelUp), barX + barWidth - textAlignment, barY, Fonts.font.SUBTITLE, Color.WHITE);
            textAlignment = frame.getNewTextAlignment(g, Integer.toString(frame.player.level), Enums.alignment.MIDDLE, Fonts.font.TITLE);
            methods.drawString(g, Integer.toString(frame.player.level), barX + (barWidth / 2) - textAlignment, barY, Fonts.font.TITLE, Color.WHITE);
            textAlignment = frame.getNewTextAlignment(g, "LEVEL", Enums.alignment.MIDDLE, Fonts.font.TEXT);
            methods.drawString(g, "LEVEL", barX + (barWidth / 2) - textAlignment, barY - 122, Fonts.font.TEXT, Color.WHITE);

            frame.hud.paintValueBarSquare(g, frame.player.experience, frame.player.toLevelUp, barColour, backColour, barX, barY, barWidth, barHeight);
        }
    }

    //If the player can increase their stats, draw the upgrades screen. Draw each button at their locations onscreen, with selected buttons drawn as
    //bright and fully opaque, whereas unselected buttons are dull and faded. Increased stats are shown in small text next to the buttons. If the
    //player finishes with this screen, they move on to the floor transition as the level up screen is deactivated.
    public void paintStatUp(Graphics g) {
        if (active && (frame.player.usableLevels > 0 || canIncreaseStats)) {
            if (!finished) {
                int textAlignment;
                textAlignment = frame.getNewTextAlignment(g, Integer.toString(frame.player.usableLevels), Enums.alignment.MIDDLE, Fonts.font.TITLE);
                methods.drawString(g, Integer.toString(frame.player.usableLevels), (frame.w / 2) - textAlignment, frame.w / 5, Fonts.font.TITLE, Color.WHITE);
                textAlignment = frame.getNewTextAlignment(g, "POINTS", Enums.alignment.MIDDLE, Fonts.font.TEXT);
                methods.drawString(g, "POINTS", (frame.w / 2) - textAlignment, (frame.w / 5) - 122, Fonts.font.TEXT, Color.WHITE);
                int n = -1;
                for (int i = 0; i < buttonList.length; i += 1) {
                    int buttonX = buttonAttributes[i][2];
                    int buttonY = buttonAttributes[i][3];
                    int buttonWidth = buttonAttributes[i][4];
                    int buttonHeight = buttonAttributes[i][5];
                    char buttonChar = (char) buttonList[i][1];
                    String buttonName = (String) buttonList[i][0];
                    Color statColour;
                    if (buttonSprites[i] == null) {
                        statColour = Color.RED;
                        Color colour = new Color(255, 255, 255, 105);
                        if (getSelected(buttonChar)) {
                            colour = Color.WHITE;
                        }
                        textAlignment = frame.getNewTextAlignment(g, buttonName, Enums.alignment.MIDDLE, Fonts.font.TITLE);
                        methods.drawString(g, buttonName, buttonX - textAlignment, buttonY + buttonHeight, Fonts.font.TITLE, colour);
                    }
                    else {
                        statColour = Color.WHITE;
                        if (getSelected(buttonChar)) {
                            g = methods.fadeImage((Graphics2D) g, 255);
                            textAlignment = frame.getNewTextAlignment(g, buttonName, Enums.alignment.MIDDLE, Fonts.font.TEXT);
                            methods.drawString(g, buttonName, (frame.w / 2) - textAlignment, (frame.w * 4) / 5, Fonts.font.TEXT, Color.WHITE);
                            n = i;
                        }
                        else {
                            g = methods.fadeImage((Graphics2D) g, 105);
                        }
                        g.drawImage(buttonSprites[i], buttonX, buttonY, null);
                        g = methods.fadeImage((Graphics2D) g, 255);
                    }
                    textAlignment = 0;
                    try {
                        if (playerStatistics[i] > 0) {
                            textAlignment = frame.getNewTextAlignment(g, "" + playerStatistics[i], Enums.alignment.RIGHT, Fonts.font.TEXT);
                            methods.drawString(g, "" + playerStatistics[i], buttonX + buttonWidth - 8, buttonY + buttonHeight - 8, Fonts.font.TEXT, Color.WHITE);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {

                    }
                    if (buttonAttributes[i][0] > 0) {
                        methods.drawString(g, "+" + buttonAttributes[i][0], buttonX + buttonWidth - 8 + textAlignment, buttonY + buttonHeight - 8, Fonts.font.TEXT, Color.GREEN);
                    }
                }
                if (n > -1) {
                    for (int j = 0; j < buttonDescriptions[n].length; j += 1) {
                        textAlignment = frame.getNewTextAlignment(g, buttonDescriptions[n][j], Enums.alignment.MIDDLE, Fonts.font.TEXT);
                        methods.drawString(g, buttonDescriptions[n][j], (frame.w / 2) - textAlignment, (frame.h * 3) / 5 + (j * 32), Fonts.font.TEXT, Color.WHITE);
                    }
                }
            }
        }
        else {
            finished = true;
        }
    }
}
