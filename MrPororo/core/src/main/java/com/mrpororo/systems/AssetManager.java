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

    public static final String PARRY_START = "parry_start";
    public static final String PARRY_START_V2 = "parry_start_v2";
    public static final String PARRY_SUCCESS = "parry_success";
    public static final String PARRY_SUCCESS_V2 = "parry_success_v2";

    public static final String PARRY_START_SHEET = "ParryStart_SpriteSheet.png";
    public static final String PARRY_START_V2_SHEET = "ParryStartVer2_SpriteSheet.png";
    public static final String PARRY_SUCCESS_SHEET = "ParrySuccessful_SpriteSheet.png";
    public static final String PARRY_SUCCESS_V2_SHEET = "ParrySuccessfulVer2_SpriteSheet.png";

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

        if (Gdx.files == null || !Gdx.files.internal(filename).exists()) {
            return null;
        }

        Texture texture = new Texture(Gdx.files.internal(filename));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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

    /**
     * Registers a sprite sheet laid out as an equally sized grid. Frames are read
     * left-to-right, then top-to-bottom. Returns false when the asset is missing,
     * so development builds work before artists supply the PNGs.
     *
     * For a 1x4 horizontal strip: columns=4, rows=1, frameCount=4.
     * For a 2x2 grid:         columns=2, rows=2, frameCount=4.
     */
    public boolean registerGridAnimation(String key, String filename, int columns, int rows, int frameCount, float frameDuration) {
        if (columns <= 0 || rows <= 0 || frameCount <= 0 || frameCount > columns * rows || frameDuration <= 0f) {
            throw new IllegalArgumentException("Invalid animation layout: " + key);
        }
        Texture texture = loadTexture(filename);
        if (texture == null) return false;
        if (texture.getWidth() % columns != 0 || texture.getHeight() % rows != 0) {
            throw new IllegalArgumentException(
                "Spritesheet dimensions must be divisible by its grid: " + filename);
        }

        int frameWidth = texture.getWidth() / columns;
        int frameHeight = texture.getHeight() / rows;
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            int row = i / columns;
            int col = i % columns;
            frames[i] = new TextureRegion(texture, col * frameWidth,
                row * frameHeight, frameWidth, frameHeight);
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        animationMap.put(key, animation);
        textureRegionMap.put(key, frames[0]);
        return true;
    }

    /**
     * For a sheet whose pixel dimensions are not exactly divisible by its grid
     * (ParryStart is 1774x887 for a 4x2 layout). Split using rounded cell
     * boundaries; this keeps all eight frames without stretching or crashing.
     */
    public boolean registerFlexibleGridAnimation(String key, String filename, int columns, int rows, int frameCount, float frameDuration) {
        if (columns <= 0 || rows <= 0 || frameCount <= 0 || frameCount > columns * rows || frameDuration <= 0f) {
            throw new IllegalArgumentException("Invalid parry grid: " + key);
        }
        Texture texture = loadTexture(filename);
        if (texture == null) return false;

        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            int column = i % columns;
            int row = i / columns;
            int x0 = Math.round(column * texture.getWidth() / (float) columns);
            int x1 = Math.round((column + 1) * texture.getWidth() / (float) columns);
            int y0 = Math.round(row * texture.getHeight() / (float) rows);
            int y1 = Math.round((row + 1) * texture.getHeight() / (float) rows);
            frames[i] = new TextureRegion(texture, x0, y0, x1 - x0, y1 - y0);
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        animationMap.put(key, animation);
        textureRegionMap.put(key, frames[0]);
        return true;
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

        // Version 1: 8 frames, 4 columns x 2 rows (1774 x 887 pixels).
        registerFlexibleGridAnimation(PARRY_START, PARRY_START_SHEET, 4, 2, 8, 0.025f);

        // Version 2: 6 frames, 6 columns x 1 row (312 x 48 pixels).
        registerGridAnimation(PARRY_START_V2, PARRY_START_V2_SHEET, 6, 1, 6, 0.033f);

        // Success version 1: 8 frames, 8 columns x 1 row (1024 x 96 pixels).
        registerGridAnimation(PARRY_SUCCESS, PARRY_SUCCESS_SHEET, 8, 1, 8, 0.045f);

        // Success version 2: 3 frames, 3 columns x 1 row (93 x 32 pixels).
        registerGridAnimation(PARRY_SUCCESS_V2, PARRY_SUCCESS_V2_SHEET, 3, 1, 3, 0.08f);
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
