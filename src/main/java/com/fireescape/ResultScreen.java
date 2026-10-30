package com.fireescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Rectangle;

public class ResultScreen extends ScreenAdapter {
    private final FireEscape game;
    private final int score,kills,level;
    private final Rectangle replay = new Rectangle(300, 230, 360, 60);
    private final Rectangle menu = new Rectangle(300, 150, 360, 60);
    public ResultScreen(FireEscape game,int score,int kills,int level) { this.game=game; this.score=score; this.kills=kills; this.level=level; }
    @Override public void resize(int width, int height) {
        if (width > 0 && height > 0) game.viewport.update(width, height, true);
    }
    @Override public void render(float delta) {
        boolean clicked = Gdx.input.justTouched();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || (clicked && replay.contains(game.pointer()))) {
            game.showScreen(new GameScreen(game)); return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (clicked && menu.contains(game.pointer()))) {
            game.showScreen(new MenuScreen(game)); return;
        }
        game.begin(); game.panel(190, 110, 580, 420);
        game.centered("GAME OVER - FLAME EXTINGUISHED", 485, 1.8f);
        game.centered("Lives remaining: 0 / 3", 453, 1.1f);
        game.centered("You survived for " + (score / 10f) + " seconds", 425, 1.4f);
        game.centered("Score: " + score + "    Best: " + game.bestScore, 375, 1.8f);
        game.centered("Defeated: "+kills+"    Level: "+level,330,1.3f);
        game.panel(replay.x, replay.y, replay.width, replay.height);
        game.centered("PLAY AGAIN  [ENTER]", 269, 1.5f);
        game.panel(menu.x, menu.y, menu.width, menu.height);
        game.centered("MAIN MENU  [ESC]", 189, 1.5f);
        game.batch.end();
    }
}
