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
    private Music freezeLoop;

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

        loadSound("parry_success", "audio/melee_parry_success.mp3");
        loadSound("freeze_start", "audio/time_freeze_start.mp3");
        loadSound("freeze_end", "audio/time_freeze_end.mp3");

        if (freezeLoop == null && Gdx.files != null && Gdx.audio != null && Gdx.files.internal("audio/time_freeze_duration.mp3").exists()) {
            freezeLoop = Gdx.audio.newMusic(Gdx.files.internal("audio/time_freeze_duration.mp3"));
            freezeLoop.setLooping(true);
            freezeLoop.setVolume(0.18f);
        }
    }

    public void playSfx(String key) {
        Sound sound = sounds.get(key);

        if (sound != null) {
            sound.play(0.4f);
        }
    }

    public void startFreezeAudio() {
        playSfx("freeze_start");
        if (freezeLoop != null) freezeLoop.play();
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
        freezeLoop.dispose();
        freezeLoop = null;
    }
}

