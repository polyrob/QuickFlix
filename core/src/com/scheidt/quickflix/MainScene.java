package com.scheidt.quickflix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.scheidt.quickflix.data.Assets;

/**
 * Created by NewRob on 2/8/2015.
 */
public class MainScene extends ScreenAdapter {

    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private QuickFlix game;

    Texture badlogic;

    Sound tapSound;

    GameState gameState = GameState.Init;

    static enum GameState { Init, Action, GameOver }

    public MainScene(QuickFlix game) {
        this.game = game;
        this.batch = game.batch;
        this.camera = game.camera;

        tapSound = Assets.getManager().get("snd/pop.ogg", Sound.class);
        badlogic = Assets.getManager().get("badlogic.jpg", Texture.class);
        font= Assets.getManager().get("fnt/yal32.fnt", BitmapFont.class);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateScene(delta);
        drawScene();
    }


    private void updateScene(float deltaTime) {
        if(Gdx.input.justTouched()){
            tapSound.play();
            Gdx.app.log(this.getClass().getName(), "justTouched() coords: " + Gdx.input.getX() + ", " + Gdx.input.getY());
            if(gameState == GameState.Init) {
                System.out.println("Changing GameState -> Action");
                gameState = GameState.Action;
                return;
            }
            if(gameState == GameState.GameOver) {
                System.out.println("Changing GameState -> Init");
                gameState = GameState.Init;
                //resetScene();
                return;
            }
        }

        /* stuff to process every update call */

    }

    private void drawScene() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        //batch.disableBlending();
        //batch.draw(badlogic,0,0);
        //batch.enableBlending();

        font.draw(batch, "GameState: " + gameState, 25, 40);

        if(gameState == GameState.Init){
            batch.draw(badlogic,0,0);

        }
        if(gameState == GameState.GameOver){
           // batch.draw(gameOver, 400-206, 240-80);
        }

//        batch.setColor(Color.BLACK);
//        batch.draw(fuelIndicator, 10, 350);
//        batch.setColor(Color.WHITE);
        //batch.draw(fuelIndicator,10,350,0,0,fuelPercentage,119);
        //if(gameState == GameState.GameOver)explosion.draw(batch);
        batch.end();
    }

    private void resetScene(){

    }
}
