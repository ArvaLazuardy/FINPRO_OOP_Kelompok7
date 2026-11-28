
package com.mrpororo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SpriteTestScreen extends ScreenAdapter {

    // ==============================
    // SCREEN
    // ==============================

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;
    private Viewport viewport;

    // ==============================
    // PLAYER SIZE
    // ==============================

    private float playerWidth = 100;
    private float playerHeight = 100;

    private float x = 200;
    private float y = 100;

    private final float groundY = 100;

    // ==============================
    // MOVEMENT
    // ==============================

    private final float moveSpeed = 250;
    private final float jumpSpeed = 450;
    private final float gravity = 900;

    private float velocityY = 0;

    private boolean onGround = true;
    private boolean facingRight = true;

    // ==============================
    // ANIMATIONS
    // ==============================

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> shootAnimation;
    private Animation<TextureRegion> dyingAnimation;

    private float animationTime = 0;

    // Shoot animation
    private final float shootDuration = 0.40f;
    private float shootTimer = 0;

    // Jump animation
    private float jumpAnimationTime = 0;

    // Dying animation
    private float dyingTime = 0f;
    private boolean dyingStarted = false;

    // ==============================
    // TEXTURES
    // ==============================

    private final Array<Texture> textures =
        new Array<>();

    private Texture bulletTexture;
    private TextureRegion bulletRegion;

    // ==============================
    // BULLETS
    // ==============================

    private final Array<Bullet> bullets =
        new Array<>();

    private static class Bullet {

        float x;
        float y;
        float velocityX;

        Bullet(float x, float y, float velocityX) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
        }
    }

    // ==============================
    // SHOW
    // ==============================

    @Override
    public void show() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();

        viewport = new FitViewport(
            WORLD_WIDTH,
            WORLD_HEIGHT,
            camera
        );

        viewport.apply();

        camera.position.set(
            WORLD_WIDTH / 2,
            WORLD_HEIGHT / 2,
            0
        );

        camera.update();

        // ==========================
        // LOAD PLAYER ANIMATIONS
        // ==========================

        idleAnimation = loadAnimation(
            "player/idle/",
            "idle",
            2,
            0.15f,
            Animation.PlayMode.LOOP
        );

        walkAnimation = loadAnimation(
            "player/walk/",
            "walk",
            4,
            0.10f,
            Animation.PlayMode.LOOP
        );

        jumpAnimation = loadAnimation(
            "player/jump/",
            "jump",
            9,
            0.10f,
            Animation.PlayMode.NORMAL
        );

        shootAnimation = loadAnimation(
            "player/shoot/",
            "shoot",
            2,
            0.08f,
            Animation.PlayMode.NORMAL
        );

        // ==========================
        // LOAD DYING ANIMATION
        // ==========================

        dyingAnimation = loadAnimation(
            "player/dying/",
            "dying",
            7,
            0.12f,
            Animation.PlayMode.NORMAL
        );

        // ==========================
        // LOAD BULLET
        // ==========================

        bulletTexture = new Texture(
            Gdx.files.internal(
                "player/bullet.png"
            )
        );

        bulletTexture.setFilter(
            Texture.TextureFilter.Nearest,
            Texture.TextureFilter.Nearest
        );

        bulletRegion = new TextureRegion(
            bulletTexture
        );
    }

    // ==============================
    // LOAD ANIMATION
    // ==============================

    private Animation<TextureRegion> loadAnimation(
        String folder,
        String fileName,
        int frameCount,
        float frameDuration,
        Animation.PlayMode playMode
    ) {

        Array<TextureRegion> frames =
            new Array<>();

        for (int i = 1; i <= frameCount; i++) {

            String path = folder
                + fileName
                + i
                + ".png";

            Texture texture = new Texture(
                Gdx.files.internal(path)
            );

            // Pixel art: no smoothing
            texture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );

            textures.add(texture);

            frames.add(
                new TextureRegion(texture)
            );
        }

        Animation<TextureRegion> animation =
            new Animation<>(
                frameDuration,
                frames
            );

        animation.setPlayMode(playMode);

        return animation;
    }

    // ==============================
    // RESET PLAYER
    // ==============================

    private void resetPlayer() {

        x = 200;
        y = groundY;

        velocityY = 0;

        onGround = true;
        facingRight = true;

        shootTimer = 0;
        jumpAnimationTime = 0;
        animationTime = 0;

        dyingTime = 0;
        dyingStarted = false;

        bullets.clear();
    }

    // ==============================
    // UPDATE
    // ==============================

    private void update(float delta) {

        animationTime += delta;

        // ==========================
        // RESET
        // ==========================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.R)) {

            resetPlayer();
        }

        // ==========================
        // START DYING
        // ==========================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.K)) {

            dyingStarted = true;
            dyingTime = 0f;

            shootTimer = 0;
            velocityY = 0;

            bullets.clear();
        }

        // ==========================
        // DYING UPDATE
        // ==========================

        if (dyingStarted) {

            dyingTime += delta;

            // Stop normal player movement
            return;
        }

        // ==========================
        // MOVEMENT
        // ==========================

        boolean moving = false;

        if (Gdx.input.isKeyPressed(
            Input.Keys.A)
            || Gdx.input.isKeyPressed(
            Input.Keys.LEFT)) {

            x -= moveSpeed * delta;

            facingRight = false;
            moving = true;
        }

        if (Gdx.input.isKeyPressed(
            Input.Keys.D)
            || Gdx.input.isKeyPressed(
            Input.Keys.RIGHT)) {

            x += moveSpeed * delta;

            facingRight = true;
            moving = true;
        }

        // Screen boundaries
        x = Math.max(
            0,
            Math.min(
                x,
                WORLD_WIDTH - playerWidth
            )
        );

        // ==========================
        // JUMP
        // ==========================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.SPACE)
            && onGround) {

            velocityY = jumpSpeed;

            onGround = false;

            jumpAnimationTime = 0;
        }

        // ==========================
        // GRAVITY
        // ==========================

        if (!onGround) {

            velocityY -= gravity * delta;

            y += velocityY * delta;

            jumpAnimationTime += delta;
        }

        // ==========================
        // LANDING
        // ==========================

        if (y <= groundY) {

            y = groundY;

            velocityY = 0;

            onGround = true;
        }

        // ==========================
        // SHOOT
        // ==========================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.J)
            && shootTimer <= 0) {

            shootTimer = shootDuration;

            float bulletX;

            if (facingRight) {
                bulletX = x + playerWidth * 0.80f;
            } else {
                bulletX = x + playerWidth * 0.20f;
            }

            float bulletY =
                y + playerHeight * 0.55f;

            float bulletSpeed = facingRight
                ? 500
                : -500;

            bullets.add(
                new Bullet(
                    bulletX,
                    bulletY,
                    bulletSpeed
                )
            );
        }

        if (shootTimer > 0) {
            shootTimer -= delta;
        }

        // ==========================
        // BULLET MOVEMENT
        // ==========================

        for (int i = bullets.size - 1;
             i >= 0; i--) {

            Bullet bullet = bullets.get(i);

            bullet.x +=
                bullet.velocityX * delta;

            if (bullet.x < -100
                || bullet.x > WORLD_WIDTH + 100) {

                bullets.removeIndex(i);
            }
        }
    }

    // ==============================
    // GET PLAYER FRAME
    // ==============================

    private TextureRegion getCurrentFrame() {

        // Dying
        if (dyingStarted) {

            return dyingAnimation.getKeyFrame(
                dyingTime,
                false
            );
        }

        // Shoot
        if (shootTimer > 0) {

            float elapsed =
                shootDuration - shootTimer;

            return shootAnimation.getKeyFrame(
                elapsed
            );
        }

        // Jump
        if (!onGround) {

            return jumpAnimation.getKeyFrame(
                jumpAnimationTime
            );
        }

        // Walk
        if (Gdx.input.isKeyPressed(
            Input.Keys.A)
            || Gdx.input.isKeyPressed(
            Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(
            Input.Keys.D)
            || Gdx.input.isKeyPressed(
            Input.Keys.RIGHT)) {

            return walkAnimation.getKeyFrame(
                animationTime
            );
        }

        // Idle
        return idleAnimation.getKeyFrame(
            animationTime
        );
    }

    // ==============================
    // DRAW FLIPPED SPRITE
    // ==============================

    private void drawSprite(
        TextureRegion region,
        float x,
        float y,
        float width,
        float height,
        boolean flipX
    ) {

        float u = region.getU();
        float v = region.getV();

        float u2 = region.getU2();
        float v2 = region.getV2();

        // Flip horizontal only
        if (flipX) {

            float temp = u;
            u = u2;
            u2 = temp;
        }

        batch.draw(
            region.getTexture(),
            x,
            y,
            width,
            height,
            u,
            v,
            u2,
            v2
        );
    }

    // ==============================
    // RENDER
    // ==============================

    @Override
    public void render(float delta) {

        delta = Math.min(delta, 0.033f);

        update(delta);

        // Background
        Gdx.gl.glClearColor(
            0.05f,
            0.08f,
            0.15f,
            1
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        camera.update();

        batch.setProjectionMatrix(
            camera.combined
        );

        shapeRenderer.setProjectionMatrix(
            camera.combined
        );

        // ==========================
        // DRAW GROUND
        // ==========================

        shapeRenderer.begin(
            ShapeRenderer.ShapeType.Filled
        );

        shapeRenderer.rect(
            0,
            groundY - 5,
            WORLD_WIDTH,
            5
        );

        shapeRenderer.end();

        // ==========================
        // DRAW PLAYER
        // ==========================

        batch.begin();

        TextureRegion currentFrame =
            getCurrentFrame();

        drawSprite(
            currentFrame,
            x,
            y,
            playerWidth,
            playerHeight,
            !facingRight
        );

        // ==========================
        // DRAW BULLETS
        // ==========================

        float bulletWidth = 48;
        float bulletHeight = 32;

        for (Bullet bullet : bullets) {

            boolean bulletFacingRight =
                bullet.velocityX > 0;

            drawSprite(
                bulletRegion,
                bullet.x - bulletWidth / 2,
                bullet.y - bulletHeight / 2,
                bulletWidth,
                bulletHeight,
                !bulletFacingRight
            );
        }

        batch.end();
    }

    // ==============================
    // RESIZE
    // ==============================

    @Override
    public void resize(int width, int height) {

        viewport.update(
            width,
            height,
            true
        );
    }

    // ==============================
    // DISPOSE
    // ==============================

    @Override
    public void dispose() {

        batch.dispose();
        shapeRenderer.dispose();

        for (Texture texture : textures) {
            texture.dispose();
        }

        if (bulletTexture != null) {
            bulletTexture.dispose();
        }
    }
}
