package com.scheidt.quickflix.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.scheidt.quickflix.data.Assets;

/**
 * Created by NewRob on 2/8/2015.
 */
public class Choice extends Actor {

    public static int IMG_WIDTH = 640;
    public static int IMG_HEIGHT = 211;

    Texture bkgnd;

    public Choice(String movie) {

        bkgnd = Assets.getManager().get("img/film_strip.png", Texture.class);

        this.setWidth(IMG_WIDTH);
        this.setHeight(IMG_HEIGHT);

    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(bkgnd, getX()-30, getY()-100, getWidth()+60, getHeight()+165);

    }

}
