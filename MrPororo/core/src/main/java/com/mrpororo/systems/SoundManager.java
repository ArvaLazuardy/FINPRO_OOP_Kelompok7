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
    private long parryaudioId = -1L;

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

    // New audio may not be present in every teammate's local checkout yet.
    private void loadOptionalSound(String key, String path) {
        if (Gdx.files.internal(path).exists()) {
            loadSound(key, path);
        }
    }

    public void loadBgm(String path) {
        bgm = Gdx.audio.newMusic(Gdx.files.internal(path));
    }

    public void init() {
        for (int i = 1; i <= 8; i++) {
            loadOptionalSound("player_walk" + i, "audio/player_walk" + i + ".mp3");
        }
        for (int i = 1; i <= 6; i++) {
            loadOptionalSound("player_shoot" + i, "audio/player_shoot" + i + ".mp3");
        }
        for (int i = 1; i <= 2; i++) {
            loadOptionalSound("bullet_impact" + i, "audio/bullet_impact" + i + ".mp3");
        }
        loadOptionalSound("player_jump", "audio/player_jump.mp3");

        loadSound("parry_start", "audio/melee_parry_start.mp3");
        loadSound("parry_success", "audio/melee_parry_success.mp3");
        loadSound("freeze_start", "audio/time_freeze_start.mp3");
        loadSound("freeze_end", "audio/time_freeze_end.mp3");

        loadSound("player_hit1", "audio/player_hit_1.mp3");
        loadSound("player_hit2", "audio/player_hit_2.mp3");
        loadSound("player_hit3", "audio/player_hit_3.mp3");


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

    public void playSfx(String key, float volume) {
        Sound sound = sounds.get(key);

        if (sound != null) {
            sound.play(volume);
        }
    }

    // custom volume
    public void startFreezeAudio() {
        playSfx("freeze_start");
        if (freezeLoop != null) freezeLoop.play();
    }

    // random play
    private void playRandomSfx(String prefix, int count) {
        int available = 0;
        String selected = null;
        for (int i = 1; i <= count; i++) {
            String key = prefix + i;
            if (sounds.containsKey(key) && random.nextInt(++available) == 0) {
                selected = key;
            }
        }
        if (selected != null) playSfx(selected);
    }

    // custom volume
    private void playRandomSfx(String prefix, int count, float volume) {
        int available = 0;
        String selected = null;
        for (int i = 1; i <= count; i++) {
            String key = prefix + i;
            if (sounds.containsKey(key) && random.nextInt(++available) == 0) {
                selected = key;
            }
        }
        if (selected != null) playSfx(selected, volume);
    }

    public void playPlayerWalk() {
        playRandomSfx("player_walk", 8);
    }

    public void playPlayerShoot() {
        playRandomSfx("player_shoot", 6, 0.15f);
    }

    public void playPlayerJump() {
        playSfx("player_jump");
    }

    public void playBulletImpact() {
        playRandomSfx("bullet_impact", 2);
    }

    public void playPlayerHit() {
        playRandomSfx("player_hit", 3);
    }

    public void playBgm() {
        if (bgm != null && !bgm.isPlaying()) {
            bgm.play();
        }
    }

    public void startParry() {
        stopParry();
        Sound sound = sounds.get("parry_start");
        if (sound != null) parryaudioId = sound.play(0.4f);
    }

    public void stopParry() {
        Sound sound = sounds.get("parry_start");
        if (sound != null && parryaudioId >= 0L) sound.stop(parryaudioId);
        parryaudioId = -1L;
    }

    public void successfulParry() {
        stopParry();
        playSfx("parry_success");
    }

    public void stopFreezeAudio() {
        if (freezeLoop != null) freezeLoop.stop();
        playSfx("freeze_end");
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
        if (bgm != null) { bgm.dispose(); bgm = null; }
        if (freezeLoop != null) { freezeLoop.dispose(); freezeLoop = null; }
    }
}

