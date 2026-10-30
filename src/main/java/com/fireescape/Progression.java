package com.fireescape;

import com.badlogic.gdx.utils.Array;

/** Experience and upgrades belong to this run and reset when the player restarts. */
public class Progression {
    public enum Upgrade {
        POWER("Hotter flames", "+1 fireball damage", "Defeat tougher water faster"),
        HASTE("Rapid fire", "Fire 18% faster", "More fireballs per second"),
        REACH("Long reach", "+55 attack range", "Attack enemies earlier"),
        MAGNET("Ember magnet", "+40 pickup radius", "Collect XP from farther away"),
        SWIFTNESS("Light feet", "+30 movement speed", "Escape the crowd more easily");
        final String title, effect, detail;
        Upgrade(String title, String effect, String detail) {
            this.title=title; this.effect=effect; this.detail=detail;
        }
    }
    private int level=1, experience;
    int damage=1;
    float attackInterval=0.60f, attackRange=240, pickupRadius=100, movementSpeed=FirePlayer.SPEED;

    public int level() { return level; }
    public int experience() { return experience; }
    public int requiredExperience() { return 9 + (level-1)*6; }
    public boolean needsUpgrade() { return experience>=requiredExperience(); }
    public void collect(int amount) { if(amount>0) experience+=amount; }
    boolean available(Upgrade upgrade) {
        return upgrade!=null && !(upgrade==Upgrade.HASTE && attackInterval<=0.12f)
            && !(upgrade==Upgrade.SWIFTNESS && movementSpeed>=510);
    }
    Array<Upgrade> choices() {
        Array<Upgrade> result=new Array<>();
        for(Upgrade upgrade:Upgrade.values()) if(available(upgrade)) result.add(upgrade);
        result.shuffle(); result.truncate(3); return result;
    }
    public boolean choose(Upgrade upgrade) {
        if(!needsUpgrade() || !available(upgrade)) return false;
        experience-=requiredExperience(); level++;
        switch(upgrade) {
            case POWER: damage++; break;
            case HASTE: attackInterval=Math.max(0.12f,attackInterval*0.82f); break;
            case REACH: attackRange+=55; break;
            case MAGNET: pickupRadius+=40; break;
            case SWIFTNESS: movementSpeed=Math.min(510,movementSpeed+30); break;
        }
        return true;
    }
}
