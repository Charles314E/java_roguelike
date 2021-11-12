package com.Entities;

import com.Entities.Afflictions.Affliction;
import com.Entities.AttackTypes.Attacks.Attack;
import com.System.Animation;
import com.System.Enums;
import com.System.Fonts;
import com.System.Maps.Map;
import com.System.Text.FloatingText;
import com.methods;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Entity extends Animation {
    public String spriteBaseFolder = "sprites/";                            //Attributes regarding entity sprites.
    public String spritePath;
    public BufferedImage spriteSheet;
    public BufferedImage attackSheet;
    public String name;                                                     //The entity's name.
    public int tileSize = 32;                                               //The size of tiles in the game.
    public double walkingSpeed = 0.5;                                       //Attributes regarding the entity's animation.
    public double attackingSpeed = 1.0;
    public double imageSpeed = walkingSpeed;
    public int attackImageNumber;
    public int attackSubimage;
    public boolean attacking = false;                                       //Whether the entity is attacking.
    public boolean invulnerable = false;                                    //Whether the entity is immune to attack.
    public boolean flying = false;                                          //Whether the entity can fly over walls.
    public boolean swimming = false;                                        //Whether the entity can swim in water.
    public Attack[] attacks;                                                //A list of the entity's available attacks.
    public int level;                                                       //The entity's stats.
    public int health;
    public int maxHealth;
    public int mana;
    public int maxMana;
    public int attack = 0;
    public int defense = 0;
    public int x;                                                           //The entity's coordinates.
    public int y;
    public Rectangle boundingBox;                                           //The entity's bounding box.
    public int xDir;                                                        //The entity's direction.
    public int yDir;
    public int[][] space;                                                   //How much space the entity takes up.
    public Map map;                                                         //What map the entity is currently in.
    public ArrayList<Affliction> afflictions = new ArrayList<>();           //All of the afflictions the entity has applied to them.

    //Fetch the entity's sprites for different actions. Sprite filenames are determined by the entity's name.
    public void getSprite() {
        this.imageNumber = 3;
        this.spritePath += name;
        this.spriteSheet = methods.getSprite(this.spritePath);
        this.attackSheet = methods.getSprite(this.spritePath + "_attacking");
        System.out.println(this.spriteSheet);
        System.out.println(this.attackSheet);
        this.spritePath += ".png";
        System.out.println("[DBG]: " + name + " sprite - " + this.spritePath);
    }

    //Update the bounding box.
    public void setBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new Rectangle(x, y, 1, 1);
        }
        boundingBox.setLocation(x, y);
    }

    //Damage the entity. This has different effects depending on the type of entity.
    public void damageHealth(int damage) {
        damageHealth(damage, 1, 1);
    }

    public void checkHealth() {

    }

    public void damageHealth(int damage, double attackMultiplier, double defenseMultiplier) {
        //invulnerable = true;
        health = (int) (Math.max(0, health - (damage * attackMultiplier)) - (defense * defenseMultiplier));
        map.frame.textStores.get("damage").addDamageText(damage, x, y);
        checkHealth();
    }

    public void damageHealth(int damage, Color damageTextColour) {
       damageHealth(damage, 1, 1, damageTextColour);
    }

    public void damageHealth(int damage, double attackMultiplier, double defenseMultiplier, Color damageTextColour) {
        damageHealth(damage, attackMultiplier, defenseMultiplier);
        ArrayList<FloatingText> textStore = map.frame.textStores.get("damage").floatingText;
        textStore.get(textStore.size() - 1).setColours(damageTextColour);
    }

    //Reduce mana.
    public void damageMana(int damage) {
        mana = Math.max(0, mana - damage);
    }

    //Increase level.
    public int increaseLevel(int n) {
        level += n;
        return level;
    }

    public boolean isAfflicted(String effect) {
        for (Affliction affliction : afflictions) {
            if (affliction.name.equals(effect)) {
                return true;
            }
        }
        return false;
    }

    //Move the entity based on key input. This input can come from a variety of sources, not just keyboard presses.
    public int[] move(Enums.key input) {
        int[] move = new int[2];
        switch (input) {
            case LEFT: move = new int[] {-1, 0}; System.out.println("[DBG]: Moved left."); break;
            case RIGHT: move = new int[] {1, 0}; System.out.println("[DBG]: Moved right."); break;
            case UP: move = new int[] {0, -1}; System.out.println("[DBG]: Moved up."); break;
            case DOWN: move = new int[] {0, 1}; System.out.println("[DBG]: Moved down."); break;
        }
        if (map.checkEntityPositions(x + move[0], y + move[1]) == null) {
            boolean collided = checkCollision(move[0], move[1]);
            if (!collided) {
                x += move[0];
                y += move[1];
                return move;
            }
        }
        System.out.println("[DBG]: Collided with wall.");
        return move;
    }

    //Teleport to another area of the map.
    public boolean teleport(int x, int y) {
        if (!this.map.isWall(x, y) || flying) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    //Change direction.
    public void setDirection(int x, int y) {
        xDir = x;
        yDir = y;
    }

    //If the entity is not flying, check whether it can move where it wants to for lack of walls and other creatures.
    public boolean checkCollision(int xStep, int yStep) {
        if (!flying) {
            if (map.isWall(x + xStep, y + yStep) || map.checkEntityPositions(x + xStep, y + yStep) != null) {
                return true;
            }
            return false;
        }
        return false;
    }

    //Check distance between entities or coordinates.
    public double checkDistance(Entity entity) {
        return checkDistance(entity.x, entity.y);
    }
    public double checkDistance(int x, int y) {
        return methods.distance(this.x, this.y, x, y);
    }

    //Set the subimage of an attacking entity, and make it stop attacking when its animation is finished.
    public void setAttackSubimage() {
        setSubimage(attackImageNumber, attackingSpeed);
        if (imageIndex == 0) {
            attacking = false;
        }
    }

    //Set the subimage of a passive entity.
    public void setSubimage() {
        setSubimage(imageNumber, imageSpeed);
    }

    //Draw the entity, using rectangular sections of a sprite sheet. If there is no sprite sheet, default to a coloured square.
    //Row 1 - Down
    //Row 2 - Right
    //Row 3 - Left
    //Row 4 - Up
    public void drawSelf(Graphics g, int x, int y, int screenTileSize, Color colour, Area cutArea) {
        if (spriteSheet != null) {
            int offsetX = 0;
            int offsetY = 0;
            BufferedImage sprite = methods.imageDeepCopy(spriteSheet);
            if (attacking) {
                offsetX = xDir * (subimage * 2);
                offsetY = yDir * (subimage * 2);
                if (attackSheet != null) {
                    sprite = methods.imageDeepCopy(attackSheet);
                }
            }
            int tileX = tileSize * subimage;
            int tileY;
            int[] dir = {xDir, yDir};
            if (Arrays.equals(dir, new int[] {0, 1})) {
                tileY = 0;
            } else if (Arrays.equals(dir, new int[] {-1, 0})) {
                tileY = tileSize;
            } else if (Arrays.equals(dir, new int[] {1, 0})) {
                tileY = tileSize * 2;
            } else {
                tileY = tileSize * 3;
            }
            if (invulnerable) {
                methods.tintImage(sprite, Color.WHITE);
            }
            g.drawImage(sprite,
                    x + methods.integerDivision(offsetX, tileSize, screenTileSize), y + methods.integerDivision(offsetY, tileSize, screenTileSize),
                    x + screenTileSize + methods.integerDivision(offsetX, tileSize, screenTileSize), y + screenTileSize + methods.integerDivision(offsetY, tileSize, screenTileSize),
                    tileX, tileY, tileX + tileSize, tileY + tileSize,
                    null);
            methods.drawString(g, "" + level,
                    x + screenTileSize + methods.integerDivision(offsetX, tileSize, screenTileSize),
                    y + methods.integerDivision(offsetX, tileSize, screenTileSize),
                    Fonts.font.DAMAGE, Color.WHITE);
        }
        else {
            g.setColor(colour);
            g.fillRect(x, y, screenTileSize, screenTileSize);
        }
    }
}