package com.System.Maps;

import com.Entities.Enemy;
import com.Entities.Entity;
import com.Entities.Player;
import com.GUI.Camera;
import com.GUI.GUI;
import com.System.Background;
import com.System.EnemyBattlers;
import com.System.Enums;
import com.System.WallChecker;
import com.methods;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Map {
    public GUI frame;                                                   //The frame the map is assigned to.
    public String name;                                                 //The map's name.
    public int[][] map;                                                 //The map's terrain layout.
    public boolean[][] visited;                                         //Where the player has visited.
    public Player player;                                               //The map's player.
    public ArrayList<Enemy> enemyList;                                  //The list of all of the map's enemies.
    public HashSet<Entity> entityList;                                  //The list of all of the map's entities.
    public Background background;                                       //The map's tileset background.
    public Camera camera;                                               //The map's camera.
    public Object[][] enemySpawns = new Object[0][0];                   //The map's spawn pool.
    public Object[][] staticEnemySpawns = new Object[0][0];             //The enemies that will be spawned when the set map initializes.
    public int[][] staticEnemyLocations;                                //The set map's enemy locations.
    public int monsterExperience = 0;                                   //The amount of experience the player has earned battling monsters on this map.
    public int floor = 0;                                               //The floor of the map.
    public int w;                                                       //The map's dimensions.
    public int h;
    public int startX;                                                  //The map's entrance location and facing direction.
    public int startY;
    public int[] startDir;
    public int exitX;                                                   //The map's exit location and facing direction.
    public int exitY;
    public int[] exitDir;
    public Enums.mapLayout layout = Enums.mapLayout.STATIC;             //Whether a map is randomly generated or static.
    public int timeThreshold = -1;                                      //How much time a player is given to complete the floor.
    public ArrayList<int[]> corners;                                    //All of the map's corner locations.
    public WallChecker wallChecker = new WallChecker(this);      //The map's wallChecker that determines terrain autotiling.

    //Map constructor.
    public Map() {
    }
    public Map(int floor, String[] map) {
        if (map != null) {
            assignSetMap(map);
        }
        this.floor = floor;
        setMapAttributes();
    }
    public Map(int floor, int startX, int startY, String[] map) {
        if (map != null) {
            assignSetMap(map);
        }
        this.floor = floor;
        this.startX = startX;
        this.startY = startY;
        setMapAttributes();
    }
    public Map(int floor, int startX, int startY, String[] map, Player player, ArrayList<Enemy> enemyList, String background) {
        this(floor, startX, startY, map);
        setPlayer(player);
        setBackground(background);
        this.enemyList = enemyList;
        alignLists();
    }
    public Map(int floor, String[] map, Player player, ArrayList<Enemy> enemyList, String background) {
        this(floor, map);
        setPlayer(player);
        setBackground(background);
        this.enemyList = enemyList;
        alignLists();
    }

    //Fill in tiles on the minimap as the player encounters them.
    public void setVisited() {
        visited = new boolean[h][w];
        System.out.println(methods.tuple("visited", w, h));
        for (int y = 0; y < h; y += 1) {
            for (int x = 0; x < w; x += 1) {
                visited[y][x] = false;
            }
        }
    }

    //Set the map's frame.
    public void setFrame(GUI frame) {
        this.frame = frame;
    }

    //Set the map's name.
    public void setName(String name) {
        this.name = name;
    }

    //Set the map's enemy spawn pool.
    public void setEnemySpawns(Object ... enemy) {
        if (enemy != null) {
            enemySpawns = new Object[enemy.length / 2][2];
            for (int i = 0; i < enemy.length / 2; i += 1) {
                enemySpawns[i] = new Object[]{enemy[i * 2], enemy[(i * 2) + 1]};
            }
        } else {
            enemySpawns = new Object[0][0];
        }
    }

    public void setEnemyLocations(Object ... enemy) {
        if (enemy != null) {
            staticEnemySpawns = new Object[enemy.length / 4][2];
            staticEnemyLocations = new int[enemy.length / 4][2];
            for (int i = 0; i < enemy.length / 4; i += 1) {
                //System.out.println(methods.tuple(enemy[i * 4], enemy[(i * 4) + 1], enemy[(i * 4) + 2], enemy[(i * 4) + 3]));
                staticEnemySpawns[i] = new Object[] { enemy[i * 4], enemy[(i * 4) + 1] };
                staticEnemyLocations[i] = new int[] { (int) enemy[(i * 4) + 2], (int) enemy[(i * 4) + 3] };
            }
        } else {
            staticEnemySpawns = new Object[0][0];
            staticEnemyLocations = new int[0][0];
        }
    }

    //Set the map's background to null or an actual tilesheet.
    public void setBackground() {
        setBackground("");
    }
    public void setBackground(String background) {
        this.background = new Background(background, this);
    }

    //Set the map's player and align the entity lists to match.
    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            player.setBoundingBox();
        }
        alignLists(true, true);
    }

    public void setMap(int[][] map) {
        this.map = map;
        display();
        w = this.map[0].length;
        h = this.map.length;
        setVisited();
    }
    //Set the map's layout and cloud the area in a fog of war.
    public void assignSetMap(String[] map) {
        setMap(parseMap(map));
    }

    //Parse the inputted character map to generate the floor layout.
    public int[][] parseMap(String[] map) {
        int[][] parsedMap = new int[map.length][(map[0].length() + 1) / 2];
        System.out.println(map[0].length() + " " + ((map[0].length() + 1) / 2));
        for (int i = 0; i < parsedMap.length; i += 1) {
            for (int j = 0; j < parsedMap[i].length; j += 1) {
                parsedMap[i][j] = 0;
                char tile = map[i].charAt(j * 2);
                switch (tile) {
                    case '#': parsedMap[i][j] = 1; break;
                    case '-': parsedMap[i][j] = 5; break;
                }
                if (j > 0 && j < parsedMap[i].length - 1) {
                    char subtile = map[i].charAt(j * 2 - 1);
                    switch (subtile) {
                        case 'N': parsedMap[i][j] = 3; startDir = methods.checkArrowDirection(tile); break;
                        case 'X': parsedMap[i][j] = 4; exitDir = methods.checkArrowDirection(tile); break;
                    }
                }
            }
        }
        return parsedMap;
    }

    //Set the map's floor.
    public void setFloor(int floor) {
        this.floor = floor;
    }

    //Set the map's entity lists and backgrounds.
    public void setMapAttributes() {
        this.enemyList = new ArrayList<>();
        this.entityList = new HashSet<>();
        setBackground();
    }

    //Check whether an entity exists at a particular location on the map.
    public Entity checkEntityPositions(int x, int y) {
        for (Entity entity : entityList) {
            if (entity != null) {
                if (entity.x == x && entity.y == y) {
                    return entity;
                }
            }
        }
        return null;
    }

    //Add an enemy to the map and align the entity lists to match.
    public void addEnemy(String name, int level, int x, int y) {
        Class<?> enemyClass = EnemyBattlers.battlers.get(name);
        System.out.println("[DBG]: " + enemyClass + " " + enemyClass.getConstructors()[0]);
        try {
            Enemy enemy = (Enemy) enemyClass.getConstructors()[0].newInstance(this);
            System.out.println("[DBG]: Enemy " + enemy);
            enemy.increaseLevel(level - enemy.level);
            enemy.teleport(x, y);
            enemyList.add(enemy);
            System.out.println("[DBG]: Enemy created successfully.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        alignLists();
    }

    //Add a player to the map and align the entity lists to match, sending the player to the entrance if appropriate.
    public void addPlayer(Player player, boolean resetPlayer) {
        entityList.add(player);
        if (resetPlayer) {
            this.player.map = this;
            this.player.teleport(startX, startY);
            player.setDirection(-startDir[0], -startDir[1]);
        }
    }

    //Align the entity lists after clearing them as appropriate.
    public void alignLists() {
        alignLists(false);
    }
    public void alignLists(boolean clear) {
        alignLists(clear, false);
    }
    public void alignLists(boolean clear, boolean resetPlayer) {
        if (clear) {
            this.entityList.clear();
            if (player != null) {
                addPlayer(player, resetPlayer);
            }
        }
        for (Enemy enemy : this.enemyList) {
            enemy.setBoundingBox();
            this.entityList.add(enemy);
        }
    }

    //Check if a certain coordinate is a wall.
    public boolean isWall(int x, int y) {
        return isWall(map, x, y);
    }
    public boolean isWall(int[][] map, int x, int y) {
        try {
            if (map[y][x] == 1) {
                return true;
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }
    public boolean isWall(int x1, int y1, int x2, int y2) {
        for (int y = y1; y < y2 + 1; y += 1) {
            for (int x = x1; x < x2 + 1; x += 1) {
                if (!isWall(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    //Check if a certain coordinate is water.
    public boolean isWater(int x, int y) {
        try {
            if (map[y][x] == 5) {
                return true;
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    //Return the coordinates of a random point a certain distance away from a coordinate on the map. This is done my picking a random point until it
    //finds one that is far enough away. If it can't find ine within reasonable time, it will abandon the pursuit.
    public int[] assignRandomFarPoint(int value, int x, int y, int startX, int startY, int minDist, boolean checkWalls, boolean silent, int terminateAfter) {
        return assignRandomFarPoint(map, value, x, y, startX, startY, minDist, checkWalls, silent, terminateAfter);
    }
    public int[] assignRandomFarPoint(int[][] map, int value, int x, int y, int startX, int startY, int minDist, boolean checkWalls, boolean silent, int terminateAfter) {
        System.out.println(map);
        if (map != null) {
            int i = 0;
            double dist = methods.distance(x, y, startX, startY);
            int[] dir = new int[2];
            if (dist < minDist || isWall(map, x, y)) {
                boolean found = false;
                while ((!found && i < terminateAfter)) {
                    x = methods.randomRange(1, map[0].length - 2);
                    y = methods.randomRange(1, map.length - 2);
                    dist = methods.distance(x, y, startX, startY);
                    Object[] point = assignPoint(map, value, x, y, dist, dist >= minDist, checkWalls, i, silent);
                    x = (int) point[0];
                    y = (int) point[1];
                    int valid;
                    valid = (int) point[2];
                    switch (valid) {
                        case 0:
                            found = false;
                            break;
                        case 1:
                            found = true;
                            break;
                    }
                    if (checkWalls && found) {
                        dir[0] = (int) point[3];
                        dir[1] = (int) point[4];
                    }
                    i += 1;
                }
            } else {
                Object[] point = assignPoint(map, value, x, y, dist, dist >= minDist, checkWalls, i, silent);
                x = (int) point[0];
                y = (int) point[1];
                if (checkWalls) {
                    dir[0] = (int) point[3];
                    dir[1] = (int) point[4];
                }
            }
            if (i < terminateAfter) {
                if (checkWalls) {
                    return new int[]{x, y, dir[0], dir[1]};
                } else {
                    return new int[]{x, y};
                }
            } else {
                System.out.println("Too many attempts. Reattempting map generation...");
                return null;
            }
        }
        return null;
    }

    //Return the coordinates of a random point on the map.
    public Object[] assignPoint(int[][] map, int value, int x, int y, double dist, boolean condition, boolean checkWalls, int attempt, boolean silent) {
        int found = 0;
        if (!silent) {
            System.out.print((attempt + 1) + ": " + methods.tuple(x, y, dist, condition, !isWall(map, x, y), !corners.contains(new int[]{x, y})));
        }
        if (condition && !(isWall(map, x, y) || corners.contains(new int[]{x, y}))) {
            if (checkWalls) {
                int[] wallNumber = wallChecker.getAdjacentTotal(map, x, y);
                if (!(wallNumber[0] == 0 || wallNumber[0] == 4)) {
                    map[y][x] = value;
                    String[] wallCode = wallChecker.getAdjacentTiles(map, x, y).split("");
                    if (!silent) {
                        System.out.print(" " + methods.tuple(Arrays.toString(wallNumber), Arrays.toString(wallCode)));
                    }
                    ArrayList<String> directions = new ArrayList<>();
                    directions.add("0 -1");
                    directions.add("0 1");
                    directions.add("-1 0");
                    directions.add("1 0");
                    wallCode = Arrays.copyOf(wallCode, 4);
                    int i = 0;
                    for (String wall : wallCode) {
                        if (wall.equals("0")) {
                            directions.set(i, "null");
                        }
                        i += 1;
                    }
                    for (int ii = 0; ii < i; ii += 1) {
                        directions.remove("null");
                    }
                    String[] dir = directions.get(methods.randomRange(0, directions.size() - 1)).split(" ");
                    if (!silent) {
                        System.out.print(" ... Found " + Arrays.toString(directions.toArray()) + ".");
                        System.out.println();
                    }
                    found = 1;
                    System.out.println();
                    return new Object[]{x, y, found, Integer.parseInt(dir[0]), Integer.parseInt(dir[1])};
                }
            } else {
                map[y][x] = value;
                if (!silent) {
                    System.out.print(" ... Found.");
                    System.out.println();
                }
                found = 1;
                return new Object[]{x, y, found};
            }
        }
        if (!silent) {
            System.out.println();
        }
        return new Object[]{x, y, found, 0, 0};
    }

    //Create a new map filled completely with wall tiles. Useful for obtaining a fresh map to start or restart generation.
    public int[][] createMap(int size) {
        int[][] map = new int[size][size];
        corners = new ArrayList<>();
        for (int y = 0; y < map.length; y += 1) {
            for (int x = 0; x < map[y].length; x += 1) {
                map[y][x] = 1;
            }
        }
        return map;
    }

    //Obtain random coordinates somewhere on the map.
    public int[] randomiseCoordinates(int[][] map, int offset) {
        int x = methods.randomRange(offset + 1, map[0].length - offset - 2);
        int y = methods.randomRange(offset + 1, map.length - offset - 2);
        System.out.println(methods.tuple(x, y));
        return new int[]{x, y};
    }

    //Shift the direction of tunnel generation either clockwise or anti-clockwise.
    public int[] turnCorner(int[] dir) {
        int[] newDir = dir;
        while (Arrays.equals(newDir, dir) || Arrays.equals(newDir, new int[]{-dir[0], -dir[1]})) {
            switch ((int) (Math.random() * 4)) {
                case 0:
                    newDir = new int[]{-1, 0};
                    break;
                case 1:
                    newDir = new int[]{1, 0};
                    break;
                case 2:
                    newDir = new int[]{0, -1};
                    break;
                case 3:
                    newDir = new int[]{0, 1};
                    break;
            }
        }
        return newDir;
    }

    //Use assignRandomFarPoint to find suitable entrance and exit locations for this map. If it can't find any in reasonable time, it will return
    //with failure.
    public boolean createDoors(int[][] map, int x, int y, int size) {
        int[] entrance = assignRandomFarPoint(map,3, x, y, startX, startY, (size / 2), true, false, 30);
        if (entrance != null) {
            startX = entrance[0];
            startY = entrance[1];
            startDir = new int[]{entrance[2], entrance[3]};
            int[] exit = assignRandomFarPoint(map,4, x, y, startX, startY, (size / 2), true, false, 30);
            if (exit != null) {
                exitX = exit[0];
                exitY = exit[1];
                exitDir = new int[]{exit[2], exit[3]};
                System.out.println(methods.tuple("start", startX, startY, startDir[0], startDir[1]));
                System.out.println(methods.tuple("exit", exitX, exitY, exitDir[0], exitDir[1]));
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    //Spawn a number of initial enemies at random points on the map when the player first spawns in.
    public void spawnInitialEnemies(int n, int radius, int enemyCap) {
        System.out.println(methods.tuple(n, enemySpawns.length, staticEnemySpawns.length));
        if (enemySpawns.length > 0 || staticEnemySpawns.length > 0) {
            for (int i = 0; i < n; i += 1) {
                Object[] enemy;
                int[] spawnPoint;
                if (enemySpawns.length > 0) {
                    enemy = enemySpawns[methods.randomRange(0, enemySpawns.length - 1)];
                    spawnPoint = assignRandomFarPoint(0, 0, 0, player.x, player.y, radius, false, true, 15);
                }
                else {
                    enemy = staticEnemySpawns[i];
                    spawnPoint = staticEnemyLocations[i];
                }
                if (spawnPoint != null) {
                    spawnEnemy((String) enemy[0], (int) enemy[1], spawnPoint[0], spawnPoint[1], enemyCap);
                }
            }
        }
    }

    //Spawn a single enemy somewhere on the map.
    public void spawnEnemy(String enemy, int level, int x, int y, int entityCap) {
        if (entityList.size() < entityCap) {
                addEnemy(enemy, level, x, y);
                System.out.println("[DBG]: Spawned enemy at " + methods.tuple(x, y) + ".");
                System.out.println();
        }
    }

    //Print the map's layout to the terminal.
    public void display() {
        System.out.println("Map : " + this);
        for (int y = 0; y < map.length; y += 1) {
            for (int x = 0; x < map[y].length; x += 1) {
                char icon = ' ';
                switch (map[y][x]) {
                    case 0:
                        icon = ' ';
                        break;      // floor
                    case 1:
                        icon = '#';
                        break;      // wall
                    case 2:
                        icon = '?';
                        break;      // spawning pit
                    case 3:
                        icon = 'N';
                        break;      // entrance
                    case 4:
                        icon = 'X';
                        break;      // exit
                    case 5:
                        icon = '-';
                        break;      // water
                }
                if (player != null) {
                    if (x == player.x && y == player.y) {
                        icon = 'P';
                    }
                }
                System.out.print(icon + " ");
            }
            System.out.println();
        }
    }

    //Draw the minimap, fitting the entire map into a container box of static dimensions. Visited areas will fill in on the map, whilst unknown areas
    //will remain blank.
    public void drawMinimap(Graphics g, int x, int y, int w, int h) {
        int tw = w / map[0].length;
        int th = h / map.length;
        Color colour;
        methods.drawContainerBox(g, x, y, w, h);
        for (int my = 0; my < map.length; my += 1) {
            for (int mx = 0; mx < map[my].length; mx += 1) {
                colour = null;
                if (visited[my][mx]) {
                    switch (map[my][mx]) {
                        case 0:
                            colour = Color.WHITE;
                            break;
                        case 1:
                            colour = new Color(55, 55, 55);
                            break;
                        case 3:
                            colour = new Color(205, 205, 155, 127);
                            break;
                        case 4:
                            colour = new Color(55, 55, 0, 127);
                            break;
                        case 5:
                            colour = new Color(55, 105, 205, 205);
                            break;
                    }
                    if (player != null) {
                        if (mx == player.x && my == player.y) {
                            colour = Color.GREEN;
                        }
                    }
                    for (Enemy enemy : enemyList) {
                        if (mx == enemy.x && my == enemy.y) {
                            colour = Color.ORANGE;
                            break;
                        }
                    }
                    if (colour != null) {
                        g.setColor(colour);
                        g.fillRect(x + (tw * mx), y + (th * my), tw, th);
                    }
                }
            }
        }
    }
}