package com.scheidt.quickflix;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.scheidt.quickflix.data.Assets;
import com.scheidt.quickflix.data.CrossReference;
import com.scheidt.quickflix.scene.LoadingScreen;

public class QuickFlix extends Game {

    public SpriteBatch batch;
    //public FPSLogger fpsLogger;
    public OrthographicCamera camera;
    public TextureAtlas atlas;
    public Viewport viewport;
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 800;
    public BitmapFont font;
    public boolean soundEnabled;
    public float soundVolume;
    public CrossReference crs_ref;

    public QuickFlix() {
        //this.fpsLogger=new FPSLogger();
        //Assets.getInstance().init();
        this.camera = new OrthographicCamera();
        this.camera.position.set(SCREEN_WIDTH/2, SCREEN_HEIGHT /2,0);
        this.viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    }

    @Override
    public void create() {
        batch=new SpriteBatch();
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render(){
        //fpsLogger.log();
        super.render();
    }

    @Override
    public void resize (int width, int height) {
        viewport.update(width, height);
    }
    @Override
    public void dispose () {
        batch.dispose();
        atlas.dispose();
        Assets.dispose();
    }
}
