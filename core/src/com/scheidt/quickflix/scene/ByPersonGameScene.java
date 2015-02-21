package com.scheidt.quickflix.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.scheidt.quickflix.QuickFlix;
import com.scheidt.quickflix.actor.PersonActor;
import com.scheidt.quickflix.actor.TimerBar;
import com.scheidt.quickflix.data.Assets;
import com.scheidt.quickflix.data.CrossReference;
import com.scheidt.quickflix.models.GamePayload;
import com.scheidt.quickflix.models.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by NewRob on 2/8/2015.
 */
public class ByPersonGameScene extends BaseScene {

    private static final int PEOPLE_X_LEFT = 100;
    private static final int PEOPLE_X_RIGHT = 1280 - 100 - PersonActor.IMG_WIDTH;
    private static final int PEOPLE_Y = 320;
    public static final float MOVE_SPEED = 0.5f;
    public static final int GAME_SPEED = 10;

    QuickFlix game;
    private CrossReference crs_ref;

    private Stage stage;

    private TimerBar timerBar;
    private float startX, endX;
    private float timer = 50f;

    private Image screenBg;
    private final Image chairsBg;
    private Skin skin;

    private Music music;
    private Sound losingHorn;
    private Sound pop;
    private Sound swoosh;
    private Sound wrong;

    private MoveToAction mtaShift;

    private int level = 1;
    private Label levelLabel;


    private Table choiceTable;
    private Table graveyardTable;
    private PersonActor pGraveyard, pLeft, pRight, pNext;


    /* temp for scoring */
    private int wrongAnswers = 0;
    private boolean gameover = false;


    public ByPersonGameScene(final QuickFlix game) {
        super(game);
        this.game = game;
        this.crs_ref = game.crs_ref;

        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.getFont("default-font").setScale(1.4f, 1.4f);

        screenBg = new Image(Assets.getManager().get("img/background.png", Texture.class));
        chairsBg = new Image(Assets.getManager().get("img/chairs.png", Texture.class));

        music = Assets.getManager().get("snd/DST-AlphaTron.mp3", Music.class);
        losingHorn = Assets.getManager().get("snd/losing_horn.mp3", Sound.class);
        pop = Assets.getManager().get("snd/pop.ogg", Sound.class);
        swoosh = Assets.getManager().get("snd/swoosh.mp3", Sound.class);
        wrong = Assets.getManager().get("snd/wrong_buzzer.mp3", Sound.class);


        mtaShift = action(MoveToAction.class);
        mtaShift.setPosition(PEOPLE_X_LEFT, PEOPLE_Y);
        mtaShift.setDuration(1);
        mtaShift.setInterpolation(Interpolation.elasticOut);
        //title.addAction(mtaShift);

        timerBar = new TimerBar();
        timerBar.setPosition(game.SCREEN_WIDTH / 2, (game.SCREEN_HEIGHT / 2) + 300);

        levelLabel = new Label("Level: " + level, skin);
        levelLabel.setPosition(game.SCREEN_WIDTH / 2 - levelLabel.getTextBounds().width/2, 600);
        //levelLabel.setAlignment(Align.center);


        choiceTable = new Table();

//        playTable = new Table().debug();
//        playTable.setFillParent(true);

        screenBg.setFillParent(true);
        chairsBg.setBounds(-20, -20, game.SCREEN_WIDTH + 40, 100);


        stage.addActor(screenBg);
        stage.addActor(chairsBg);
        stage.addActor(timerBar);
        stage.addActor(levelLabel);
    }

    @Override
    public void show() {
        startX = 200;
        endX = 400;

        startGame();
    }

    private void startGame() {
        music.play();
        /* start with random person from list */
        Person p1 = crs_ref.getRandomPerson();
        GamePayload payload = crs_ref.getGamePayload(p1, 1);

        //pOut, pLeft, pRight, pNext;;
        pLeft = new PersonActor(payload.p1);
        pRight = new PersonActor(payload.p2);

//        pLeft.setBounds(PEOPLE_X_LEFT,PEOPLE_Y,PersonActor.IMG_WIDTH,PersonActor.IMG_HEIGHT);
//        pRight.setBounds(PEOPLE_X_RIGHT,PEOPLE_Y,PersonActor.IMG_WIDTH,PersonActor.IMG_HEIGHT);
        pLeft.setPosition(PEOPLE_X_LEFT, PEOPLE_Y);
        pRight.setPosition(PEOPLE_X_RIGHT, PEOPLE_Y);

        choiceTable = buildChoiceTable(payload);
        //choiceTable.addAction(moveBy(0, -600, 1, Interpolation.pow2));

        stage.addActor(pLeft);
        stage.addActor(pRight);
        stage.addActor(choiceTable);
    }


