package com.GUI;

import com.Entities.*;
import com.Step.GameTimer;
import com.Step.MoveEnemy;
import com.Step.MovePlayer;
import com.Step.SpawnEnemy;
import com.System.*;
import com.System.InputMethods.KeyPressed;
import com.System.Maps.CaveMap;
import com.System.Maps.IceMap;
import com.System.Maps.Map;
import com.System.Text.FloatingText;
import com.System.Text.TextStore;
import com.methods;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends JFrame {
    public String title;                                                    //The game's title, shown on the top bar of the window.
    public int w;                                                           //The dimensions of the screen and camera.
    public int h;
    public int wCam;
    public int hCam;
    public int entityCap;                                                   //How many entities can be in a single room at a time.
    public Player player;                                                   //The game's player.
    public HUD hud;                                                         //The game's HUD.
    public Camera camera;                                                   //The game's camera.
    public ArrayList<Map> rooms;                                            //A list of all of the game's rooms.
    public ArrayList<Transition> transitions;                               //A list of all of the game's transitions.
    public LevelTransition roomTransition;                                  //The current transition between rooms. Is usually null unless warping.
    public Timer movePlayer;                                                //Timers regarding game flow. One game tick is 0.1 seconds.
    public Timer moveEnemy;
    public Timer spawnEnemy;
    public Timer timePlayer;
    public HashMap<String, TextStore> textStores = new HashMap<>();         //A map storing all of the game's floating text.

    //The GUI constructor. Handles the initial setup of the game, creating fonts, enemy definitions, and setting the window's characteristics.
    public GUI(String title, int width, int height, int cameraWidth, int cameraHeight) {
        textStores.put("damage", new TextStore());
        EnemyBattlers.compileEnemyDictionary();
        Fonts.addFont("stonehen.ttf", Fonts.font.TEXT, 30);
        Fonts.addFont("partybusiness.ttf", Fonts.font.DAMAGE, 20);
        Fonts.addFont("DUNGRG__.TTF", Fonts.font.TITLE, 120);
        Fonts.addFont("DUNGRG__.TTF", Fonts.font.SUBTITLE, 60);
        this.title = title;
        w = width;
        h = height;
        wCam = cameraWidth;
        hCam = cameraHeight;
        rooms = new ArrayList<>();
        transitions = new ArrayList<>();
        entityCap = 16;
        player = new Player("Jonas", 1);

        int level = 1;
        for (int i = 0; i < 5; i += 1) {
            ArrayList<Object> enemySpawns = new ArrayList<>();
            enemySpawns.add("Slime"); enemySpawns.add(1 + (int) (i * 0.5));
            enemySpawns.add("Bat"); enemySpawns.add(1 + (int) (i * 0.5));
            int size = 40 + (i * 5);
            createCaveRoom("Gloom Caverns", level, size * 3, size, 15 + (i * 5), 25 + (i * 7), 40 + (i * 10), "Cave", enemySpawns.toArray());
            level += 1;
        }
        createSetRoom("Boss Fight", 0, -1, 6, 7, new String[] {
                        "# # # # # # # # # # # # #",
                        "# # - - - - - - - - - # #",
                        "# - - -           - - - #",
                        "#                       #",
                        "#                       #",
                        "#                       #",
                        "# #                   # #",
                        "# # # # # #Nv # # # # # #",
                        "# # # # # # # # # # # # #"},
                "Cave", "Slime", 5, 6, 3, "Bat", 1, 1, 2, "Bat", 1, 11, 2);

        hud = new HUD(w, h, player);
        camera = new Camera(this, wCam, hCam, rooms.get(0), hud);

        this.addKeyListener(new KeyPressed(player));
        w = camera.sw;
        h = camera.sh;
        add(camera, BorderLayout.CENTER);

        //Set the window size to the class attributes w and h.
        setSize(w, h);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(title);

        repaint();
        movePlayer = new Timer(100, new MovePlayer(this, player));
        movePlayer.start();
        unlinkRoom();
        startRoomSwitch(rooms.get(0), true);
    }

    //Create a new room, setting its attributes, placing the player within it and spawning its enemies.
    public void createRoom(String name, String background, int time, int spawnType, Object ... enemySpawns) {
        Map room = rooms.get(rooms.size() - 1);
        room.setFrame(this);
        room.setPlayer(player);
        room.setName(name);
        room.setBackground(background);
        room.timeThreshold = time;
        int n = 0;
        switch (spawnType) {
            case 0: room.setEnemySpawns(enemySpawns); n = entityCap / 2; break;
            case 1: room.setEnemyLocations(enemySpawns); n = enemySpawns.length / 4; break;
        }
        room.display();
        room.spawnInitialEnemies(n, Math.max(wCam, hCam) * 2, entityCap);
    }

    //Create a room with a predetermined map.
    public void createSetRoom(String name, int floor, int time, int x, int y, String[] map, String background, Object ... setSpawns) {
        rooms.add(new Map(floor, x, y, map));
        createRoom(name, background, time, 1, setSpawns);
    }

    //Create a room with tunnel-like generation.
    public void createCaveRoom(String name, int floor, int time, int size, int maxLength, int minCorridors, int maxCorridors, String background, Object ... enemySpawns) {
        rooms.add(new CaveMap(floor, size, maxLength, minCorridors, maxCorridors));
        createRoom(name, background, time, 0, enemySpawns);
    }

    //Create a room with circular, open generation (WIP).
    public void createIceRoom(String name, int floor, int time, int size, int minRadius, int maxRadius, int minCircles, int maxCircles, String background, Object ... enemySpawns) {
        rooms.add(new IceMap(floor, size, minRadius, maxRadius, minCircles, maxCircles));
        createRoom(name, background, time, 0, enemySpawns);
    }

    //Stop all of the game timers to pause game flow.
    public void stopTimers() {
        if (timePlayer != null) {
            moveEnemy.stop();
            spawnEnemy.stop();
            timePlayer.stop();
        }
    }

    //Prepare the room to be switched, pausing game flow and breaking the necessary map links. Transition should only be set to true if you wish to
    //move to a new room. Otherwise, use unlinkRoom() if you don't want to pause game flow.
    public void startRoomSwitch(Map room, boolean transition) {
        if (room != null) {
            room.display();
        }
        stopTimers();
        if (transition) {
            int monsterExperience = 0;
            if (player.map != null) {
                monsterExperience = player.map.monsterExperience;
            }
            roomTransition = new LevelTransition(this, room, monsterExperience, player.map != null);
            repaint();
        }
        else {
            unlinkRoom();
            if (room != null) {
                linkRoom(room);
                finishRoomSwitch(room);
            }
        }
    }

    //Breaks all linkages between a room and transient objects that can leave it.
    public void unlinkRoom() {
        for (Map r : rooms) {
            r.player = null;
            r.alignLists(true);
            r.camera = null;
        }
        player.setMap(null);
        if (camera != null) {
            camera.setMap(null);
        }
    }

    //Create new links between the player and camera to a new map.
    public void linkRoom(Map room) {
        room.player = player;
        room.alignLists(true, true);
        camera.setMap(room);
        if (room.exitX > 0 && room.exitY > 0) {
            room.map[room.exitY][room.exitX] = 4;
        }
        System.out.println(methods.tuple("start", room.startX, room.startY, room.startDir[0], room.startDir[1]));
        player.setMap(room);
        player.teleport(room.startX, room.startY);
        player.setDirection(-room.startDir[0], -room.startDir[1]);
        System.out.println(methods.tuple("player", player.x, player.y, player.xDir, player.yDir));
    }

    //Close up a room transition, restarting the game flow and allocating a new time limit for the floor.
    public void finishRoomSwitch(Map room) {
        int allocatedTime = roomTransition.timeThreshold;
        System.out.println(methods.tuple("time", allocatedTime));
        roomTransition = null;
        moveEnemy = new Timer(100, new MoveEnemy(player, room.enemyList));
        moveEnemy.start();
        spawnEnemy = new Timer(10000, new SpawnEnemy(room, entityCap,Math.max(wCam, hCam) * 2));
        spawnEnemy.start();
        timePlayer = new Timer(25, new GameTimer(this, player, allocatedTime));
        timePlayer.start();
    }

    //Align text using its width in pixels. Justifications include LEFT, MIDDLE and RIGHT.
    public void alignFloatingText(Graphics g, FloatingText text, Enums.alignment justification) {
        text.alignmentOffset = getNewTextAlignment(g, text.text, justification);
    }
    public int getNewTextAlignment(Graphics g, String str, Enums.alignment justification) {
        return getNewTextAlignment(g, str, justification, getFont());
    }
    public int getNewTextAlignment(Graphics g, String str, Enums.alignment justification, Fonts.font font) {
        return getNewTextAlignment(g, str, justification, Fonts.fonts.get(font));
    }
    public int getNewTextAlignment(Graphics g, String str, Enums.alignment justification, Font font) {
        FontMetrics fm = g.getFontMetrics(font);
        int fontWidth = fm.stringWidth(str);
        if (justification == Enums.alignment.LEFT) {
            return 0;
        }
        else if (justification == Enums.alignment.MIDDLE) {
            return fontWidth / 2;
        }
        else if (justification == Enums.alignment.RIGHT) {
            return fontWidth;
        }
        return 0;
    }
}