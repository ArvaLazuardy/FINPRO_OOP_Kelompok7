package com.mrpororo.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FreezeOverlayRenderer {

    private float freezeDuration;
    private boolean isVisible;

    private SpriteBatch batch;
    private BitmapFont font;

    public FreezeOverlayRenderer() {
        freezeDuration = 0;
        isVisible = false;

        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public void render() {
        if (!isVisible) {
            return;
        }

        batch.begin();
        font.draw(batch, "TIME FROZEN!", 250, 300);
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
