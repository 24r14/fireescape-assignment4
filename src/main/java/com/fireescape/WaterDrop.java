package com.fireescape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** An enemy that keeps following the flame's current position. */
public class WaterDrop {
    static final float WIDTH = 27, HEIGHT = 45, ARRIVAL_TIME = 0.75f;
    final Rectangle bounds;
    private final Rectangle hitbox = new Rectangle();
    private float arrivalTime = ARRIVAL_TIME;
    private int health=2,maxHealth=2;

    WaterDrop(float x, float y) { bounds = new Rectangle(x, y, WIDTH, HEIGHT); }

    // Enemies enter outside the current camera view, not the distant map boundary.
    static WaterDrop atEdge(int side,float position,Rectangle view) {
        float fraction=MathUtils.clamp(position,0,1);
        switch(side) {
            case 0: return new WaterDrop(view.x-WIDTH-8,view.y+fraction*(view.height-HEIGHT));
            case 1: return new WaterDrop(view.x+view.width+8,view.y+fraction*(view.height-HEIGHT));
            case 2: return new WaterDrop(view.x+fraction*(view.width-WIDTH),view.y-HEIGHT-8);
            case 3: return new WaterDrop(view.x+fraction*(view.width-WIDTH),view.y+view.height+8);
            default: throw new IllegalArgumentException("Edge must be 0, 1, 2 or 3");
        }
    }

    void update(float delta, float speed, Rectangle target) {
        float activeTime = Math.max(0, delta);
        if (arrivalTime > 0) {
            float waiting = Math.min(arrivalTime, activeTime);
            arrivalTime -= waiting;
            activeTime -= waiting;
        }
        // Recalculate this direction every frame so turning the player redirects the enemy.
        float dx = target.x + target.width / 2 - (bounds.x + bounds.width / 2);
        float dy = target.y + target.height / 2 - (bounds.y + bounds.height / 2);
        float distance = Vector2.len(dx, dy);
        if (distance > 0 && activeTime > 0) {
            float step = Math.min(Math.max(0, speed) * activeTime, distance);
            bounds.x += dx / distance * step;
            bounds.y += dy / distance * step;
        }
    }

    void setMaxHealth(int value) { health=maxHealth=Math.max(1,value); }
    int health() { return health; }
    int maxHealth() { return maxHealth; }
    void takeDamage(int amount) { health=Math.max(0,health-Math.max(0,amount)); }
    boolean isDead() { return health<=0; }
    boolean isArriving() { return arrivalTime > 0; }
    Rectangle hitbox() { return hitbox.set(bounds.x + 5, bounds.y + 5, 17, 32); }
}
