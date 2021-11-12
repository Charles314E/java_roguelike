package com.System;

import com.methods;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Animation {
    public BufferedImage sprite;                    //The spritesheet the animation cycles through.
    public int subimage = 0;                        //The animation's current subimage, which is how far into the animation it is based on its speed.
    public int imageNumber;                         //How many subimages the animation has.
    public int imageIndex = 0;                      //The animation's current image index, which is the image the animation is currently displaying.
    public double imageSpeed;                       //How fast the animation plays.
    public boolean active = true;                   //Whether the animation is visible.
    public Enums.animationEnd animationEnd;         //What happens when the animation ends.

    //Animation constructor. Default is to destroy the animation when it ends.
    public Animation() {}
    public Animation(String spritePath, double imageSpeed, int imageNumber) {
        sprite = methods.getSprite(spritePath);
        this.imageSpeed = imageSpeed;
        this.imageNumber = imageNumber;
        this.animationEnd = Enums.animationEnd.DESTROY;
    }
    public Animation(String spritePath, double imageSpeed, int imageNumber, Enums.animationEnd animationEnd) {
        this(spritePath, imageSpeed, imageNumber);
        this.animationEnd = animationEnd;
    }

    //Set the animation's subimage.
    public void setSubimage() {
        setSubimage(imageNumber, imageSpeed);
    }
    public void setSubimage(int cap, double speed) {
        if (active) {
            imageIndex += 1;
            if (imageIndex >= cap / speed) {
                imageIndex = 0;
                if (animationEnd == Enums.animationEnd.DESTROY) {
                    active = false;
                }
            }
            subimage = (int) (imageIndex * speed);
        }
    }
}
