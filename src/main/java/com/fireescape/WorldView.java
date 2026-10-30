package com.fireescape;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Follows the flame; HUD and menu rendering continue using the stationary UI camera. */
public class WorldView {
    final OrthographicCamera camera=new OrthographicCamera(FireEscape.WIDTH,FireEscape.HEIGHT);
    final Rectangle visible=new Rectangle();

    void follow(FirePlayer player) {
        Arena.visibleAround(player.bounds,visible);
        // The top 70 screen units belong to the HUD, so center the flame below it.
        camera.position.set(visible.x+FireEscape.WIDTH/2,visible.y+FireEscape.HEIGHT/2,0);
        camera.update();
    }

    Vector2 toWorld(Vector2 interfacePoint) { return Arena.toWorld(interfacePoint,visible); }
}