    private void nextTurn() {
        swoosh.play();
        level++;
        levelLabel.setText("Level: " + level);
        if (graveyardTable != null) graveyardTable.remove();
        if (pGraveyard != null) pGraveyard.remove();
        Gdx.app.log("GameScene", "nextTurn()");
        GamePayload payload = crs_ref.getGamePayload(pRight.getPerson(), level);

//        pLeft.setPerson(pRight.getPerson());
        pLeft.addAction(moveBy(-800, 0, MOVE_SPEED));
        pRight.addAction(moveTo(PEOPLE_X_LEFT, PEOPLE_Y, MOVE_SPEED));
        //pNext.addAction(moveBy(-600, 0, 1));
        pGraveyard = pLeft;
        pLeft = pRight;
        pRight = new PersonActor(payload.p2);

        pRight.setPosition(PEOPLE_X_RIGHT + 800, PEOPLE_Y);
        pRight.addAction(moveTo(PEOPLE_X_RIGHT, PEOPLE_Y, MOVE_SPEED));
        //pRight.setPerson(payload.p2);

        choiceTable.addAction(moveBy(0, -600, MOVE_SPEED, Interpolation.pow2));
        graveyardTable = choiceTable;
        choiceTable = buildChoiceTable(payload);
        stage.addActor(choiceTable);
        stage.addActor(pRight);
        System.out.println("Current stage size: " + stage.getActors().size);
    }


    private Table buildChoiceTable(GamePayload payload) {
        Table newTable = new Table();

        TextButton btnCorrect = new TextButton(payload.correct.getTitle(), skin);
        TextButton btnWrong1 = new TextButton(payload.wrong1.getTitle(), skin);
        TextButton btnWrong2 = new TextButton(payload.wrong2.getTitle(), skin);


        List<TextButton> btnList = new ArrayList<TextButton>();
        btnList.add(btnCorrect);
        btnList.add(btnWrong1);
        btnList.add(btnWrong2);

        btnWrong1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                wrong.play();
                System.out.println("wrong.");
                timerBar.decrementTimer(50);

            }
        });
        btnWrong2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                wrong.play();
                System.out.println("wrong.");
                timerBar.decrementTimer(50);
            }
        });
        btnCorrect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pop.play();
                System.out.println("correct!");
                timerBar.decrementTimer(-25);
                nextTurn();
            }
        });


        long seed = System.nanoTime();
        Collections.shuffle(btnList, new Random(seed));

        newTable.clear();

        newTable.add(btnList.get(0)).height(100).width(600).padBottom(40);
        newTable.row();
        newTable.add(btnList.get(1)).height(100).width(600).padBottom(40);
        newTable.row();
        newTable.add(btnList.get(2)).height(100).width(600);
        newTable.setPosition(game.SCREEN_WIDTH / 2, (game.SCREEN_HEIGHT / 2) + 600);
        newTable.addAction(moveBy(0, -600, 0.5f, Interpolation.pow2));

        return newTable;
    }


    @Override
    public void render(float delta) {

        // Interpolate the percentage to make it more smooth
        //percent = Interpolation.linear.apply(percent, Assets.getManager().getProgress(), 0.1f);

        if (!gameover) {
            timerBar.decrementTimer(delta * GAME_SPEED);
            if (timerBar.isTimeUp()) {

                gameover = true;
                if (music.isPlaying()) music.stop();
                losingHorn.play();
                choiceTable.clearListeners();
                levelLabel.addAction(parallel(scaleBy(2f, 2f, 2f, Interpolation.pow2), moveBy(0, -100f, 2)));
                choiceTable.addAction(sequence(moveBy(0, -1000, 2f, Interpolation.pow2), delay(2f), run(new Runnable() {
                    @Override
                    public void run() {
                        switchScreen(game, new MenuScene(game), stage);
                        //game.setScreen(new MenuScene(game));
                    }
                })));
            }

        }
        // Update positions (and size) to match the percentage

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Show the loading screen
        stage.act();
        stage.draw();

        //Table.drawDebug(stage);
        super.render(delta);
    }

    @Override
    protected void handleBackPress() {
        Gdx.app.log("GameScene", "handleBackPress()");
//        if(!menuShown){
//            //showMenu(!menuShown);
//        }else{
//            //showExit(!exitShown);
//        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScene", "dispose()");
        stage.dispose();
        skin.dispose();
        music.dispose();
        losingHorn.dispose();
    }

}

