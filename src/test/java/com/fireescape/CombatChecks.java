package com.fireescape;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import static com.fireescape.GameChecks.require;
import static com.fireescape.GameChecks.near;

public class CombatChecks {
    static WaterDrop ready(float x,float y,int health) {
        WaterDrop enemy=new WaterDrop(x,y);
        enemy.setMaxHealth(health);
        enemy.update(WaterDrop.ARRIVAL_TIME,0,new Rectangle());
        return enemy;
    }
    public static void run() {
        Progression stats=new Progression();
        require(!stats.choose(Progression.Upgrade.POWER),"no free upgrade");
        stats.collect(8); require(!stats.needsUpgrade(),"upgrade threshold");
        stats.collect(20); require(stats.needsUpgrade(),"XP triggers upgrade");
        require(stats.choose(Progression.Upgrade.POWER),"select damage upgrade");
        require(stats.level()==2 && stats.experience()==19 && stats.damage==2,"carry excess XP and apply damage");
        require(stats.needsUpgrade(),"extra XP may queue another choice");
        stats.choose(Progression.Upgrade.HASTE);
        require(stats.level()==3 && stats.experience()==4 && stats.attackInterval<0.6f,"second level and faster fire");
        require(!stats.choose(Progression.Upgrade.MAGNET),"cannot choose twice for one level");
        for(Progression.Upgrade upgrade:Progression.Upgrade.values()) {
            Progression one=new Progression(); one.collect(9); one.choose(upgrade);
            require(one.level()==2,"all upgrade options apply");
            if(upgrade==Progression.Upgrade.REACH) require(one.attackRange==295,"range upgrade");
            if(upgrade==Progression.Upgrade.MAGNET) require(one.pickupRadius==140,"magnet upgrade");
            if(upgrade==Progression.Upgrade.SWIFTNESS) require(one.movementSpeed==360,"speed upgrade");
        }
        Progression capped=new Progression();
        for(int i=0;i<30;i++) {
            capped.collect(capped.requiredExperience());
            if(capped.available(Progression.Upgrade.HASTE)) capped.choose(Progression.Upgrade.HASTE);
            else capped.choose(Progression.Upgrade.SWIFTNESS);
        }
        Array<Progression.Upgrade> offers=capped.choices();
        require(offers.size==3 && offers.get(0)!=offers.get(1) && offers.get(1)!=offers.get(2)
            && offers.get(0)!=offers.get(2),"three unique useful offers even after caps");
        require(!offers.contains(Progression.Upgrade.HASTE,true),"capped fire rate not offered");

        FirePlayer player=new FirePlayer(); player.bounds.setPosition(0,0);
        Array<WaterDrop> enemies=new Array<>();
        CombatSystem system=new CombatSystem(enemies);
        WaterDrop far=ready(700,40,2), close=ready(145,40,2);
        enemies.add(far); enemies.add(close); enemies.add(new WaterDrop(70,40));
        require(system.nearestTarget(42,42)==close,"nearest target ignores arriving enemies and range");
        enemies.removeIndex(2);
        system.update(0.01f,player,0);
        require(system.bolts.size==1 && system.bolts.first().target==close,"automatic shot at nearest enemy");
        for(int i=0;i<60;i++) system.update(0.05f,player,0);
        require(system.kills()==1 && !enemies.contains(close,true),"fireballs defeat an enemy");
        require(system.orbs.size==1 && system.orbs.first().value==3,"one kill drops exactly 3 XP");
        require(system.progression.experience()==0,"distant XP not collected automatically");
        player.bounds.setPosition(system.orbs.first().x-42,system.orbs.first().y-41.5f);
        system.update(0.05f,player,0);
        require(system.progression.experience()==3 && system.orbs.size==0,"nearby XP collected exactly once");

        ExperienceOrb orb=new ExperienceOrb(90,0,3);
        require(!orb.update(0.1f,0,0,50) && orb.x==90,"outside magnet radius stays put");
        require(!orb.update(0.1f,0,0,100) && orb.x<90,"magnet attracts XP");
        require(orb.update(1,0,0,100),"attracted XP reaches pickup without overshoot");

        system.progression.collect(6);
        float enemyX=far.bounds.x;
        system.update(10,player,200);
        require(near(far.bounds.x,enemyX) && system.progression.experience()==9,"upgrade freezes combat and XP");
        system.progression.choose(Progression.Upgrade.POWER);
        system.update(0.1f,player,100);
        require(far.bounds.x<enemyX,"combat resumes after upgrade");

        Array<WaterDrop> collisionEnemies=new Array<>();
        WaterDrop overlap=ready(player.bounds.x+20,player.bounds.y+20,100);
        collisionEnemies.add(overlap);
        CombatSystem collision=new CombatSystem(collisionEnemies);
        require(collision.update(0.01f,player,0),"surviving contact still ends run");
        WaterDrop sweptTarget=ready(150,0,2);
        FireBolt fast=new FireBolt(0,22.5f,sweptTarget,1);
        fast.update(0.5f); require(fast.touches(sweptTarget),"fast projectile cannot tunnel through enemy");
        Array<WaterDrop> doubleEnemies=new Array<>(); WaterDrop fragile=ready(100,200,1); doubleEnemies.add(fragile);
        CombatSystem doubleShot=new CombatSystem(doubleEnemies);
        doubleShot.bolts.add(new FireBolt(95,222.5f,fragile,1));
        doubleShot.bolts.add(new FireBolt(95,222.5f,fragile,1));
        doubleShot.update(0.01f,new FirePlayer(),0);
        require(doubleShot.kills()==1 && doubleShot.orbs.size==1,"two shots cannot duplicate kill rewards");
        CombatSystem fresh=new CombatSystem(new Array<>());
        require(fresh.progression.level()==1 && fresh.progression.experience()==0 && fresh.kills()==0,
            "new run resets all progression");
        System.out.println("PASS: automatic targeting, hits, kills, XP drop/pickup, level choices, caps, freeze/resume and run reset.");
    }
}
