package com.System;

import com.main;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class Fonts {
    public static HashMap<font, Font> fonts = new HashMap<>();                      //All of the fonts currently installed in the program.
    public enum font {                                                              //Types of program font.
        TITLE, SUBTITLE, TEXT, DAMAGE
    }

    //Add a new font to the database, taking a font file from the fonts folder and assigning it a font type.
    public static boolean addFont(String filepath, font key, int size) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //Class thisClass = Class.forName("Fonts");
            //FileInputStream fileInput = new FileInputStream(filepath);
            InputStream fileInput = main.class.getResource("/com/resources/fonts/" + filepath).openStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, fileInput).deriveFont((float) size);
            fonts.put(key, font);
            ge.registerFont(font);
            System.out.println("[FNT]: " + font);
            return true;
        } catch (IOException e) {
            System.out.println("[DBG]: File not found.");
            return false;
        } catch (FontFormatException e) {
            System.out.println("[DBG]: Incorrect font format.");
            return false;
        }
    }
}
