package com.System;

import com.System.Maps.Map;
import com.methods;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

public class Background {
    public String backgroundPath = "backgrounds/";                      //The backgrounds filepath.
    public Image tileSheet;                                             //The tiles the background uses to draw into the screen.
    public int tw;                                                      //How many tiles to record on a line of the sheet before moving into the next.
    public Map map;                                                     //The map the background is assigned to.
    public String name;                                                 //The filename of the background's tilesheet.
    public int tileSize = 32;                                           //The dimensions of a tile in the tilesheet.
    public HashMap<String, int[]> tileMap = new HashMap<>();            //All of the wall and floor tiles.
    public HashMap<String, int[]> doorMap = new HashMap<>();            //All of the door entrance and exit tiles.

    //Background constructor. Fetch the filename of the tilesheet.
    public Background(String name, Map map) {
        this.name = name;
        this.map = map;
        getBackground();
    }

    //Fetch the tilesheet for the background.
    public void getBackground() {
        this.backgroundPath += name;
        this.tileSheet = methods.getSprite(backgroundPath);
        System.out.println("[DBG]: " + name + " background " + tileSheet + " - " + backgroundPath + ".png");
        if (tileSheet != null) {
            tw = tileSheet.getWidth(null) / tileSize;
            initializeTileMap();
        }
    }

    //Set up the code mappings for each terrain tile in the tilesheet.
    public void initializeTileMap() {
        ArrayList<int[]> tiles = new ArrayList<>();
        ArrayList<int[]> doors = new ArrayList<>();
        //Top, Bottom, Left, Right
        tiles.add(new int[] {1, 1, 1, 1});
        tiles.add(new int[] {1, 0, 1, 1});
        tiles.add(new int[] {0, 1, 1, 1});
        tiles.add(new int[] {1, 1, 0, 1});
        tiles.add(new int[] {1, 1, 1, 0});
        tiles.add(new int[] {0, 0, 1, 1});
        tiles.add(new int[] {1, 1, 0, 0});
        tiles.add(new int[] {1, 0, 0, 0});
        tiles.add(new int[] {0, 1, 0, 0});
        tiles.add(new int[] {0, 0, 0, 1});
        tiles.add(new int[] {0, 0, 1, 0});
        tiles.add(new int[] {0, 1, 0, 1});
        tiles.add(new int[] {0, 1, 1, 0});
        tiles.add(new int[] {1, 0, 0, 1});
        tiles.add(new int[] {1, 0, 1, 0});
        tiles.add(new int[] {1, 1, 1, 1, 1, 1, 1, 0});
        tiles.add(new int[] {1, 1, 1, 1, 1, 1, 0, 1});
        tiles.add(new int[] {1, 1, 1, 1, 1, 0, 1, 1});
        tiles.add(new int[] {1, 1, 1, 1, 0, 1, 1, 1});
        tiles.add(new int[] {0, 1, 0, 1, 0, 0, 0, 1});
        tiles.add(new int[] {0, 1, 1, 0, 0, 0, 1, 0});
        tiles.add(new int[] {1, 0, 0, 1, 0, 1, 0, 0});
        tiles.add(new int[] {1, 0, 1, 0, 1, 0, 0, 0});
        tiles.add(new int[] {1, 1, 0, 1, 0, 0, 0, 1});
        tiles.add(new int[] {1, 1, 1, 0, 0, 0, 1, 0});
        tiles.add(new int[] {1, 1, 0, 1, 0, 1, 0, 0});
        tiles.add(new int[] {1, 1, 1, 0, 1, 0, 0, 0});
        tiles.add(new int[] {0, 1, 1, 1, 0, 0, 0, 1});
        tiles.add(new int[] {0, 1, 1, 1, 0, 0, 1, 0});
        tiles.add(new int[] {1, 0, 1, 1, 0, 1, 0, 0});
        tiles.add(new int[] {1, 0, 1, 1, 1, 0, 0, 0});
        tiles.add(new int[] {1, 1, 1, 1, 0, 0, 1, 1});
        tiles.add(new int[] {1, 1, 1, 1, 1, 1, 0, 0});
        tiles.add(new int[] {1, 1, 1, 1, 0, 1, 0, 1});
        tiles.add(new int[] {1, 1, 1, 1, 1, 0, 1, 0});
        tiles.add(new int[] {1, 1, 1, 1, 1, 1, 1, 1});
        tiles.add(new int[] {0, 0, 0, 0});
        tiles.add(new int[] {2});
        for (int i = 1; i < tiles.size() + 1; i += 1) {
            String key = map.wallChecker.concatenateCode(tiles.get(i - 1));
            int[] value = new int[] {i % tw, i / tw};
            System.out.println(methods.tuple("tile", key, Arrays.toString(value)));
            tileMap.put(key, value);
        }

        //Do the same for the door tiles.
        doors.add(new int[] {1, 0, 2});
        doors.add(new int[] {1, 2, 2});
        doors.add(new int[] {2, 1, 2});
        doors.add(new int[] {0, 1, 2});
        doors.add(new int[] {1, 0, 1});
        doors.add(new int[] {1, 2, 1});
        doors.add(new int[] {2, 1, 1});
        doors.add(new int[] {0, 1, 1});
        doors.add(new int[] {1, 0, 0});
        doors.add(new int[] {1, 2, 0});
        doors.add(new int[] {2, 1, 0});
        doors.add(new int[] {0, 1, 0});
        for (int i = 0; i < doors.size(); i += 1) {
            int[] value = new int[] {i % tw, i / tw};
            String key = map.wallChecker.concatenateCode(doors.get(i));
            System.out.println(methods.tuple("door", key, Arrays.toString(value)));
            doorMap.put(key, value);
        }
    }

