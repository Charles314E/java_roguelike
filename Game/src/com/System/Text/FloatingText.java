package com.System.Text;

import com.GUI.HUD;
import com.System.Enums;
import com.System.Fonts;
import com.methods;

import java.awt.*;
import java.util.ArrayList;

public class FloatingText {
    public String text;                                     //What the text says.
    public int x = 0;                                       //Attributes dealing with the text's position and speed onscreen.
    public int y = 0;
    int xOffset = 0;
    int yOffset = 0;
    int xSpeed = 0;
    int ySpeed = -3;
    int xCap = 0;
    int yCap = -24;
    int colourTimer = 0;                                    //The timer for the text to change colour.
    int colourGap = 5;                                      //How many game ticks it takes for the text to change colour.
    public int alignmentOffset = 0;                         //The text's text alignment (justification value).
    ArrayList<Color> colours = new ArrayList<>();           //All of the colours the text can flash.
    Color colour;                                           //The text's current colour.
    int finishTimer = 0;                                    //The timer for the text to disappear after stopping.
    int finishWait = 10;                                    //How many game ticks it takes for the text to disappear.
    boolean active = true;                                  //Whether the text is visible.

    //FloatingText constructor. Set its text, position and colors.
    public FloatingText(String text, int x, int y) {
        this(text, x, y, Color.WHITE);
    }
    public FloatingText(String text, int x, int y, Color ... colours) {
        setText(text);
        setPosition(x, y);
        setColours(colours);
    }

    //Set the visible text.
    public void setText(String text) {
        this.text = text;
    }

    //Deactivate the text, rendering it invisble.
    public void deactivate() {
        this.active = false;
    }

    //Set the text's position onscreen.
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //Set the text's colour list.
    public void setColours(Color ... colours) {
        this.colours = new ArrayList<>();
        addColours(colours);
        this.colour = this.colours.get(0);
    }
    public void addColours(Color ... colours) {
        for (Color colour : colours) {
            this.colours.add(colour);
        }
    }

    //Cycle through the text's colour list.
    public void setSpeed(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    //Move through the text's colour list.
    public Color getNextColour() {
        int nextColour = colours.indexOf(colour) + 1;
        if (nextColour >= colours.size()) {
            nextColour = 0;
        }
        return colours.get(nextColour);
    }

    //Alter the text's colour.
    public void changeColour() {
        if (colourTimer < colourGap) {
            colourTimer += 1;
        }
        else {
            colour = getNextColour();
            colourTimer = 0;
        }
    }

    //Float the text using its speed. If it reaches its cap, freeze it and set it on a timer to disappear.
    public void moveText() {
        changeColour();
        xOffset = Math.min(Math.abs(xOffset + xSpeed), Math.abs(xCap)) * methods.sign(xSpeed);
        yOffset = Math.min(Math.abs(yOffset + ySpeed), Math.abs(yCap)) * methods.sign(ySpeed);
        if (((Math.abs(xOffset) >= Math.abs(xCap) && Math.abs(xCap) > 0) || (Math.abs(yOffset) >= Math.abs(yCap) && Math.abs(yCap) > 0)) || (xCap == 0 && yCap == 0)) {
            if (active) {
                if (finishTimer < finishWait) {
                    finishTimer += 1;
                }
                else {
                    deactivate();
                }
            }
        }
    }

    //Draw the text onscreen.
    public void display(Graphics g, double tileX, double tileY, int tileSize) {
        if (active) {
            methods.drawString(g, text, (int) ((tileX + methods.doubleDivision(xOffset, tileSize)) * tileSize) - alignmentOffset, (int) ((tileY + methods.doubleDivision(yOffset, tileSize)) * tileSize), Fonts.font.DAMAGE, colour);
        }
    }
}
