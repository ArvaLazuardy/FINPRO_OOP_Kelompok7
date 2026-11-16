package com.mrpororo.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameHUD {

    private int score;
    private int health;

    private SpriteBatch batch;
    private BitmapFont font;

    public GameHUD() {
        score = 0;
        health = 3;

        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    public void render() {
        batch.begin();

        font.draw(batch, "Score: " + score, 20, 460);
        font.draw(batch, "Health: " + health, 20, 430);

        batch.end();
    }

    public void update() {
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
