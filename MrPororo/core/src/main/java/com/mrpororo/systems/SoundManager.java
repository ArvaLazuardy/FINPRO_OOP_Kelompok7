package com.mrpororo.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, Sound> sounds = new HashMap<>();
    private final Random random = new Random();
    private Music bgm;

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void loadSound(String key, String path) {
        if (!sounds.containsKey(key)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sounds.put(key, sound);
        }
    }

    public void loadBgm(String path) {
        bgm = Gdx.audio.newMusic(Gdx.files.internal(path));
    }

    public void init() {
        // loadSound("shoot1", "audio/Paige_fire_01.mp3");
        // loadBgm("audio/BGM_Flowering Night.mp3");
        // placeholder
    }

    public void playSfx(String key) {
        Sound sound = sounds.get(key);

        if (sound != null) {
            sound.play(0.4f);
        }
    }

    // random death noise
    public void playDeath() {
        int number = random.nextInt(2) + 1;
        playSfx("death" + number);
    }

    public void playBgm() {
        if (bgm != null && !bgm.isPlaying()) {
            bgm.play();
        }
    }

    public void stopBgm() {
        if (bgm != null) {
            bgm.stop();
        }
    }

    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }

        sounds.clear();
        bgm.dispose();
        bgm = null;
    }
}

