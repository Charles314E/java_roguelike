package com.Entities;

import com.Entities.AttackTypes.Attacks.Attack;
import com.System.EnemyBattlers;
import com.System.Enums;
import com.System.Maps.Map;
import com.methods;

import java.util.ArrayList;

public class Enemy extends Entity {
    public int move = 0;                        //The timer for the enemy to move.
    public int moveGap = 2;                     //How many game ticks it takes for the enemy to move.
    public int attack = 0;                      //The timer for enemy attacks.
    public int attackGap;                       //How many game ticks it takes for the enemy to attack.
    int experienceDrop;                         //How much experience the enemy drops.
    Enums.enemyBehaviourType behaviour;         //The enemy's overall movement behaviour.
    Enums.enemyBehaviourType currentBehaviour;  //The current behaviour pattern the enemy is adopting.
    int sightRadius;                            //If the enemy is neutral, the minimum distance between it and the player for it to become hostile.

    //Enemy constructor, sets the enemy's map, and sets its stats and sprites using its name.
    public Enemy(String name, int level, Map map, int maxHealth, int maxMana, int moveGap, double walkingSpeed, int attackGap, double attackingSpeed, int attackSubimage, boolean flying, boolean swimming, int experienceDrop, Enums.enemyBehaviourType behaviour) {
        this.spritePath = spriteBaseFolder + "Enemy/";
        this.name = name;
        this.map = map;
        this.level = level + map.player.difficulty;
        getSprite();
        this.maxHealth = maxHealth;
        this.maxMana = maxMana;
        this.moveGap = moveGap;
        this.walkingSpeed = walkingSpeed;
        this.attackGap = attackGap;
        this.attackingSpeed = attackingSpeed;
        this.attackSubimage = attackSubimage;
        this.flying = flying;
        this.swimming = swimming;
        this.experienceDrop = experienceDrop;
        this.behaviour = behaviour;
        attacks = new Attack[] {
                new Attack(this),
        };
        sightRadius = 6;
        setCurrentBehaviour();
        System.out.println(methods.tuple(flying, swimming));
        this.health = this.maxHealth;
        this.mana = this.maxMana;
        this.attackImageNumber = 0;
        if (this.attackSheet != null) {
            this.attackImageNumber = this.attackSheet.getWidth(null) / tileSize;
        }
    }
    //Enemy constructor. Teleports it to a location on the map.
    public Enemy(String name, int level, Map map, int x, int y) {
        this(name, level, map, 1, 0, 0, 0.0, 0, 0.0, 0, false, false, 1, Enums.enemyBehaviourType.RANDOM);
        teleport(x, y);
    }

    //If the enemy is dead, grant player experience and remove the enemy from the map.
    public void checkHealth() {
        if (health == 0) {
            map.player.gainExperience(experienceDrop * level, false);
            map.enemyList.remove(this);
            map.alignLists(true);
        }
    }

    //Set the current enemy behaviour.
    public void setCurrentBehaviour() {
        currentBehaviour = behaviour;
        if (behaviour == Enums.enemyBehaviourType.RANDOM_UNTIL_APPROACHED) {
            currentBehaviour = Enums.enemyBehaviourType.RANDOM;
            if (checkDistance(map.player) < sightRadius) {
                currentBehaviour = Enums.enemyBehaviourType.HOSTILE;
            }
        }
    }

    //Pathfind towards the player, one step at a time. This function checks each of the enemy's adjacent walls to see where it can move that reduces
    //its distance to the player.
    public ArrayList<int[]> trackPlayer(Player player) {
        if (map.player != null) {
            setCurrentBehaviour();
            ArrayList<int[]> path = new ArrayList<>();
            int cx = x;
            int cy = y;
            ArrayList<int[]> dir = new ArrayList<>();
            for (int i = 0; i < 4; i += 1) {
                int nx = cx;
                int ny = cy;
                switch (i) {
                    case 0:
                        ny -= 1;
                        break;
                    case 1:
                        nx += 1;
                        break;
                    case 2:
                        ny += 1;
                        break;
                    case 3:
                        nx -= 1;
                        break;
                }
                if ((!map.isWall(nx, ny) || flying) || (map.isWater(nx, ny) && (swimming || flying))) {
                    dir.add(new int[]{nx, ny, 0});
                }
            }
            int[] bestNode = dir.get(methods.randomRange(0, dir.size() - 1));
            if (currentBehaviour == Enums.enemyBehaviourType.HOSTILE) {
                bestNode = new int[]{x, y, 1};
                if (methods.distance(x, y, player.x, player.y) > 1) {
                    double bestDist = Double.POSITIVE_INFINITY;
                    for (int[] node : dir) {
                        double dist = methods.distance(node[0], node[1], player.x, player.y);
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestNode = node;
                        }
                    }
                } else {
                    if (!(map.checkEntityPositions(x + xDir, y + yDir) == player)) {
                        xDir = player.x - x;
                        yDir = player.y - y;
                    }
                }
            }
            path.add(bestNode);
            return path;
        }
        return null;
    }
}
