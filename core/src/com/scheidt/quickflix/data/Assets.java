package com.scheidt.quickflix.data;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by NewRob on 2/9/2015.
 */
public class Assets {

    private static AssetManager manager;


    public static void init() {
        manager.load("mainassets.pack", TextureAtlas.class);
        Assets.getManager().load("loading.pack", TextureAtlas.class);
//        manager.load("badlogic.jpg", Texture.class);
        manager.load("img/background.png", Texture.class);
        manager.load("img/chairs.png", Texture.class);
        manager.load("img/film_strip.png", Texture.class);
        manager.load("img/ticket.png", Texture.class);
        manager.load("img/bar.png", Texture.class);
        manager.load("title.png", Texture.class);

        manager.load("snd/menu_music.mp3", Music.class);
        manager.load("snd/DST-AlphaTron.mp3", Music.class);
        manager.load("snd/losing_horn.mp3", Sound.class);
        manager.load("snd/pop.ogg", Sound.class);
        manager.load("snd/swoosh.mp3", Sound.class);
        manager.load("snd/wrong_buzzer.mp3", Sound.class);

        manager.load("fnt/yal32.fnt", BitmapFont.class);
        manager.load("fnt/impact-40.fnt", BitmapFont.class);

        manager.load("uiskin.atlas", TextureAtlas.class);
        manager.load("uiskin.json", Skin.class);
        //manager.load("default.fnt", BitmapFont.class);
    }

    public static AssetManager getManager() {
        if (manager == null) manager = new AssetManager();
        return manager;
    }

    public static void dispose() {
        if (manager == null) return;
        manager.dispose();
    }


}
