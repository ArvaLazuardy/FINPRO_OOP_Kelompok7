
package com.mrpororo;

import com.badlogic.gdx.Game;
import com.mrpororo.screens.GameScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
