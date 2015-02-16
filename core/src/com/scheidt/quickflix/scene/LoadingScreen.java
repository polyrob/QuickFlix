package com.scheidt.quickflix.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.scheidt.quickflix.QuickFlix;
import com.scheidt.quickflix.data.Assets;
import com.scheidt.quickflix.data.CrossReference;
import com.scheidt.quickflix.util.StreamUtil;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends ScreenAdapter {

    private Stage stage;

    //	private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    private Actor loadingBar;
    QuickFlix game;
    private float waitTime = 1;

    public LoadingScreen(QuickFlix game) {
        this.game = game;
    }

    @Override
    public void show() {
        //stage.setViewport(game.viewport);

        // Tell the manager to load assets for the loading screen
        Assets.getManager().load("loading.pack", TextureAtlas.class);

        // Wait until they are finished loading
        Assets.getManager().finishLoading();

        // Initialize the stage where we will place everything
        stage = new Stage(game.viewport);

        // Get our textureatlas from the manager
        TextureAtlas atlas = Assets.getManager().get("loading.pack", TextureAtlas.class);

        // Grab the regions from the atlas and create some images
        //logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        //stage.addActor(logo);

        Assets.init();
    }

    @Override
    public void resize(int width, int height) {
        // Scale the viewport to fit the screen
        Vector2 scaledView = Scaling.fit.apply(800, 480, width, height);
        stage.getViewport().update((int) scaledView.x, (int) scaledView.y, true);

        // Make the background fill the screen
        screenBg.setSize(width, height);

        // Place the logo in the middle of the screen and 100 px up
//		logo.setX((width - logo.getWidth()) / 2);
//		logo.setY((height - logo.getHeight()) / 2 + 100);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few
        // px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a
        // few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Assets.getManager().update()) { // Load some, will return true if done loading
            waitTime -= delta;
            if (waitTime <= 0) {
                game.atlas=Assets.getManager().get("mainassets.pack", TextureAtlas.class);
                game.font = Assets.getManager().get("fnt/impact-40.fnt", BitmapFont.class);

                /* load cross reference file */
                try {
                    CrossReference crossReference = (CrossReference) StreamUtil.readFromGame(CrossReference.FILENAME);
                    crossReference.initLists();
                    game.crs_ref = crossReference;
                } catch (Exception e) {
                    e.printStackTrace();
                    Gdx.app.error("LoadingScreen", "Error loading/deserializing Cross Reference Data.");
                    Gdx.app.exit();
                }

                game.setScreen(new MenuScene(game));
            }
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, Assets.getManager().getProgress(), 0.1f);

        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
        Assets.getManager().unload("loading.pack");
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
