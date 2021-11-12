package com.System.InputMethods;

import com.Entities.Player;
import com.System.Enums;

import java.awt.event.*;

public class KeyPressed implements KeyListener {
    public Player player;                       //Which player the key presses should be mapped to.

    //KeyPressed constructor.
    public KeyPressed(Player player) {
        this.player = player;
    }

    //When a key is pressed, determine whether it is a valid input and if it is, assign it to the player.
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_W) {
            player.key = Enums.key.UP;
        }
        else if (e.getKeyCode() == e.VK_S) {
            player.key = Enums.key.DOWN;
        }
        else if (e.getKeyCode() == e.VK_A) {
            player.key = Enums.key.LEFT;
        }
        else if (e.getKeyCode() == e.VK_D) {
            player.key = Enums.key.RIGHT;
        }
        else if (e.getKeyCode() == e.VK_SPACE) {
            player.key = Enums.key.ATTACK;
        }
        else if (e.getKeyCode() == e.VK_SHIFT) {
            player.key = Enums.key.CANCEL;
        }
        else {
            System.out.println("[DBG]: No action performed.");
        }
    }
    public void keyTyped(KeyEvent e) {

    }

    //When a key is released, reset the player's key.
    public void keyReleased(KeyEvent e) {
        //System.out.println("[DBG]: Enums released.");
        player.key = null;
        player.subimage = 0;
    }
}