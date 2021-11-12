package com;

import com.System.Enums;
import com.System.Fonts;
import com.System.Maps.Map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class methods {
    //Print multiples of variables inside brackets.
    public static String tuple(Object ... str) {
        String tuple = "(" + str[0];
        for (int i = 1; i < str.length; i += 1) {
            tuple += ", " + str[i];
        }
        tuple += ")";
        return tuple;
    }

    //Check whether a number is positive, negative or zero.
    public static int sign(double n) {
        if (n > 0) {
            return 1;
        }
        else if (n < 0) {
            return -1;
        }
        return 0;
    }

    //Fill a circle on a map with a particular value.
    public static int[] createCircle(int[][] map, int value, int x, int y, int circleRadius) {
        for (int my = 0; my < map.length; my += 1) {
            for (int mx = 0; mx < map[my].length; mx += 1) {
                if (methods.distance(mx, my, x, y) <= circleRadius) {
                    map[my][mx] = value;
                }
            }
        }
        return new int[] {x, y, circleRadius};
    }
    public static int[] createCircle(boolean[][] map, boolean value, int x, int y, int circleRadius) {
        for (int my = 0; my < map.length; my += 1) {
            for (int mx = 0; mx < map[my].length; mx += 1) {
                if (methods.distance(mx, my, x, y) <= circleRadius) {
                    map[my][mx] = value;
                }
            }
        }
        return new int[] {x, y, circleRadius};
    }

    //Divide integers without truncation.
    public static double doubleDivision(int dividend, int divisor) {
        return (double) dividend / (double) divisor;
    }
    public static int integerDivision(int dividend, int divisor) {
        return integerDivision(dividend, divisor, 1);
    }
    public static int integerDivision(int dividend, int divisor, int multiplier) {
        return (int) (doubleDivision(dividend, divisor) * multiplier);
    }

    //Obtain a random number in a range of values.
    public static double randomRange(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
    public static int randomRange(int min, int max) {
        return (int) Math.round(Math.random() * (max - min)) + min;
    }

    //Check distance between two points.
    public static double distance(int thisX, int thisY, int otherX, int otherY) {
        return Math.sqrt(Math.pow((otherX - thisX), 2) + Math.pow((otherY - thisY), 2));
    }

    //Compress and stretch a sine wave to transform it.
    public static double transformSine(double theta, double squeezeX, double squeezeY, double delay) {
        return squeezeY * Math.sin((theta * squeezeX) - (delay / Math.PI));
    }

    //Check if two circles intersect.
    public static boolean circlesIntersect(int x1, int y1, int r1, int x2, int y2, int r2) {
        double dist = methods.distance(x1, y1, x2, y2);
        double radSum = r1 + r2;
        if ((Math.pow(dist, 2) - Math.pow(radSum, 2)) < 0) {
            return true;
        }
        return true;
    }

    //Code sourced from stack overflow user Klark - https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
    public static BufferedImage imageDeepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    //Draw a styled string onscreen.
    public static void drawString(Graphics g, String str, int x, int y, Fonts.font font, Color colour) {
        g.setColor(colour);
        g.setFont(Fonts.fonts.get(font));
        g.drawString(str, x, y);
    }

    //Draw a textbox onscreen.
    public static void drawContainerBox(Graphics g, int x, int y, int w, int h) {
        drawContainerBox(g, x, y, w, h, new Color(0, 0, 0, 105));
    }
    public static void drawContainerBox(Graphics g, int x, int y, int w, int h, Color backColour) {
        g.setColor(Color.WHITE);
        g.drawRect(x - 2, y - 2, w + 4, h + 4);
        g.drawRect(x - 3, y - 3, w + 6, h + 6);
        g.drawRect(x - 5, y - 5, w + 10, h + 10);
        g.setColor(backColour);
        g.fillRect(x, y, w + 2, h + 2);
    }

    //Change an image to a solid colour.
    public static BufferedImage tintImage(BufferedImage sprite, Color colour) {
        for (int sy = 0; sy < sprite.getHeight(); sy += 1) {
            for (int sx = 0; sx < sprite.getWidth(); sx += 1) {
                if ((sprite.getRGB(sx, sy) >> 24) != 0x00) {
                    sprite.setRGB(sx, sy, colour.getRGB());
                }
            }
        }
        return sprite;
    }

    //Code sourced from stack overflow users tommo and alfonx - https://stackoverflow.com/questions/11552092/changing-image-opacity
    public static Graphics fadeImage(Graphics2D g, double opacity) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (opacity / 255)));
        return g;
    }

    //Obtain a sprite from a path.
    public static BufferedImage getSprite(String spritePath) {
        try {
            URL fileInput = main.class.getResource("/com/resources/" + spritePath + ".png");
            if (fileInput == null) {
                return null;
            }
            return ImageIO.read(fileInput.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    //Combine a list into a string with a delimiter.
    public static String concencateList(Object[] list, String limit) {
        if (list.length > 0) {
            String string = (String) list[0];
            for (int i = 1; i < list.length; i += 1) {
                string += limit + list[i];
            }
            System.out.println(string);
            return string;
        }
        return "";
    }

    //Change an object list into a string list.
    public static String[] changeListTypeToString(Object[] list) {
        String[] stringList = new String[list.length];
        for (int i = 0; i < list.length; i += 1) {
            stringList[i] = "" + list[i];
        }
        return stringList;
    }

    //Determine the direction of an arrow.
    public static int[] checkArrowDirection(char arrow) {
        int[] dir = {0, 0};
        switch (arrow) {
            case '^': dir[1] = -1; break;
            case 'v': dir[1] = 1; break;
            case '<': dir[0] = -1; break;
            case '>': dir[0] = 1; break;
        }
        return dir;
    }
}