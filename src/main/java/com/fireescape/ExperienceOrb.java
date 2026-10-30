package com.fireescape;

import com.badlogic.gdx.math.Vector2;

/** A defeated enemy leaves experience behind; moving near it attracts it to the flame. */
public class ExperienceOrb {
    float x,y;
    int value;
    ExperienceOrb(float x,float y,int value) { this.x=x; this.y=y; this.value=value; }
    boolean update(float delta,float targetX,float targetY,float radius) {
        float dx=targetX-x, dy=targetY-y;
        float distance=Vector2.len(dx,dy);
        if(distance<=22) return true;
        if(distance<=radius) {
            float step=Math.min(390*delta,distance);
            x+=dx/distance*step; y+=dy/distance*step;
            return distance-step<=22;
        }
        return false;
    }
}
