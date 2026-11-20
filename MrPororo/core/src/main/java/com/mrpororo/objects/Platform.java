package com.mrpororo.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Platform extends GameObject {

    private final boolean oneWay;

    public Platform(float x, float y, float width, float height,
                    TextureRegion texture) {
        this(x, y, width, height, texture, false);
    }

    public Platform(float x, float y, float width, float height,
                    TextureRegion texture, boolean oneWay) {

        super(x, y, width, height, 0f, Color.LIGHT_GRAY);

        setSprite(texture);
        this.oneWay = oneWay;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public Rectangle getCoreHitbox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (sprite != null) {
            batch.draw(sprite, x, y, width, height);
        }
    }
}
