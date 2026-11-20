package com.mrpororo.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Blue frost tint + faint analog clock while Time Freeze is active. */
public class FreezeOverlayRenderer {
    public void render(ShapeRenderer shapes, boolean visible, float remaining) {
        if (!visible) return;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.28f, 0.67f, 1f, 0.21f);
        shapes.rect(0f, 0f, 960f, 540f);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.84f, 0.98f, 1f, 0.45f);
        shapes.circle(480f, 270f, 150f, 72);
        shapes.circle(480f, 270f, 134f, 72);
        shapes.line(480f, 270f, 480f + 110f * (float)Math.sin(remaining), 270f + 110f * (float)Math.cos(remaining));
        shapes.line(480f, 270f, 480f + 72f * (float)Math.sin(remaining * 0.3f), 270f + 72f * (float)Math.cos(remaining * 0.3f));
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    public void dispose() {}
}
