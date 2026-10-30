package com.fireescape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends ScreenAdapter {
    private final FireEscape game;
    private float animationTime;
    private final Rectangle start = new Rectangle(330, 246, 300, 60);
    public MenuScreen(FireEscape game) { this.game = game; }
    @Override public void resize(int width, int height) {
        if (width > 0 && height > 0) game.viewport.update(width, height, true);
    }
    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || (Gdx.input.justTouched() && start.contains(game.pointer()))) {
            game.showScreen(new GameScreen(game)); return;
        }
        animationTime+=Math.max(0,Math.min(delta,0.1f));
        game.begin();
        game.panel(150, 100, 660, 445);
        game.centered("FIRE ESCAPE", 505, 3.1f);
        game.drawFlame(animationTime,434,340,92,100);
        game.panel(start.x, start.y, start.width, start.height);
        game.centered("START  [ENTER]", 285, 1.8f);
        game.centered("Move to survive. Your flame fires automatically!", 220, 1.25f);
        game.centered("WASD / arrows: move. The camera follows you.", 188, 1.25f);
        game.centered("Collect green XP. Level up and choose a power!", 157, 1.25f);
        game.centered("3 lives. Flashing = protected. P: pause   Esc: menu", 126, 1.2f);
        game.batch.end();
    }
}
