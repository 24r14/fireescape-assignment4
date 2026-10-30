package com.fireescape;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import static com.fireescape.GameChecks.require;
import static com.fireescape.GameChecks.near;

public class CameraChecks {
    static void run() {
        FirePlayer player=new FirePlayer();
        Rectangle before=Arena.visibleAround(player.bounds,new Rectangle());
        require(near(player.bounds.x+42-before.x,480) && near(player.bounds.y+45.5f-before.y,285),
            "flame centered in the playfield below the fixed HUD");
        player.move(1,0,125);
        Rectangle after=Arena.visibleAround(player.bounds,new Rectangle());
        require(near(after.x-before.x,125) && near(after.y,before.y),"camera follows rightward keyboard movement");
        player.move(0,-1,90);
        Rectangle lower=Arena.visibleAround(player.bounds,new Rectangle());
        require(near(lower.y-after.y,-90),"camera follows vertical movement");
        Vector2 cursor=Arena.toWorld(new Vector2(480,285),lower);
        require(near(cursor.x,player.bounds.x+42) && near(cursor.y,player.bounds.y+45.5f),
            "mouse targets convert to world coordinates after camera moves");
        player.move(-1,-1,20000);
        Rectangle bottom=Arena.visibleAround(player.bounds,new Rectangle());
        require(bottom.x==0 && bottom.y==0,"camera stops at lower map edges");
        player.move(1,1,20000);
        Rectangle top=Arena.visibleAround(player.bounds,new Rectangle());
        require(near(top.x+top.width,Arena.WIDTH) && near(top.y+top.height,Arena.HEIGHT),
            "upper edges show no blank area and keep player below HUD");
        Rectangle offset=new Rectangle(1400,900,960,570);
        for(int side=0;side<4;side++) {
            WaterDrop enemy=WaterDrop.atEdge(side,0.5f,offset);
            require(!offset.overlaps(enemy.bounds),"enemy spawns beyond scrolled view");
            require(Arena.contains(enemy.bounds),"interior-view spawns remain in the world");
        }
        require(!Arena.contains(WaterDrop.atEdge(0,0.5f,bottom).bounds),"map-edge spawns rejected rather than outside world");

        Array<WaterDrop> enemies=new Array<>();
        WaterDrop distant=CombatChecks.ready(1800,1400,1); enemies.add(distant);
        CombatSystem combat=new CombatSystem(enemies);
        combat.bolts.add(new FireBolt(1795,1422.5f,distant,1));
        FirePlayer collector=new FirePlayer(); collector.bounds.setPosition(1500,1400);
        combat.update(0.01f,collector,0);
        require(combat.orbs.size==1 && combat.orbs.first().x>1700 && combat.orbs.first().y>1300,
            "XP stays at the kill location in the enlarged world");
        System.out.println("PASS: camera follow, world boundaries, mouse conversion, visible-edge spawning and world-space XP.");
    }
}
