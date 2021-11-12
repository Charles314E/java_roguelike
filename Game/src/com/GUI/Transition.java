package com.GUI;

import com.System.Enums;
import com.System.Fonts;
import com.System.Maps.Map;
import com.methods;

import java.awt.*;

public class Transition {
    public GUI frame;                                   //The frame the transition is stored in.
    String title;                                       //The big text that displays prominently onscreen.
    String[] subtitles;                                 //The smaller text below the title.
    boolean fadeIn;                                     //Whether the transition fades in or out or simply remains opaque towards the start and end.
    boolean fadeOut;
    public boolean active = true;                       //Whether the transition is visible.
    public boolean paused = false;                      //Whether the transition is paused.
    public boolean moved = false;                       //Whether the player has been moved from the previous room.
    public int transitionTime = 0;                      //How far along the transition is on its timeline.
    public int transitionLength = 600;                  //How long the transition takes to complete.
    public int baseTransitionLength = transitionLength;
    public int subtitleTime = 0;                        //How long the subtitles last for.
    public Color backColour;                            //The transition's colours and final opacities.
    public Color textColour;
    int backOpacity = 0;
    int titleOpacity = 0;
    int[] subtitleOpacities;
    int subtitleHeight = 60;
    int subtitleListHeight;

    //Transition constructor.
    public Transition() {
        
    }
    public Transition(GUI frame, boolean fadeIn, boolean fadeOut, Color backColour, Color textColour, String title, String ... subtitles) {
        this.frame = frame;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.title = title;
        this.backColour = backColour;
        this.textColour = textColour;
        setSubtitles(subtitles);
    }
    public Transition(GUI frame, int transitionLength, boolean fadeIn, boolean fadeOut, Color backColour, Color textColour, String title, String ... subtitles) {
        this(frame, fadeIn, fadeOut, backColour, textColour, title, subtitles);
        setTransitionLength(transitionLength);
    }

    public void setSubtitles(String ... subtitles) {
        this.subtitles = subtitles;
        this.subtitleOpacities = new int[subtitles.length];
        this.subtitleListHeight = (int) (subtitleHeight * methods.doubleDivision(subtitles.length, 2));
        this.subtitleTime = (int) Math.max(0, 0.1 * (subtitles.length - 1));
        System.out.println(methods.tuple("subtitleListHeight", subtitles.length, this.subtitleListHeight));
    }

    public void setTransitionLength(int length) {
        baseTransitionLength = length;
        transitionLength = baseTransitionLength + subtitleTime;
    }

    //Sets all of the events that will happen throughout the transition.
    public void transition() {
        if (transitionTime > transitionLength * 0.1 && !moved) {
            frame.startRoomSwitch(null, false);
            moved = true;
        }
    }

    //Uses plateaued sine waves to produce a fade in and out without breaking the graphics.
    public void setTextOpacity() {
        if (!paused) {
            transitionTime += 1;
        }
        double transitionWave = methods.doubleDivision(transitionTime, transitionLength) * Math.PI;
        backOpacity = (int) (Math.max(0, Math.min(1, methods.transformSine(transitionWave, 1, 3, 0))) * 255);
        titleOpacity = (int) (Math.max(0, Math.min(1, methods.transformSine(transitionWave, 1.8, 3, baseTransitionLength * 0.2))) * 255);
        for (int i = 0; i < subtitleOpacities.length; i += 1) {
            subtitleOpacities[i] = (int) (Math.max(0, Math.min(1, methods.transformSine(transitionWave, 2.6, 2, baseTransitionLength * (0.4 + (0.1 * (subtitles.length - (i + 1))))))) * 255);
        }
        if (transitionTime < transitionLength * 0.25 && !fadeIn) {
            backOpacity = 255;
            for (int i = 0; i < subtitleOpacities.length; i += 1) {
                subtitleOpacities[i] = 0;
            }
        }
        if (transitionTime > transitionLength * 0.75 && !fadeOut) {
            backOpacity = 255;
            for (int i = 0; i < subtitleOpacities.length; i += 1) {
                subtitleOpacities[i] = 0;
            }
        }
        if (!paused) {
            //System.out.println(methods.tuple("transition", transitionTime, transitionWave, backOpacity, titleOpacity, subtitleOpacities[0], paused));
        }
    }

    //Draws the opacities of the different parts of the transition as bars that change along the timeline. Used for debugging purposes.
    public void drawWaves(Graphics g) {
        methods.drawContainerBox(g, 8, 8, 96, 96, new Color(55, 55, 55, 105));
        g.setColor(Color.WHITE);
        g.fillRect(23, (int) (10 + (1 - methods.doubleDivision(backOpacity, 255)) * 94), 2, (int) (methods.doubleDivision(backOpacity, 255) * 94));
        g.fillRect(55, (int) (10 + (1 - methods.doubleDivision(titleOpacity, 255)) * 94), 2, (int) (methods.doubleDivision(titleOpacity, 255) * 94));
        for (int i = 0; i < subtitleOpacities.length; i += 1) {
            int saturation = (int) (255 * methods.doubleDivision(i + 1, subtitleOpacities.length));
            g.setColor(new Color(255, saturation, saturation));
            g.fillRect(87, (int) (10 + (1 - methods.doubleDivision(subtitleOpacities[i], 255)) * 94), 2, (int) (methods.doubleDivision(subtitleOpacities[i], 255) * 94));
        }
    }

    //Draw the transition.
    public void paintTransition(Graphics g) {
        if (active) {
            setTextOpacity();
            transition();
            g.setColor(new Color(backColour.getRed(), backColour.getGreen(), backColour.getBlue(), backOpacity));
            g.fillRect(0, 0, frame.w, frame.h);
            int textAlignment;
            textAlignment = frame.getNewTextAlignment(g, title, Enums.alignment.MIDDLE, Fonts.font.TITLE);
            int titleOffset = 0;
            if (subtitleListHeight > 0) {
                titleOffset = subtitleHeight;
            }
            methods.drawString(g, title, (frame.w / 2) - textAlignment, (frame.h / 2) - (subtitleListHeight + titleOffset), Fonts.font.TITLE, new Color(textColour.getRed(), textColour.getGreen(), textColour.getBlue(), titleOpacity));
            for (int i = 0; i < subtitles.length; i += 1) {
                if (!subtitles[i].equals("")) {
                    textAlignment = frame.getNewTextAlignment(g, subtitles[i], Enums.alignment.MIDDLE, Fonts.font.SUBTITLE);
                    methods.drawString(g, subtitles[i], (frame.w / 2) - textAlignment, ((frame.h * 3) / 5) - subtitleListHeight + (subtitleHeight * i), Fonts.font.SUBTITLE, new Color(textColour.getRed(), textColour.getGreen(), textColour.getBlue(), subtitleOpacities[i]));
                }
            }
            drawWaves(g);
        }
    }
}
