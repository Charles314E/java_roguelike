package com.GUI;

import com.Entities.Enemy;
import com.Entities.Entity;
import com.Entities.Player;
import com.Step.GameTimer;
import com.System.Enums;
import com.System.Maps.Map;
import com.System.Text.FloatingText;
import com.methods;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;

import static java.awt.Color.*;

public class Camera extends JPanel {
    public int w, h, sw, sh;                    //Camera's tile dimensions and the screen's dimensions.
    int cx, cy, px, py;                         //The current screen coordinates being accessed.
    int tileSize;                               //The size of tiles in the game.
    Player player;                              //The player the camera is following.
    GUI frame;                                  //The camera's parent frame.
    Map map;                                    //The map the camera is currently in.
    HUD hud;                                    //The HUD the camera is using.

    //Camera constructor.
    public Camera(GUI frame, int w, int h, Map map, HUD hud) {
        super();
        this.frame = frame;
        this.w = w;
        this.h = h;
        this.sw = hud.sw;
        this.sh = hud.sh;
        this.tileSize = Math.min(this.sw / this.w, this.sh / this.h);
        this.sw = Math.min(this.sw, this.tileSize * this.w);
        this.sh = Math.min(this.sh, this.tileSize * this.h);
        hud.camera = this;
        this.player = hud.player;
        this.map = map;
        this.hud = hud;
    }

    //The camera draws all visual elements onto the screen. All objects with a Graphics function offload the painting to the camera.
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map != null) {
            Area obscurity = null;
            Color darkness = null;
            if (map.timeThreshold > -1) {
                obscurity = new Area(new Rectangle(0, 0, sw, sh));
                darkness = new Color(0, 0, 0, 51);
                if (frame.roomTransition == null) {
                    GameTimer lightTimer = (GameTimer) frame.timePlayer.getActionListeners()[0];
                    Object[] light = hud.createLight((Graphics2D) g, lightTimer.time, lightTimer.timeAllowed);
                    obscurity = (Area) light[0];
                    darkness = (Color) light[1];
                }
            }
            paintRoom(g, obscurity);
            if (obscurity != null) {
                hud.paintLight((Graphics2D) g, obscurity, darkness);
            }
            hud.paintHUD(g);
        }
        for (int i = 0; i < frame.transitions.size(); i += 1) {
            frame.transitions.get(i).paintTransition(g);
        }
        if (frame.roomTransition != null) {
            if (frame.roomTransition.active) {
                frame.roomTransition.paintTransition(g);
                if (frame.roomTransition != null) {
                    if (frame.roomTransition.levelEnd != null) {
                        frame.roomTransition.levelEnd.paintSelf(g);
                    }
                }
            }
        }
        repaint();
    }

    //Change the camera's current map. Alone this won't work. For a full removal, the GUI's unlinkRoom() function will need to be called.
    public void setMap(Map map) {
        this.map = map;
        if (this.map != null) {
            map.camera = this;
        }
    }

    //Calculate the camera's current coordinates in the map and thus determine which part of the map to draw.
    public void calculateCoordinates(int x, int y) {
        px = x + player.x - (w / 2);
        py = y + player.y - (h / 2);
        cx = methods.integerDivision(x, w, sw);
        cy = methods.integerDivision(y, h, sh);
    }

    //Get the camera's bounding box.
    public Rectangle getCameraBox() {
        int x1 = player.x - (w / 2);
        int y1 = player.y - (h / 2);
        return new Rectangle(x1, y1, w, h);
    }

    //Paint the room the camera is in, drawing its tiles using the Background class' autotiling functionality. The separate for loops draw the tiles
    //in layers, making sure everything is properly positioned in the z-axis.
    public void paintRoom(Graphics g, Area light) {
        for (int y = 0; y < h; y += 1) {
            for (int x = 0; x < w; x += 1) {
                calculateCoordinates(x, y);
                map.background.drawRoomFloor(g, px, py, cx, cy, tileSize);
            }
        }
        for (int y = 0; y < h; y += 1) {
            for (int x = 0; x < w; x += 1) {
                calculateCoordinates(x, y);
                map.background.drawRoomTiles(g, px, py, cx, cy, tileSize);
            }
        }
        for (int y = 0; y < h; y += 1) {
            for (int x = 0; x < w; x += 1) {
                calculateCoordinates(x, y);
                map.background.drawRoomDoors(g, px, py, cx, cy, tileSize);
            }
        }
        Rectangle cBox = getCameraBox();
        for (int y = 0; y < h; y += 1) {
            for (int x = 0; x < w; x += 1) {
                calculateCoordinates(x, y);
                if (px == player.x && py == player.y) {
                    player.drawSelf(g, cx, cy, tileSize, GREEN, null);
                }
                for (Enemy enemy: map.enemyList) {
                    if (px == enemy.x && py == enemy.y) {
                        boolean canSee = true;
                        if (light != null) {
                            canSee = !(light.contains(cx, cy) && light.contains(cx + tileSize - 1, cy) && light.contains(cx, cy + tileSize - 1) && light.contains(cx + tileSize - 1, cy + tileSize - 1));
                        }
                        if (canSee) {
                            enemy.drawSelf(g, cx, cy, tileSize, RED, light);
                            hud.paintEnemyHealth(g, cx, cy, enemy);
                        }
                    }
                }
            }
        }
        for (FloatingText text : frame.textStores.get("damage").floatingText) {
            frame.alignFloatingText(g, text, Enums.alignment.MIDDLE);
            if (cBox.contains(text.x, text.y)) {
                int textX = text.x - (int) cBox.getBounds().getX();
                int textY = text.y - (int) cBox.getBounds().getY();
                text.display(g, textX + 0.5, textY + 0.25, tileSize);
            }
        }
    }
}