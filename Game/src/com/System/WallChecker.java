package com.System;

import com.System.Maps.Map;

import java.util.Arrays;

public class WallChecker {
    public Map map;                         //What map this wallChecker is assigned to.

    //WallChecker constructor.
    public WallChecker(Map map) {
        this.map = map;
    }

    //Combine a coordinate's wall code into a string.
    public String concatenateCode(int[] array) {
        String code = "";
        for (int i : array) {
            code += i;
        }
        if (code.length() == 4) {
            code += "0000";
        }
        return code;
    }

    //Turn a four digit adjacent code to an eight digit one to allow it to account for diagonals.
    public int[] appendCode(int[] code, int tl, int tr, int bl, int br) {
        int[] newCode = Arrays.copyOf(code, 8);
        newCode[4] = tl;
        newCode[5] = tr;
        newCode[6] = bl;
        newCode[7] = br;
        return newCode;
    }

    //Get the number of wall tiles a coordinate has adjacent to it.
    public int[] getAdjacentTotal(int[][] map, int x, int y) {
        String[] adj = getAdjacentTiles(map, x, y, false, false).split("-");
        return new int[] {Integer.parseInt(adj[0]), Integer.parseInt(adj[1])};
    }

    //Get a coordinate's code, applying a 1 to a direction in which a wall is present, and a 0 to a direction in which it is absent. This code is
    //used to match with a code key in the Backgrounds class to determine what tile should be placed it its coordinate.
    //This function has two modes, one to obtain just the total number of surrounding walls, and another to actually obtain the code.
    public String getAdjacentTiles(int[][] map, int x, int y) {
        return getAdjacentTiles(map, x, y, true, false);
    }
    public String getAdjacentTiles(int[][] map, int x, int y, boolean getCode, boolean cannotRecurse) {
        int top;
        int bottom;
        int left;
        int right;
        int topLeft;
        int topRight;
        int bottomLeft;
        int bottomRight;

        try {
            top = map[y - 1][x];
            if (top > 1) {
                top = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            top = 1;
        }
        try {
            bottom = map[y + 1][x];
            if (bottom > 1) {
                bottom = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            bottom = 1;
        }
        try {
            left = map[y][x - 1];
            if (left > 1) {
                left = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            left = 1;
        }
        try {
            right = map[y][x + 1];
            if (right > 1) {
                right = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            right = 1;
        }

        try {
            topLeft = map[y - 1][x - 1];
            if (topLeft > 1) {
                topLeft = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            topLeft = 1;
        }
        try {
            topRight = map[y - 1][x + 1];
            if (topRight > 1) {
                topRight = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            topRight = 1;
        }
        try {
            bottomLeft = map[y + 1][x - 1];
            if (bottomLeft > 1) {
                bottomLeft = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            bottomLeft = 1;
        }
        try {
            bottomRight = map[y + 1][x + 1];
            if (bottomRight > 1) {
                bottomRight = 0;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            bottomRight = 1;
        }

        int adjacentTotal = top + bottom + left + right;
        int diagonalTotal = topLeft + topRight + bottomLeft + bottomRight;

        if (getCode) {
            int[] code = {top, bottom, left, right, 0};
            int[] diagonalCode = {};
            try {
                if (this.map.isWall(map, x, y)) {
                    code = new int[]{top, bottom, left, right};
                    diagonalCode = new int[] {topLeft, topRight, bottomLeft, bottomRight};
                    if (x == 0) {
                        code = new int[]{top, bottom, 1, right};
                    } else if (x == this.map.w - 1) {
                        code = new int[]{top, bottom, left, 1};
                    } else if (y == 0) {
                        code = new int[]{1, bottom, left, right};
                    } else if (y == this.map.h - 1) {
                        code = new int[]{top, 1, left, right};
                    }
                }
                if (adjacentTotal == 4 && diagonalTotal < 4) {
                    if (diagonalTotal == 0) {
                        code = appendCode(code, 0, 0, 0, 0);
                    }
                    else {
                        code = appendCode(code, topLeft, topRight, bottomLeft, bottomRight);
                    }
                } else if (adjacentTotal == 2 && diagonalTotal >= 1) {
                    if (Arrays.equals(code, new int[]{0, 1, 0, 1})) {
                        code = appendCode(code, 0, 0, 0, 1);
                    } else if (Arrays.equals(code, new int[]{0, 1, 1, 0})) {
                        code = appendCode(code, 0, 0, 1, 0);
                    } else if (Arrays.equals(code, new int[]{1, 0, 0, 1})) {
                        code = appendCode(code, 0, 1, 0, 0);
                    } else if (Arrays.equals(code, new int[]{1, 0, 1, 0})) {
                        code = appendCode(code, 1, 0, 0, 0);
                    }
                } else if (adjacentTotal == 3 && diagonalTotal == 1) {
                    if (!cannotRecurse) {
                        String leftTile = getAdjacentTiles(map, x - 1, y, true, true);
                        String rightTile = getAdjacentTiles(map, x + 1, y, true, true);
                        String topTile = getAdjacentTiles(map, x, y - 1, true, true);
                        String bottomTile = getAdjacentTiles(map, x, y + 1, true, true);
                        if (!(leftTile.equals(rightTile) || topTile.equals(bottomTile))) {
                            code = appendCode(code, topLeft, topRight, bottomLeft, bottomRight);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                if (this.map.isWall(map, x - 1, y - 1) || this.map.isWall(map, x + 1, y - 1) || this.map.isWall(map, x - 1, y + 1) || this.map.isWall(map, x + 1, y + 1)) {
                    code = new int[]{1, 1, 1, 1};
                    if (x == 0) {
                        if (y == 0) {
                            code = appendCode(code, 1, 1, 1, 0);
                        }
                        if (y == this.map.h - 1) {
                            code = appendCode(code, 1, 1, 0, 1);
                        }
                    } else if (x == this.map.w - 1) {
                        if (y == 0) {
                            code = appendCode(code, 1, 0, 1, 1);
                        }
                        if (y == this.map.h - 1) {
                            code = appendCode(code, 0, 1, 1, 1);
                        }
                    }
                }
            }
            return concatenateCode(code);
        }
        else {
            return adjacentTotal + "-" + diagonalTotal;
        }
    }
}
