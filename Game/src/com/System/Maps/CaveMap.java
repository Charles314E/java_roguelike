package com.System.Maps;

import com.Entities.Enemy;
import com.Entities.Player;
import com.System.Enums;
import com.methods;

import java.util.ArrayList;
import java.util.Arrays;

public class CaveMap extends Map {
    //CaveMap constructor. When generating a map, set its size and generation characteristics.
    public CaveMap(int[][] map) {
        if (map != null) {
            setMap(map);
        }
        layout = Enums.mapLayout.RANDOMLY_GENERATED;
        setMapAttributes();
    }
    public CaveMap(int[][] map, Player player, ArrayList<Enemy> enemyList, String background) {
        this(map);
        setPlayer(player);
        setBackground(background);
        this.enemyList = enemyList;
        alignLists();
    }
    public CaveMap(int floor, int size, int maxLength, int minCorridors, int maxCorridors) {
        this(null);
        setFloor(floor);
        attemptMapGeneration(size, maxLength, minCorridors, maxCorridors);
    }
    public CaveMap(int floor, int size, int maxLength, int minCorridors, int maxCorridors, Player player, ArrayList<Enemy> enemyList, String background) {
        this(null, player, enemyList, background);
        setFloor(floor);
        attemptMapGeneration(size, maxLength, minCorridors, maxCorridors);
        this.enemyList = enemyList;
        alignLists();
    }

    //If a map cannot be generated within adequate time, create a new one until a reasonable map is found.
    public void attemptMapGeneration(int size, int maxLength, int minCorridors, int maxCorridors) {
        int[][] map = generateMap(size, maxLength, minCorridors, maxCorridors);
        while (map == null) {
            map = generateMap(size, maxLength, minCorridors, maxCorridors);
        }
        setMap(map);
    }

    //Turn a corner in the generation.
    public int[] turnCorner(int x, int y, int[] dir) {
        corners.add(new int[] {x, y});
        return turnCorner(dir);
    }

    //Generate a random tunnel path, with randomised tunnel lengths and directions. If a tunnel threatens to go off the edge of the map, veer it off
    //to the side to prevent an error. Once enough tunnels have been created, the generator attempts to find an extrance and exit location for the
    //player to spawn at and find respectively. If one isn't found, abandon this generated map.
    public int[][] generateMap(int size, int maxLength, int minCorridors, int maxCorridors) {
        int[][] map = createMap(size);
        int[] coord = randomiseCoordinates(map, 1);
        corners = new ArrayList<>();
        int x = coord[0];
        int y = coord[1];
        System.out.println(methods.tuple(minCorridors, maxCorridors));
        int numCorridors = methods.randomRange(minCorridors, maxCorridors);
        int[] dir = turnCorner(new int[] {0, 0});
        startX = x;
        startY = y;
        startDir = Arrays.copyOf(dir, 2);
        for (int corridor = 0; corridor < numCorridors; corridor += 1) {
            int tunnelLength = methods.randomRange(3, maxLength);
            System.out.println(methods.tuple(numCorridors, tunnelLength) + methods.tuple(dir[0], dir[1]));
            for (int length = 0; length < tunnelLength; length += 1) {
                boolean legalTunnel = false;
                while (!legalTunnel) {
                    try {
                        int i = map[y + (dir[1] * 2)][x + (dir[0] * 2)];
                        if (i > 1) {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                        legalTunnel = true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        dir = turnCorner(x, y, dir);
                        System.out.println("Turn Corner: " + methods.tuple(dir[0], dir[1]));
                    }
                }
                map[y][x] = 0;
                x += dir[0];
                y += dir[1];
            }
            dir = turnCorner(x, y, dir);
        }
        if (!createDoors(map, x, y, size)) {
            return null;
        }
        return map;
    }
}
