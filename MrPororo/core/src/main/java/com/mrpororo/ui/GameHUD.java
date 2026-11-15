
package com.mrpororo.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class GameHUD {

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public GameHUD() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();

        font.setColor(Color.WHITE);
        font.getData().setScale(1.3f);
    }

    public void setProjectionMatrix(Matrix4 projection) {
        batch.setProjectionMatrix(projection);
        shapeRenderer.setProjectionMatrix(projection);
    }

    public void render(
        float health,
        float maxHealth,
        float energy,
        float maxEnergy
    ) {
        renderBars(health, maxHealth, energy, maxEnergy);
        renderText(health, maxHealth, energy, maxEnergy);
    }

    private void renderBars(
        float health,
        float maxHealth,
        float energy,
        float maxEnergy
    ) {
        float healthPercentage = maxHealth > 0
            ? Math.max(0f, Math.min(health / maxHealth, 1f))
            : 0f;

        float energyPercentage = maxEnergy > 0
            ? Math.max(0f, Math.min(energy / maxEnergy, 1f))
            : 0f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(24, 490, 200, 16);

        shapeRenderer.setColor(0.85f, 0.20f, 0.25f, 1f);
        shapeRenderer.rect(24, 490, 200 * healthPercentage, 16);

        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(24, 458, 200, 16);

        shapeRenderer.setColor(0.30f, 0.81f, 0.94f, 1f);
        shapeRenderer.rect(24, 458, 200 * energyPercentage, 16);

        shapeRenderer.end();
    }

    private void renderText(
        float health,
        float maxHealth,
        float energy,
        float maxEnergy
    ) {
        batch.begin();

        font.draw(batch,
            "HP: " + (int) health + "/" + (int) maxHealth,
            24, 530);

        font.draw(batch,
            "PARRY ENERGY: " + (int) energy + "/" + (int) maxEnergy,
            24, 498);

        batch.end();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
