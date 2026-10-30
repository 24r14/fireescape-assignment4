package com.fireescape;

public class GameChecks {
    static void require(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
    static boolean near(float a, float b) { return Math.abs(a-b) < 0.001f; }
    public static void main(String[] args) {
        GameRules rules = new GameRules();
        rules.update(15); require(rules.score()==150 && rules.level()==2, "score and difficulty");
        require(rules.speed()>95 && rules.spawnInterval()<1.35f, "harder level");
        require(rules.lives()==3, "new game has three lives");
        require(rules.hit() && rules.lives()==2 && !rules.isOver(), "first collision is survivable");
        for(int i=0;i<100;i++) require(!rules.hit(), "crowd contact cannot drain lives in one frame");
        rules.update(1.4f);
        require(rules.isInvulnerable() && !rules.hit() && rules.lives()==2, "grace period protects player");
        rules.update(-10);
        require(rules.isInvulnerable(), "negative delta cannot advance cooldown");
        rules.update(0.11f);
        require(!rules.isInvulnerable() && rules.hit() && rules.lives()==1, "second hit after grace period");
        rules.update(GameRules.INVULNERABILITY_SECONDS);
        require(rules.hit() && rules.isOver() && rules.lives()==0, "third valid hit ends game");
        int finalScore=rules.score();
        rules.update(10);
        require(rules.score()==finalScore && !rules.hit() && rules.lives()==0, "loss freezes score and prevents negative lives");
        GameRules replay=new GameRules();
        require(replay.lives()==3 && replay.score()==0 && !replay.isInvulnerable() && !replay.isOver(), "replay restores lives, score and cooldown");
        FirePlayer fire = new FirePlayer();
        float x = fire.bounds.x, y = fire.bounds.y;
        fire.move(1,0,30); require(near(fire.bounds.x,x+30), "right");
        fire.move(-1,0,30); require(near(fire.bounds.x,x), "left");
        fire.move(0,1,30); require(near(fire.bounds.y,y+30), "up");
        fire.move(0,-1,30); require(near(fire.bounds.y,y), "down");
        fire.move(1,1,30);
        require(near((float)Math.hypot(fire.bounds.x-x,fire.bounds.y-y),30), "diagonal speed");
        fire.move(1,1,10000); require(near(fire.bounds.x,Arena.WIDTH-84) && near(fire.bounds.y,Arena.HEIGHT-91), "top/right bounds");
        fire.move(-1,-1,10000); require(fire.bounds.x==0 && fire.bounds.y==0, "bottom/left bounds");
        WaterDrop water = new WaterDrop(25, 20);
        require(water.hitbox().overlaps(fire.hitbox()), "water collision");
        water.bounds.x=400; require(!water.hitbox().overlaps(fire.hitbox()), "avoided drop");
        com.badlogic.gdx.math.Rectangle target = new com.badlogic.gdx.math.Rectangle(200,200,0,0);
        WaterDrop chasing = new WaterDrop(0,0);
        chasing.update(0.5f,100,target);
        require(chasing.isArriving() && chasing.bounds.x==0 && chasing.bounds.y==0, "entry warning is stationary");
        chasing.update(0.25f,100,target);
        require(!chasing.isArriving(), "warning ends");
        chasing.update(0.1f,100,target);
        require(chasing.bounds.x>0 && chasing.bounds.y>0, "enemy chases up/right");
        require(near((float)Math.hypot(chasing.bounds.x,chasing.bounds.y),10), "pursuit speed independent of direction");
        x=chasing.bounds.x; y=chasing.bounds.y;
        target.set(-100,-100,0,0);
        chasing.update(0.1f,100,target);
        require(chasing.bounds.x<x && chasing.bounds.y<y, "enemy turns toward new player location");
        target.set(100,100,0,0);
        chasing.update(10,100,target);
        require(near(chasing.bounds.x+WaterDrop.WIDTH/2,100)
            && near(chasing.bounds.y+WaterDrop.HEIGHT/2,100), "enemy does not overshoot target");
        chasing.update(0.1f,100,target);
        require(Float.isFinite(chasing.bounds.x) && Float.isFinite(chasing.bounds.y), "zero-distance pursuit is safe");
        require(WaterDrop.atEdge(0,0.5f,new com.badlogic.gdx.math.Rectangle(0,0,960,570)).bounds.x<0, "left entry");
        require(WaterDrop.atEdge(1,0.5f,new com.badlogic.gdx.math.Rectangle(0,0,960,570)).bounds.x>960, "right entry");
        require(WaterDrop.atEdge(2,0.5f,new com.badlogic.gdx.math.Rectangle(0,0,960,570)).bounds.y<0, "bottom entry");
        require(WaterDrop.atEdge(3,0.5f,new com.badlogic.gdx.math.Rectangle(0,0,960,570)).bounds.y>570, "top entry");
        GameRules longGame=new GameRules(); longGame.update(3600);
        require(longGame.speed()<FirePlayer.SPEED && longGame.spawnInterval()>=0.45f, "difficulty limits remain playable");
        CameraChecks.run();
        CombatChecks.run();
        System.out.println("PASS: continuous pursuit, retargeting, safe entry, four edges, score, loss and player movement.");
    }
}
