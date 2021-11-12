package com.Step;

import com.Entities.Afflictions.Affliction;
import com.Entities.Enemy;
import com.Entities.Player;
import com.GUI.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MoveEnemy implements ActionListener {
    Player player;                      //The player the enemies are focusing on.
    ArrayList<Enemy> enemyList;         //The list of all enemies the timer is controlling.

    //MoveEnemy constructor.
    public MoveEnemy(Player player, ArrayList<Enemy> enemyList) {
        this.player = player;
        this.enemyList = enemyList;
    }

    //Every game tick, make all enemies perform their behaviours, such as moving idly, tracking the player and attacking the player.
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < enemyList.size(); i += 1) {
            Enemy enemy = enemyList.get(i);
            for (int j = 0; j < enemy.afflictions.size(); j += 1) {
                enemy.afflictions.get(j).applyEffect();
            }
            if (!enemy.attacking) {
                enemy.move += 1;
                enemy.attack += 1;
                enemy.setSubimage();
                if (enemy.move >= enemy.moveGap) {
                    int[] nextStep = enemy.trackPlayer(player).get(0);
                    if (nextStep[2] == 0) {
                        if (enemy.map.checkEntityPositions(nextStep[0], nextStep[1]) == null) {
                            enemy.xDir = nextStep[0] - enemy.x;
                            enemy.yDir = nextStep[1] - enemy.y;
                            enemy.teleport(nextStep[0], nextStep[1]);
                            enemy.move = 0;
                        }
                    } else if (enemy.attack >= enemy.attackGap) {
                        enemy.imageIndex = 0;
                        enemy.attacks[0].attack();
                        enemy.attack = 0;
                        System.out.println("Enemy " + enemy + " attacked " + player + ".");
                    }
                }
            }
            else {
                enemy.attacks[0].attack();
            }
            enemy.setBoundingBox();
        }
    }
}
