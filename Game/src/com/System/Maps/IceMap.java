package com.System.Maps;

import com.Entities.Enemy;
import com.Entities.Player;
import com.System.Enums;
import com.methods;

import java.util.ArrayList;

public class IceMap extends Map {
    ArrayList<int[]> circles = new ArrayList<>();           //Contains a list of all the circular rooms.
    ArrayList<int[]> pools = new ArrayList<>();             //Contains a list of all of the pools within rooms.

    //IceMap constructor. When generating a map, set its size and generation characteristics.
    public IceMap(int[][] map) {
        if (map != null) {
            setMap(map);
        }
        layout = Enums.mapLayout.RANDOMLY_GENERATED;
        setMapAttributes();
    }
    public IceMap(int[][] map, Player player, ArrayList<Enemy> enemyList, String background) {
        this(map);
        setPlayer(player);
        setBackground(background);
        this.enemyList = enemyList;
        alignLists();
    }
    public IceMap(int floor, int size, int minRadius, int maxRadius, int minCorridors, int maxCorridors) {
        this(null);
        this.floor = floor;
        setMap(generateMap(floor, size, minRadius, maxRadius, minCorridors, maxCorridors));
    }
    public IceMap(int floor, int size, int minRadius, int maxRadius, int minCorridors, int maxCorridors, Player player, ArrayList<Enemy> enemyList, String background) {
        this(null, player, enemyList, background);
        setMap(generateMap(floor, size, minRadius, maxRadius, minCorridors, maxCorridors));
        this.enemyList = enemyList;
        alignLists();
    }

    //Create a circle on the map and fill it with a terrain value.
    public int[] createCircle(int value, int minRadius, int maxRadius) {
        int circleRadius = methods.randomRange(minRadius, maxRadius);
        return createCircle(value, methods.randomRange(circleRadius + 1, map[0].length - circleRadius - 2), methods.randomRange(circleRadius + 1, map.length - circleRadius - 2), circleRadius);
    }
    public int[] createCircle(int value, int x, int y, int minRadius, int maxRadius) {
        int circleRadius = methods.randomRange(minRadius, maxRadius);
        return createCircle(value, x, y, circleRadius);
    }
    public int[] createCircle(int value, int x, int y, int radius) {
        return methods.createCircle(map, value, x, y, radius);
    }

    //Generate a map by creating a series of randomly sized circles and putting pools in the centers of some of them. (WIP)
    public int[][] generateMap(int floor, int size, int minRadius, int maxRadius, int minCircles, int maxCircles) {
        int r = methods.randomRange((minRadius + maxRadius) / 2, maxRadius);
        int[][] map = createMap(size);
        int[] coord = randomiseCoordinates(map, r + 1);
        int x = coord[0];
        int y = coord[1];
        circles.add(createCircle(0, x, y, r));
        startX = x;
        startY = y;
        System.out.println(methods.tuple(minCircles, maxCircles));
        int numCircles = methods.randomRange(minCircles, maxCircles - 1);
        int numPools = methods.randomRange(2, maxCircles - 2);
        ArrayList<Integer> pools = new ArrayList<>();
        for (int i = 0; i < numPools; i += 1) {
            int pool = methods.randomRange(0, numCircles - 1);
            if (!pools.contains(pool)) {
                pools.add(pool);
            }
        }
        for (int cell = 0; cell < numCircles; cell += 1) {
            int[] circle = null;
            System.out.print("Creating circle... ");
            while (circle == null) {
                circle = createCircle(0, minRadius, maxRadius);
                if (circle != null) {
                    x = circle[0];
                    y = circle[1];
                    r = circle[2];
                    System.out.print(" Found " + methods.tuple(x, y, r) + ".");
                    System.out.println();
                }
            }
            circles.add(circle);
            if (pools.indexOf(cell) > -1) {
                int[] pool = createCircle(5, x, y, Math.max(minRadius - 1, r / 2), r);
                this.pools.add(pool);
            }
        }
        createDoors(map, x, y, size);
        return map;
    }
}
