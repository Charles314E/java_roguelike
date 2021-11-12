package com.Entities.Afflictions;

import com.Entities.Entity;

public abstract class Affliction {
    public String name;
    public int level;
    public Entity target;
    public int duration;
    public int effectGap;
    public int timer;
    public int decisecond = 0;
    public int timeGap = 10;

    public void setAttributes(Entity target, int level, int duration, int timeGap) {
        this.target = target;
        this.level = level;
        this.duration = duration;
        this.timeGap = timeGap;
        this.timer = 0;
    }

    public void applyEffect() {
        decisecond += 1;
        if (decisecond == timeGap) {
            decisecond = 0;
            String applyWord = "";
            if (timer < duration) {
                if (timer == 0) {
                    startEffect();
                    applyWord = "started";
                } else {
                    continueEffect();
                    applyWord = "continued";
                }
                timer += 1;
            } else {
                endEffect();
                applyWord = "ended";
                target.afflictions.remove(this);
                target = null;
            }
            System.out.println("[DBG]: " + hashCode() + " " + applyWord + " " + name + " effect. (" + timer + " / " + duration + ")");
        }
    }

    public abstract void startEffect();
    public abstract void continueEffect();
    public abstract void endEffect();
}
