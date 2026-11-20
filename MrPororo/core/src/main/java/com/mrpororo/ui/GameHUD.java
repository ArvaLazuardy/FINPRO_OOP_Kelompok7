package com.mrpororo.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.objects.Player;
import com.mrpororo.systems.ParrySystem;
import com.mrpororo.systems.TimeManager;

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

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, Player player, ParrySystem parrySystem, TimeManager timeManager, String levelName) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.4f, 0.85f, 1f, 1f);
        shapeRenderer.rect(20f, 360f, 180f * parrySystem.getEnergyPercentage(), 14f);

        shapeRenderer.end();

        batch.begin();

        font.draw(batch, "Score: " + score, 20, 460);
        font.draw(batch, "Health: " + health, 20, 430);
        font.draw(batch, "Parry  " + (int) parrySystem.getEnergy() + "%", 20f, 390f);
        font.draw(batch, "A/D move | SPACE jump | F freeze | 1/2/3 Levels | R respawn", 20f, 26f);

        batch.end();
    }

    public void update() {
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
