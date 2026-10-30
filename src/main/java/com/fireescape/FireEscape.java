package com.fireescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** Owns the graphics shared by the menu, gameplay and result screens. */
public class FireEscape extends Game {
    static final float WIDTH = 960, HEIGHT = 640;
    final FitViewport viewport = new FitViewport(WIDTH, HEIGHT);
    SpriteBatch batch;
    BitmapFont font;
    Texture background, waterImage, white;
    TextureRegion water;
    FlameAnimation flameAnimation;
    ShaderProgram spriteShader;
    int bestScore;

    @Override public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        background = new Texture("background.png");
        flameAnimation = new FlameAnimation();
        waterImage = new Texture("water.png");
        water = new TextureRegion(waterImage, 240, 180, 270, 450);
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE); pixel.fill();
        white = new Texture(pixel); pixel.dispose();
        // Hide the black sprite background without changing the original artwork.
        spriteShader = new ShaderProgram(
            "attribute vec4 a_position; attribute vec4 a_color; attribute vec2 a_texCoord0; uniform mat4 u_projTrans; varying vec4 v_color; varying vec2 v_texCoords; void main(){v_color=a_color; v_color.a*=255.0/254.0; v_texCoords=a_texCoord0; gl_Position=u_projTrans*a_position;}",
            "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color; varying vec2 v_texCoords; uniform sampler2D u_texture; void main(){vec4 c=texture2D(u_texture,v_texCoords); if(max(c.r,max(c.g,c.b))<0.004) discard; gl_FragColor=c*v_color;}");
        if (!spriteShader.isCompiled()) throw new IllegalStateException(spriteShader.getLog());
        showScreen(new MenuScreen(this));
    }

    void showScreen(Screen next) {
        Screen old = getScreen(); setScreen(next);
        if (old != null) old.dispose();
    }
    void begin() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply(); batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin(); batch.setColor(Color.WHITE);
        batch.draw(background, 0, -160, WIDTH, WIDTH);
    }
    void beginWorld(WorldView view) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(view.camera.combined);
        batch.begin(); batch.setColor(Color.WHITE);
        // Tile the original artwork at fixed world positions; never stretch it across the map.
        int firstX=(int)Math.floor(view.visible.x/960);
        int lastX=(int)Math.floor((view.visible.x+view.visible.width)/960);
        int firstY=(int)Math.floor(view.visible.y/960);
        int lastY=(int)Math.floor((view.visible.y+view.visible.height)/960);
        for(int x=firstX;x<=lastX;x++) for(int y=firstY;y<=lastY;y++)
            batch.draw(background,x*960,y*960,960,960);
        // A visible border explains why movement stops at the arena boundary.
        block(0,0,Arena.WIDTH,8,0.45f,0.27f,0.12f,1);
        block(0,Arena.HEIGHT-8,Arena.WIDTH,8,0.45f,0.27f,0.12f,1);
        block(0,0,8,Arena.HEIGHT,0.45f,0.27f,0.12f,1);
        block(Arena.WIDTH-8,0,8,Arena.HEIGHT,0.45f,0.27f,0.12f,1);
    }

    void beginInterface() {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin(); batch.setColor(Color.WHITE);
    }

    void panel(float x, float y, float w, float h) {
        batch.setColor(0.16f, 0.10f, 0.07f, 0.94f);
        batch.draw(white, x, y, w, h); batch.setColor(Color.WHITE);
    }
    void block(float x,float y,float w,float h,float r,float g,float b,float a) {
        batch.setColor(r,g,b,a); batch.draw(white,x,y,w,h); batch.setColor(Color.WHITE);
    }
    void text(String value, float x, float y, float scale) {
        font.setColor(1, 0.94f, 0.82f, 1); font.getData().setScale(scale);
        font.draw(batch, value, x, y);
    }
    void centered(String value, float y, float scale) {
        font.getData().setScale(scale);
        GlyphLayout layout = new GlyphLayout(font, value);
        text(value, (WIDTH-layout.width)/2, y, scale);
    }
    void drawFlame(float elapsedSeconds,float x,float y,float width,float height) {
        // Only the picture changes: position, movement and collision bounds stay stable.
        sprite(flameAnimation.frame(elapsedSeconds),x,y,width,height*FlameAnimation.HEIGHT_SCALE);
    }
    void sprite(TextureRegion region, float x, float y, float w, float h) {
        batch.setShader(spriteShader); batch.draw(region, x, y, w, h); batch.setShader(null);
    }
    Vector2 pointer() { return viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY())); }
    @Override public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose(); font.dispose(); background.dispose(); flameAnimation.dispose();
        waterImage.dispose(); white.dispose(); spriteShader.dispose();
    }
}
