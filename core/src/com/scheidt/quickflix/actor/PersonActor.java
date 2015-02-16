package com.scheidt.quickflix.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.scheidt.quickflix.data.Assets;
import com.scheidt.quickflix.models.Person;

/**
 * Created by NewRob on 2/8/2015.
 */
public class PersonActor extends Group {

    public static int IMG_WIDTH = 185;
    public static int IMG_HEIGHT = 278;

    Person person;
    Texture thumb;
    Texture bkgnd;
    BitmapFont font;

    public PersonActor(Person person) {
        this.person = person;

        thumb = new Texture(Gdx.files.internal("img/p" + person.getImgPath()));
        bkgnd = Assets.getManager().get("img/ticket.png", Texture.class);
        font = Assets.getManager().get("fnt/impact-40.fnt", BitmapFont.class);

        this.setWidth(IMG_WIDTH);
        this.setHeight(IMG_HEIGHT);
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(bkgnd, getX() - 30, getY() - 100, getWidth() + 60, getHeight() + 165);
        batch.draw(thumb, getX(), getY(), getWidth(), getHeight());

        font.draw(batch, person.getName(), IMG_WIDTH/2 + getX()-font.getBounds(person.getName()).width/2, getY()-100);
       // textureREgoin batch.draw(thumb, getX(), getY(), getOriginX(), getOriginY(),
               // getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        thumb = new Texture(Gdx.files.internal("img/p" + person.getImgPath()));
        //nameLabel.setText(person.getTitle());
//        nameLabel.invalidate();
    }


}
