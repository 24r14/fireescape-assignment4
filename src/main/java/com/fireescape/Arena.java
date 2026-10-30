package com.fireescape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** World coordinates are separate from the fixed 960 x 640 interface. */
public final class Arena {
    public static final float WIDTH=3840, HEIGHT=2560;
    public static final float VIEW_WIDTH=960, VIEW_HEIGHT=570;
    private Arena() { }

    static Rectangle visibleAround(Rectangle player, Rectangle result) {
        float left=player.x+player.width/2-VIEW_WIDTH/2;
        float bottom=player.y+player.height/2-VIEW_HEIGHT/2;
        return result.set(MathUtils.clamp(left,0,WIDTH-VIEW_WIDTH),
            MathUtils.clamp(bottom,0,HEIGHT-VIEW_HEIGHT),VIEW_WIDTH,VIEW_HEIGHT);
    }

    static Vector2 toWorld(Vector2 interfacePoint, Rectangle visible) {
        return interfacePoint.add(visible.x,visible.y);
    }

    static boolean contains(Rectangle entity) {
        return entity.x>=0 && entity.y>=0 && entity.x+entity.width<=WIDTH && entity.y+entity.height<=HEIGHT;
    }
}
