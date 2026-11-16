package com.mrpororo.screens;

import com.badlogic.gdx.Screen;
import com.mrpororo.Main;
import com.mrpororo.levels.Level;
import com.mrpororo.levels.RunGunLevel;
import com.mrpororo.ui.GameHUD;
import com.mrpororo.ui.FreezeOverlayRenderer;

public class GameScreen implements Screen {

    private final Main game;
    private Level level;
    private GameHUD hud;
    private FreezeOverlayRenderer freezeOverlay;

    public GameScreen(Main game) {
        this.game = game;
        this.level = new RunGunLevel();
        this.hud = new GameHUD();
        this.freezeOverlay = new FreezeOverlayRenderer();
    }

    @Override
    public void show() {
        level.load();
    }

    @Override
    public void render(float delta) {
        level.update(delta);
        level.render();

        hud.render();
        freezeOverlay.render();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        level.pause();
    }

    @Override
    public void resume() {
        level.resume();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        level.dispose();
        hud.dispose();
        freezeOverlay.dispose();
    }
}
