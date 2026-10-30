package com.fireescape;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** The real combat simulation is independent of drawing, so tests can exercise it. */
public class CombatSystem {
    final Array<WaterDrop> enemies;
    final Array<FireBolt> bolts=new Array<>();
    final Array<ExperienceOrb> orbs=new Array<>();
    final Progression progression=new Progression();
    private float cooldown;
    private int kills;
    CombatSystem(Array<WaterDrop> enemies) { this.enemies=enemies; }
    int kills() { return kills; }

    WaterDrop nearestTarget(float x,float y) {
        WaterDrop nearest=null;
        float best=progression.attackRange*progression.attackRange;
        for(WaterDrop enemy:enemies) {
            if(enemy.isArriving() || enemy.isDead()) continue;
            float distance=Vector2.dst2(x,y,enemy.bounds.x+WaterDrop.WIDTH/2,enemy.bounds.y+WaterDrop.HEIGHT/2);
            if(distance<=best) { nearest=enemy; best=distance; }
        }
        return nearest;
    }

    boolean update(float delta,FirePlayer player,float enemySpeed) {
        if(progression.needsUpgrade()) return false;
        Rectangle target=player.hitbox();
        float centerX=target.x+target.width/2, centerY=target.y+target.height/2;
        for(WaterDrop enemy:enemies) enemy.update(delta,enemySpeed,target);
        cooldown=Math.max(0,cooldown-delta);
        if(cooldown==0) {
            WaterDrop enemy=nearestTarget(centerX,centerY);
            if(enemy!=null) {
                bolts.add(new FireBolt(centerX,centerY,enemy,progression.damage));
                cooldown=progression.attackInterval;
            }
        }
        for(int i=bolts.size-1;i>=0;i--) {
            FireBolt bolt=bolts.get(i); bolt.update(delta);
            WaterDrop hit=null;
            for(WaterDrop enemy:enemies) {
                if(!enemy.isArriving() && !enemy.isDead() && bolt.touches(enemy)) { hit=enemy; break; }
            }
            if(hit!=null) {
                hit.takeDamage(bolt.damage);
                if(hit.isDead()) {
                    kills++;
                    leaveExperience(hit);
                    enemies.removeValue(hit,true);
                }
            }
            if(hit!=null || bolt.life<=0) bolts.removeIndex(i);
        }
        // Collision still ends the run even if XP was nearby in the same frame.
        for(WaterDrop enemy:enemies)
            if(!enemy.isArriving() && enemy.hitbox().overlaps(target)) return true;
        for(int i=orbs.size-1;i>=0;i--) {
            ExperienceOrb orb=orbs.get(i);
            if(orb.update(delta,centerX,centerY,progression.pickupRadius)) {
                progression.collect(orb.value); orbs.removeIndex(i);
                if(progression.needsUpgrade()) break;
            }
        }
        return false;
    }
    private void leaveExperience(WaterDrop enemy) {
        float x=MathUtils.clamp(enemy.bounds.x+WaterDrop.WIDTH/2,12,Arena.WIDTH-12);
        float y=MathUtils.clamp(enemy.bounds.y+WaterDrop.HEIGHT/2,12,Arena.HEIGHT-12);
        // Merge nearby drops rather than losing XP or accumulating unlimited objects.
        for(ExperienceOrb orb:orbs) {
            if(Vector2.dst2(x,y,orb.x,orb.y)<24*24) { orb.value+=3; return; }
        }
        if(orbs.size>=150) {
            ExperienceOrb nearest=orbs.first();
            for(ExperienceOrb orb:orbs)
                if(Vector2.dst2(x,y,orb.x,orb.y)<Vector2.dst2(x,y,nearest.x,nearest.y)) nearest=orb;
            nearest.value+=3;
        } else orbs.add(new ExperienceOrb(x,y,3));
    }
}
