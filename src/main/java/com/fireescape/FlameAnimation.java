package com.fireescape;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** Plays the four supplied images while keeping their wood base aligned. */
public class FlameAnimation implements Disposable {
    public static final float FRAME_SECONDS=0.15f;
    // The original sprite used a 390-pixel-high crop; taller frames need extra space above it.
    static final float HEIGHT_SCALE=450f/390f;
    private final Texture[] textures=new Texture[4];
    private final Animation<TextureRegion> animation;

    public FlameAnimation() {
        Array<TextureRegion> frames=new Array<>();
        for(int i=0;i<textures.length;i++) {
            textures[i]=new Texture("fire-animation/frame-"+(i+1)+".png");
            textures[i].setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
            // Identical crop for all frames: preserve the full tallest flame and a stable base.
            frames.add(new TextureRegion(textures[i],60,0,360,450));
        }
        animation=new Animation<>(FRAME_SECONDS,frames,Animation.PlayMode.LOOP_PINGPONG);
    }

    TextureRegion frame(float elapsedSeconds) { return animation.getKeyFrame(elapsedSeconds); }

    @Override public void dispose() {
        for(Texture texture:textures) if(texture!=null) texture.dispose();
    }
}
