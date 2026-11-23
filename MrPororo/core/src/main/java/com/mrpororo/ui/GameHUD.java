package com.mrpororo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mrpororo.objects.Player;
import com.mrpororo.systems.ParrySystem;
import com.mrpororo.systems.TimeManager;

public class GameHUD {
    private final BitmapFont font = new BitmapFont();

    public void render(SpriteBatch batch, ShapeRenderer shapes, Player player, ParrySystem parry, TimeManager time, String levelName) {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0f, 0f, 0f, 0.55f);
        shapes.rect(15f, 480f, 345f, 46f);
        shapes.setColor(0.25f, 0.35f, 0.5f, 1f);
        shapes.rect(19f, 484f, 180f, 14f);
        shapes.setColor(0.4f, 0.85f, 1f, 1f);
        shapes.rect(19f, 484f, 180f * parry.getEnergyPercentage(), 14f);
        shapes.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + (int) player.getHealth() + " / " + (int) player.getMaxHealth(), 20f, 522f);
        font.draw(batch, "PARRY  " + (int) parry.getEnergy() + "%", 208f, 498f);
        font.draw(batch, "Level: " + levelName + (time.isFrozen() ? "   FROZEN" : ""), 20f, 465f);
        font.draw(batch, "A/D move | SPACE jump | J shoot | F parry | K parryable shot | Q freeze | F1/F2/F3", 20f, 26f);
        batch.end();
    }
    public void dispose() { font.dispose(); }
}
