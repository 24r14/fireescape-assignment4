package com.fireescape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** A short-lived homing fireball. Each shot can damage only one enemy. */
public class FireBolt {
    float x,y,previousX,previousY,velocityX,velocityY,life=1.6f;
    final int damage;
    final WaterDrop target;
    FireBolt(float x,float y,WaterDrop target,int damage) {
        this.x=x; this.y=y; previousX=x; previousY=y;
        this.target=target; this.damage=damage;
    }
    void update(float delta) {
        previousX=x; previousY=y;
        if(!target.isDead()) {
            float dx=target.bounds.x+WaterDrop.WIDTH/2-x;
            float dy=target.bounds.y+WaterDrop.HEIGHT/2-y;
            float distance=Vector2.len(dx,dy);
            if(distance>0) { velocityX=dx/distance*620; velocityY=dy/distance*620; }
        }
        x+=velocityX*delta; y+=velocityY*delta; life-=delta;
    }
    boolean touches(WaterDrop enemy) {
        // Check the entire movement segment so fast shots cannot jump through enemies.
        float cx=enemy.bounds.x+WaterDrop.WIDTH/2, cy=enemy.bounds.y+WaterDrop.HEIGHT/2;
        float dx=x-previousX, dy=y-previousY;
        float lengthSquared=dx*dx+dy*dy;
        float t=lengthSquared==0 ? 0 : MathUtils.clamp(((cx-previousX)*dx+(cy-previousY)*dy)/lengthSquared,0,1);
        return Vector2.dst2(previousX+t*dx,previousY+t*dy,cx,cy)<=20*20;
    }
}
