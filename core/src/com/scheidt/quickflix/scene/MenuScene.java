package com.scheidt.quickflix.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.scheidt.quickflix.QuickFlix;
import com.scheidt.quickflix.actor.PersonActor;
import com.scheidt.quickflix.data.Assets;
import com.scheidt.quickflix.data.CrossReference;

/**
 * Created by NewRob on 2/8/2015.
 */
public class MenuScene extends BaseScene {

    private static final int PEOPLE_X_LEFT = 100;
    private static final int PEOPLE_X_RIGHT = 1280 - 100 - PersonActor.IMG_WIDTH;
    private static final int PEOPLE_Y = 320;

    private CrossReference crs_ref;

    private Stage stage;
    private Image screenBg;
    private final Image chairsBg;
    private Skin skin;
    private Image title;
    //private Label helpTip;
    private Music music;

    private Table table;
    private Table options;
    private Table exit;
    private CheckBox muteCheckBox;
    private Slider volumeSlider;
    private TextButton backButton;
    private TextButton playButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    private TextButton yesButton;
    private TextButton noButton;
    private boolean menuShown;
    private boolean exitShown;

    public MenuScene(final QuickFlix game) {
        super(game);
        this.crs_ref = game.crs_ref;

        stage = new Stage(game.viewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.getFont("default-font").setScale(1.2f, 1.2f);

//        screenBg = new Image(game.atlas.findRegion("background"));
        music = Assets.getManager().get("snd/menu_music.mp3", Music.class);
        screenBg = new Image(Assets.getManager().get("img/background.png", Texture.class));
        chairsBg = new Image(Assets.getManager().get("img/chairs.png", Texture.class));
        title = new Image(Assets.getManager().get("title.png", Texture.class));
//        helpTip=new Label("Tap around the plane to move it!",skin);
//        helpTip.setColor(Color.WHITE);

//        table=new Table().debug(); //debug to show boundary lines
        table = new Table();
        playButton = new TextButton("PLAY GAME", skin);
        table.add(playButton).height(60).width(300).padBottom(20);
        table.row();
        optionsButton = new TextButton("SOUND OPTIONS", skin);
        table.add(optionsButton).height(60).width(300).padBottom(20);
        table.row();

        table.add(new TextButton("LEADERBOARD", skin)).height(60).width(300).padBottom(20);
        table.row();
        exitButton = new TextButton("EXIT GAME", skin);
        table.add(exitButton).height(60).width(300);
        table.setPosition(game.SCREEN_WIDTH / 2, -game.SCREEN_HEIGHT / 2);

        options = new Table();
        Label soundTitle = new Label("SOUND OPTIONS", skin);
        soundTitle.setColor(Color.WHITE);
        options.add(soundTitle).padBottom(25).colspan(2);
        options.row();
        muteCheckBox = new CheckBox(" MUTE ALL", skin);
        options.add(muteCheckBox).padBottom(10).colspan(2);
        options.row();
        options.add(new Label("VOLUME ", skin)).padBottom(10).padRight(10);
        volumeSlider = new Slider(0, 2, 0.2f, false, skin);
        options.add(volumeSlider).padTop(10).padBottom(20);
        options.row();
        backButton = new TextButton("BACK", skin);
        options.add(backButton).colspan(2).padTop(20);
        options.setPosition(game.SCREEN_WIDTH / 2, -200);
        muteCheckBox.setChecked(!game.soundEnabled);
        volumeSlider.setValue(game.soundVolume);

        exit = new Table();
        Label exitTitle = new Label("Confirm Exit", skin);
        exitTitle.setColor(Color.WHITE);
        exit.add(exitTitle).padBottom(25).colspan(2);
        exit.row();
        yesButton = new TextButton("YES", skin);
        exit.add(yesButton).padBottom(20);
        noButton = new TextButton("NO", skin);
        exit.add(noButton).padBottom(20);
        exit.setPosition(game.SCREEN_WIDTH / 2, -200);

        screenBg.setFillParent(true);
        chairsBg.setBounds(-20, -20, game.SCREEN_WIDTH + 40, 100);

//        PersonActor p1 = new PersonActor(PersonActor.getTestPerson());
//        p1.setBounds(PEOPLE_X_LEFT,PEOPLE_Y,PersonActor.IMG_WIDTH,PersonActor.IMG_HEIGHT);
//
//        PersonActor p2 = new PersonActor(PersonActor.getTestPerson());
//        p2.setBounds(PEOPLE_X_RIGHT,PEOPLE_Y,PersonActor.IMG_WIDTH,PersonActor.IMG_HEIGHT);

        stage.addActor(screenBg);
        stage.addActor(chairsBg);

        stage.addActor(title);
        //stage.addActor(helpTip);
        stage.addActor(table);
        stage.addActor(options);
        stage.addActor(exit);
//        stage.addActor(p1);
//        stage.addActor(p2);

        table.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, 5f, 1, Interpolation.pow2In), Actions.moveBy(0, -5f, 1, Interpolation.pow2In))));


        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new ThrustCopterScene(game));
                if (music.isPlaying()) music.stop();
                game.setScreen(new GameScene(game));
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(false);
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                // or System.exit(0);
            }
        });
        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                // or System.exit(0);
            }
        });
        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showExit(!exitShown);
            }
        });
        volumeSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.soundVolume = volumeSlider.getValue();
            }
        });
        muteCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                game.soundEnabled = !muteCheckBox.isChecked();
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(true);
            }
        });
    }

    @Override
    public void show() {
        title.setPosition(game.SCREEN_WIDTH / 2 - title.getWidth() / 2, 800);
        //helpTip.setPosition(400-helpTip.getWidth()/2, 30);

        MoveToAction actionMove = Actions.action(MoveToAction.class);
        actionMove.setPosition(game.SCREEN_WIDTH / 2 - title.getWidth() / 2, 380);
        actionMove.setDuration(1);
        actionMove.setInterpolation(Interpolation.elasticOut);
        title.addAction(actionMove);

        if (!music.isPlaying()) {
            music = Assets.getManager().get("snd/menu_music.mp3", Music.class);
            music.play();
        }


        showMenu(true);
    }


    private void showMenu(boolean flag) {
        MoveToAction actionMove1 = Actions.action(MoveToAction.class);//out
        actionMove1.setPosition(game.SCREEN_WIDTH / 2, -200);
        actionMove1.setDuration(1);
        actionMove1.setInterpolation(Interpolation.swingIn);

        MoveToAction actionMove2 = Actions.action(MoveToAction.class);//in
        actionMove2.setPosition(game.SCREEN_WIDTH / 2, 190);
        actionMove2.setDuration(1f);
        actionMove2.setInterpolation(Interpolation.swing);

        if (flag) {
            table.addAction(actionMove2);
            options.addAction(actionMove1);
        } else {
            options.addAction(actionMove2);
            table.addAction(actionMove1);
        }
        menuShown = flag;
        exitShown = false;
    }

    private void showExit(boolean flag) {
        MoveToAction actionMove1 = Actions.action(MoveToAction.class);//out
        actionMove1.setPosition(game.SCREEN_WIDTH / 2, -200);
        actionMove1.setDuration(1);
        actionMove1.setInterpolation(Interpolation.swingIn);

        MoveToAction actionMove2 = Actions.action(MoveToAction.class);//in
        actionMove2.setPosition(game.SCREEN_WIDTH / 2, 190);
        actionMove2.setDuration(1f);
        actionMove2.setInterpolation(Interpolation.swing);

        if (flag) {
            exit.addAction(actionMove2);
            table.addAction(actionMove1);
        } else {
            table.addAction(actionMove2);
            exit.addAction(actionMove1);
        }
        exitShown = flag;
    }

    @Override
    public void render(float delta) {
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
        if (!menuShown) {
            showMenu(!menuShown);
        } else {
            showExit(!exitShown);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        music.dispose();
    }

}

