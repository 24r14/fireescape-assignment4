package com.fireescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends ScreenAdapter {
    private final FireEscape game;
    private final FirePlayer player=new FirePlayer();
    private final GameRules rules=new GameRules();
    private final WorldView worldView=new WorldView();
    private final Array<WaterDrop> drops=new Array<>();
    private final CombatSystem combat=new CombatSystem(drops);
    private Array<Progression.Upgrade> choices;
    private float spawnTimer,visualTime;
    private boolean paused,ignoreMouseUntilReleased;
    public GameScreen(FireEscape game) {
        this.game=game;
        worldView.follow(player);
        spawnEnemy(); spawnEnemy();
    }
    @Override public void resize(int width,int height) {
        if(width>0 && height>0) game.viewport.update(width,height,true);
    }
    @Override public void pause() { paused=true; }
    @Override public void render(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { game.showScreen(new MenuScreen(game)); return; }
        if(combat.progression.needsUpgrade()) {
            if(choices==null) choices=combat.progression.choices();
            handleUpgradeInput();
        } else {
            if(Gdx.input.isKeyJustPressed(Input.Keys.P)) paused=!paused;
            if(!paused) update(Math.min(delta,0.05f));
        }
        if(rules.isOver()) {
            game.bestScore=Math.max(game.bestScore,rules.score());
            game.showScreen(new ResultScreen(game,rules.score(),combat.kills(),combat.progression.level())); return;
        }
        if(combat.progression.needsUpgrade() && choices==null) choices=combat.progression.choices();
        draw();
    }
    private void update(float delta) {
        movePlayer(delta);
        worldView.follow(player);
        retireDistantEnemies();
        rules.update(delta); visualTime+=delta;
        spawnTimer+=delta;
        while(spawnTimer>=rules.spawnInterval()) { spawnTimer-=rules.spawnInterval(); spawnEnemy(); }
        if(combat.update(delta,player,rules.speed())) rules.hit();
    }
    private void handleUpgradeInput() {
        int selected=-1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selected=0;
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selected=1;
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selected=2;
        if(Gdx.input.justTouched()) {
            Vector2 pointer=game.pointer();
            for(int i=0;i<choices.size;i++) if(card(i).contains(pointer)) selected=i;
        }
        if(selected>=0 && selected<choices.size && combat.progression.choose(choices.get(selected))) {
            choices=null;
            // Clicking a card must not accidentally move the player into an enemy.
            ignoreMouseUntilReleased=true;
        }
    }
    private Rectangle card(int index) { return new Rectangle(100+260*index,208,240,150); }
    private void draw() {
        game.beginWorld(worldView);
        for(ExperienceOrb orb:combat.orbs) {
            float glow=15+2*MathUtils.sin(visualTime*4);
            game.block(orb.x-glow/2,orb.y-glow/2,glow,glow,0.1f,0.65f,0.35f,0.4f);
            game.block(orb.x-5,orb.y-5,10,10,0.35f,1,0.65f,1);
            game.block(orb.x-2,orb.y-2,4,4,0.9f,1,0.8f,1);
        }
        // Flash during the grace period without changing the supplied sprite images.
        if(rules.isInvulnerable() && (int)(visualTime*10)%2==0)
            game.batch.setColor(1,1,1,0.4f);
        game.drawFlame(visualTime,player.bounds.x,player.bounds.y,player.bounds.width,player.bounds.height);
        game.batch.setColor(1,1,1,1);
        for(WaterDrop drop:drops) {
            float x=drop.bounds.x,y=drop.bounds.y;
            if(drop.isArriving()) {
                game.batch.setColor(1,1,1,((int)(visualTime*10)%2==0)?0.4f:0.8f);
                x=MathUtils.clamp(x,worldView.visible.x,worldView.visible.x+Arena.VIEW_WIDTH-WaterDrop.WIDTH);
                y=MathUtils.clamp(y,worldView.visible.y,worldView.visible.y+Arena.VIEW_HEIGHT-WaterDrop.HEIGHT);
            }
            game.sprite(game.water,x,y,drop.bounds.width,drop.bounds.height);
            game.batch.setColor(1,1,1,1);
            if(!drop.isArriving() && drop.health()<drop.maxHealth()) {
                game.block(x,y+47,27,4,0.2f,0.1f,0.1f,1);
                game.block(x,y+47,27f*drop.health()/drop.maxHealth(),4,0.2f,0.85f,1,1);
            }
        }
        for(FireBolt bolt:combat.bolts) {
            game.block(bolt.x-7,bolt.y-7,14,14,1,0.3f,0.02f,0.65f);
            game.block(bolt.x-4,bolt.y-4,8,8,1,0.85f,0.25f,1);
        }
        game.batch.end();
        game.beginInterface();
        Progression stats=combat.progression;
        game.panel(0,570,960,70);
        game.text("SCORE "+rules.score(),24,621,1.45f);
        game.text("WAVE "+rules.level(),210,621,1.3f);
        game.text("LEVEL "+stats.level(),360,621,1.3f);
        game.text("DEFEATED "+combat.kills(),510,621,1.3f);
        game.text("WASD / arrows: move",24,598,1);
        game.text("XP  "+stats.experience()+" / "+stats.requiredExperience(),310,598,1);
        game.text("Auto fire   P: pause   Esc: menu",510,598,0.85f);
        game.text("LIVES "+rules.lives()+" / "+GameRules.MAX_LIVES,780,621,1.2f);
        for(int i=0;i<GameRules.MAX_LIVES;i++) {
            boolean alive=i<rules.lives();
            game.block(780+i*48,591,40,12,alive?1f:0.3f,alive?0.48f:0.22f,0.12f,1);
        }
        game.block(24,578,912,7,0.2f,0.25f,0.2f,1);
        game.block(24,578,912*Math.min(1f,(float)stats.experience()/stats.requiredExperience()),7,0.35f,1,0.65f,1);
        if(stats.needsUpgrade()) {
            game.panel(0,0,960,570); game.panel(70,170,820,300);
            game.centered("LEVEL "+(stats.level()+1)+" - CHOOSE AN UPGRADE",436,1.9f);
            game.centered("Game paused. Click a card or press 1, 2 or 3.",393,1.2f);
            for(int i=0;i<choices.size;i++) {
                Rectangle box=card(i); Progression.Upgrade option=choices.get(i);
                game.block(box.x,box.y,box.width,box.height,0.35f,0.23f,0.12f,1);
                game.block(box.x,box.y+box.height-4,box.width,4,1,0.65f,0.2f,1);
                game.text("["+(i+1)+"] "+option.title,box.x+14,box.y+117,1.15f);
                game.text(option.effect,box.x+14,box.y+76,1.1f);
                game.text(option.detail,box.x+14,box.y+43,0.85f);
            }
        } else if(paused) {
            game.panel(230,260,500,130); game.centered("PAUSED",350,2.3f);
            game.centered("Press P to continue",305,1.4f);
        }
        game.batch.end();
    }
    private void spawnEnemy() {
        if(drops.size>=80) return;
        for(int attempt=0;attempt<12;attempt++) {
            WaterDrop enemy=WaterDrop.atEdge(MathUtils.random(3),MathUtils.random(),worldView.visible);
            if(!Arena.contains(enemy.bounds)) continue;
            float dx=enemy.bounds.x+WaterDrop.WIDTH/2-(player.bounds.x+player.bounds.width/2);
            float dy=enemy.bounds.y+WaterDrop.HEIGHT/2-(player.bounds.y+player.bounds.height/2);
            if(dx*dx+dy*dy>=200*200) {
                enemy.setMaxHealth(2+(rules.level()-1)/4); drops.add(enemy); return;
            }
        }
    }
    private void retireDistantEnemies() {
        // Do not let enemies far behind the player consume every spawn slot.
        // Leaving them behind awards no kills or experience.
        float x=player.bounds.x+player.bounds.width/2,y=player.bounds.y+player.bounds.height/2;
        for(int i=drops.size-1;i>=0;i--) {
            WaterDrop enemy=drops.get(i);
            if(Vector2.dst2(x,y,enemy.bounds.x+WaterDrop.WIDTH/2,enemy.bounds.y+WaterDrop.HEIGHT/2)>1800*1800) {
                enemy.takeDamage(enemy.maxHealth());
                drops.removeIndex(i);
            }
        }
    }
    private void movePlayer(float delta) {
        float dx=0,dy=0;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.A)) dx--;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)||Gdx.input.isKeyPressed(Input.Keys.D)) dx++;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||Gdx.input.isKeyPressed(Input.Keys.S)) dy--;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)||Gdx.input.isKeyPressed(Input.Keys.W)) dy++;
        float distance=combat.progression.movementSpeed*delta;
        boolean mouseDown=Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        if(!mouseDown) ignoreMouseUntilReleased=false;
        if(mouseDown && !ignoreMouseUntilReleased) {
            Vector2 pointer=game.pointer();
            // Ignore clicks on the HUD or the letterbox outside the playfield.
            if(pointer.x<0 || pointer.x>Arena.VIEW_WIDTH || pointer.y<0 || pointer.y>Arena.VIEW_HEIGHT) {
                player.move(dx,dy,distance); return;
            }
            worldView.toWorld(pointer);
            dx=pointer.x-player.bounds.x-player.bounds.width/2;
            dy=pointer.y-player.bounds.y-player.bounds.height/2;
            distance=Math.min(distance,Vector2.len(dx,dy));
        }
        player.move(dx,dy,distance);
    }
}
