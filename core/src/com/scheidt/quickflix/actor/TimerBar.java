package com.scheidt.quickflix.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.scheidt.quickflix.data.Assets;

/**
 * Created by NewRob on 2/8/2015.
 */
public class TimerBar extends Actor {

    public static int IMG_WIDTH = 400;
    public static int IMG_HEIGHT = 20;

    float timer;

    Texture texture;

    public TimerBar() {
        texture = Assets.getManager().get("img/bar.png", Texture.class);

        timer = IMG_WIDTH;

        this.setWidth(IMG_WIDTH);
        this.setHeight(IMG_HEIGHT);

    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(texture, getX()-IMG_WIDTH/2, getY(), timer, IMG_HEIGHT);

    }

    public boolean isTimeUp() {
        if (timer <= 0) {
            timer = 0;
            return true;
        } else {
            return false;
        }
    }


    public void decrementTimer(float amt) {
        timer -= amt;
        if (timer > IMG_WIDTH) timer = IMG_WIDTH;
    }


}
