package com.scheidt.quickflix.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.scheidt.quickflix.QuickFlix;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class BaseScene extends ScreenAdapter{
	protected QuickFlix game;
	private boolean keyHandled;

	public BaseScene(QuickFlix game) {
        this.game = game;
        keyHandled=false;
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);


	}
	@Override
	public void render(float delta) {
		super.render(delta);
		if(Gdx.input.isKeyPressed(Keys.BACK)){
			if(keyHandled){
				return;
			}
			handleBackPress();
			keyHandled=true;
		}else{
			keyHandled=false;
		}
	}
	protected void handleBackPress() {
		System.out.println("back");
	}

    public void switchScreen(final Game game, final Screen newScreen, Stage stage){
        stage.getRoot().getColor().a = 1;
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(fadeOut(0.5f));
        sequenceAction.addAction(run(new Runnable() {
            @Override
            public void run() {
                game.setScreen(newScreen);
            }
        }));
        stage.getRoot().addAction(sequenceAction);
    }
	
}
