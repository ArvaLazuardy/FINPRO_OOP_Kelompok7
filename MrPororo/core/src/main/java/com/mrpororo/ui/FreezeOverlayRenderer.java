
package com.mrpororo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class FreezeOverlayRenderer {

    private final ShapeRenderer renderer;

    private float alpha;
    private boolean active;

    public FreezeOverlayRenderer() {
        renderer = new ShapeRenderer();
        alpha = 0f;
        active = false;
    }

    public void setProjectionMatrix(Matrix4 projection) {
        renderer.setProjectionMatrix(projection);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void update(float delta) {
        float targetAlpha = active ? 0.28f : 0f;

        alpha += (targetAlpha - alpha) * Math.min(delta * 5f, 1f);
    }

    public void render(float worldWidth, float worldHeight) {
        if (alpha <= 0.01f) {
            return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Blue translucent freeze overlay
        renderer.setColor(0.45f, 0.85f, 1f, alpha);
        renderer.rect(0, 0, worldWidth, worldHeight);

        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        renderer.dispose();
    }
}