    //Draw the default square boxes for the walls if no tiles are available.
    public void drawWall(Graphics g, int tileX, int tileY, int screenX, int screenY, int screenTileSize) {
        if (map.isWall(tileX, tileY)) {
            g.setColor(BLACK);
        }
        else {
            g.setColor(WHITE);
        }
        g.fillRect(screenX - 1, screenY, screenTileSize + 2, screenTileSize);
    }

    //Draw the repeating pattern for the room's floor.
    public void drawRoomFloor(Graphics g, int tileX, int tileY, int screenX, int screenY, int screenTileSize) {
        g.drawImage(tileSheet, screenX - 1, screenY, screenX + screenTileSize + 1, screenY + screenTileSize, 0, 0, tileSize, tileSize, null);
    }

    //Draw the room's wall tiles using the map's wallChecker's autotiling functionality.
    public void drawRoomTiles(Graphics g, int tileX, int tileY, int screenX, int screenY, int screenTileSize) {
        if (tileSheet != null) {
            //Top, Bottom, Left, Right, Top-Left, Top-Right, Bottom-Left, Bottom-Right
            int[] tilePos = tileMap.get(map.wallChecker.getAdjacentTiles(map.map, tileX, tileY));
            if (tilePos != null) {
                int sheetX = tilePos[0] * tileSize;
                int sheetY = tilePos[1] * tileSize;
                //System.out.println(methods.tuple(sheetX, sheetY));
                g.drawImage(tileSheet, screenX - 1, screenY, screenX + screenTileSize + 1, screenY + screenTileSize, sheetX, sheetY, sheetX + tileSize, sheetY + tileSize, null);
            }
            else {
                if (map.isWall(tileX, tileY)) {
                    drawWall(g, tileX, tileY, screenX, screenY, screenTileSize);
                }
            }
        }
        else {
            drawWall(g, tileX, tileY, screenX, screenY, screenTileSize);
        }
    }

    //Draw the floor's entrances and exits on a wall face next to the start and end squares.
    public void drawRoomDoors(Graphics g, int tileX, int tileY, int screenX, int screenY, int screenTileSize) {
        if (tileSheet != null) {
            try {
                int type = -1;
                int[] dir = {0, 0};
                switch (map.map[tileY][tileX]) {
                    case 3:
                        if (map.floor == 0) {
                            type = 2;
                        }
                        else {
                            type = 1;
                        }
                        dir = map.startDir;
                        break;
                    case 4:
                        type = 0;
                        dir = map.exitDir;
                        break;
                }
                if (type > -1) {
                    String key = map.wallChecker.concatenateCode(new int[]{dir[0] + 1, dir[1] + 1, type});
                    int[] wall = doorMap.get(key);
                    int screenTileX = (dir[0] * screenTileSize);
                    int screenTileY = (dir[1] * screenTileSize);
                    g.drawImage(tileSheet, screenX + screenTileX - 1, screenY + screenTileY, screenX + screenTileX + screenTileSize + 1, screenY + screenTileY + screenTileSize, wall[0] * tileSize, 160 + (wall[1] * tileSize), (wall[0] * tileSize) + tileSize, 160 + (wall[1] * tileSize) + tileSize, null);
                }
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
    }
}
