package com.fireescape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FirePlayer {
    public final Rectangle bounds = new Rectangle(Arena.WIDTH/2-42, Arena.HEIGHT/2-45.5f, 84, 91);
    private final Rectangle hitbox = new Rectangle();
    public static final float SPEED = 330;

    public void move(float dx, float dy, float distance) {
        float length = Vector2.len(dx, dy);
        // Normalize so diagonal movement is not faster than straight movement.
        if (length > 0) { bounds.x += dx / length * distance; bounds.y += dy / length * distance; }
        bounds.x = MathUtils.clamp(bounds.x, 0, Arena.WIDTH - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y, 0, Arena.HEIGHT - bounds.height);
    }
    public Rectangle hitbox() {
        // A smaller central hitbox makes the irregular flame artwork fair to play.
        return hitbox.set(bounds.x + 23, bounds.y + 12, 38, 59);
    }
}
