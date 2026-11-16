package com.mrpororo.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static AssetManager instance;

    public static final String WINTER_TILESET = "Snowy winter game tileset.png";
    public static final String SNOW_GROUND = "snow_ground";
    public static final String SNOW_PLATFORM = "snow_platform";
    public static final String SNOW_LEDGE = "snow_ledge";
    public static final String SNOW_TREE_LEFT = "snow_tree_left";
    public static final String SNOW_TREE_MIDDLE = "snow_tree_middle";
    public static final String SNOW_TREE_RIGHT = "snow_tree_right";

    private Map<String, TextureRegion> textureRegionMap;
    private Map<String, Animation<TextureRegion>> animationMap;
    private Map<String, Texture> textureMap;

    private AssetManager() {
        textureRegionMap = new HashMap<>();
        animationMap = new HashMap<>();
        textureMap = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public Texture loadTexture(String filename) {
        if (textureMap.containsKey(filename)) {
            return textureMap.get(filename);
        }

        if (Gdx.files == null &&  Gdx.files.internal(filename).exists()) {
            return null;
        }

        Texture texture = new Texture(Gdx.files.internal(filename));
        textureMap.put(filename, texture);

        return texture;
    }

    public void registerRegion(String key, TextureRegion region) {
        textureRegionMap.put(key, region);
    }

    public void registerPixelRegion(String key, String filename, int x, int y, int width, int height) {
        Texture sheet = loadTexture(filename);
        if (sheet == null) return;
        if (x < 0 || y < 0 || width <= 0 || height <= 0 || x + width > sheet.getWidth() || y + height > sheet.getHeight()) {
            throw new IllegalArgumentException("Invalid tileset region: " + key);
        }

        registerRegion(key, new TextureRegion(sheet, x, y, width, height));
    }

    public void registerRegionFromSheet(String key, String filename, int tileWidth, int tileHeight, int row, int col) {
        Texture tex = loadTexture(filename);
        if (tex != null) {
            TextureRegion[][] grid = TextureRegion.split(tex, tileWidth, tileHeight);
            textureRegionMap.put(key, grid[row][col]);
        }
    }

    public void registerAnimationFromSheet(String key, String filename, int tileWidth, int tileHeight, int row, int numFrames, float frameDuration) {
        registerAnimationFromSheet(key, filename, tileWidth, tileHeight, row, 0, numFrames, frameDuration, Animation.PlayMode.LOOP);
    }

    public void registerAnimationFromSheet(String key, String filename, int tileWidth, int tileHeight, int row, int startCol, int numFrames, float frameDuration, Animation.PlayMode playMode) {
        Texture tex = loadTexture(filename);
        if (tex != null) {
            TextureRegion[][] grid = TextureRegion.split(tex, tileWidth, tileHeight);
            TextureRegion[] frames = new TextureRegion[numFrames];

            for (int i = 0; i < numFrames; i++) {
                frames[i] = grid[row][startCol+i];
            }

            Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
            anim.setPlayMode(playMode);

            animationMap.put(key, anim);
            textureRegionMap.put(key, frames[0]);
        }
    }

    // Overload untuk animasi yang dibalik arahnya (misal menghadap kiri dengan flip horizontal)
    public void registerFlippedAnimationFromSheet(String key, String filename, int tileWidth, int tileHeight, int row, int numFrames, float frameDuration, boolean flipX, boolean flipY) {
        registerFlippedAnimationFromSheet(key, filename, tileWidth, tileHeight, row, 0, numFrames, frameDuration, Animation.PlayMode.LOOP, flipX, flipY);
    }

    public void registerFlippedAnimationFromSheet(String key, String filename, int tileWidth, int tileHeight, int row, int startCol, int numFrames, float frameDuration, Animation.PlayMode playMode, boolean flipX, boolean flipY) {
        Texture tex = loadTexture(filename);
        if (tex != null) {
            TextureRegion[][] grid = TextureRegion.split(tex, tileWidth, tileHeight);
            TextureRegion[] frames = new TextureRegion[numFrames];

            for (int i = 0; i < numFrames; i++) {
                TextureRegion frame = new TextureRegion(grid[row][startCol+i]);
                frame.flip(flipX, flipY);
                frames[i] = frame;
            }

            Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
            anim.setPlayMode(playMode);

            animationMap.put(key, anim);
            textureRegionMap.put(key, frames[0]);
        }
    }

    // Mengambil region berdasarkan key
    public TextureRegion getTextureRegion(String key) {
        return textureRegionMap.get(key);
    }

    public TextureRegion getRegion(String key) {
        return getTextureRegion(key);
    }

    // Mengambil animasi berdasarkan key
    public Animation<TextureRegion> getAnimation(String key) {
        return animationMap.get(key);
    }

    public void init() {
        registerPixelRegion(SNOW_GROUND, WINTER_TILESET, 78, 1000, 450, 373);
        registerPixelRegion(SNOW_PLATFORM, WINTER_TILESET, 155, 737, 205, 216);
        registerPixelRegion(SNOW_LEDGE, WINTER_TILESET, 252, 639, 216, 81);
        registerPixelRegion(SNOW_TREE_LEFT, WINTER_TILESET, 0, 36, 345, 497);
        registerPixelRegion(SNOW_TREE_MIDDLE, WINTER_TILESET, 355, 83, 335, 447);
        registerPixelRegion(SNOW_TREE_RIGHT, WINTER_TILESET, 707, 0, 420, 530);

        // registerRegionFromSheet("item_power", "items.png", 16, 16, 0, 0);
        // placeholder
    }

    public void dispose() {
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }

        textureMap.clear();
        textureRegionMap.clear();
        animationMap.clear();
    }
}
