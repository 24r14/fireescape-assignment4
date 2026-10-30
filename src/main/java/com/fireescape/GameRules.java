package com.fireescape;

/** Survival time, difficulty, and the player's three lives. No drawing code. */
public class GameRules {
    public static final int MAX_LIVES = 3;
    public static final float INVULNERABILITY_SECONDS = 1.5f;
    private double seconds;
    private int lives = MAX_LIVES;
    private float invulnerability;

    public void update(float delta) {
        if (isOver()) return;
        float elapsed = Math.max(0, delta);
        seconds += elapsed;
        invulnerability = Math.max(0, invulnerability - elapsed);
    }
    public int score() { return (int) Math.floor(seconds * 10 + 0.00001); }
    public int level() { return 1 + (int) (seconds / 15); }
    public float speed() { return Math.min(230, 95 + 12 * (level() - 1)); }
    public float spawnInterval() { return Math.max(0.45f, 1.35f - 0.1f * (level() - 1)); }
    public int lives() { return lives; }
    public boolean isInvulnerable() { return invulnerability > 0; }

    /** Repeated contact cannot remove another life until the grace period ends. */
    public boolean hit() {
        if (isOver() || isInvulnerable()) return false;
        lives--;
        if (!isOver()) invulnerability = INVULNERABILITY_SECONDS;
        return true;
    }
    public boolean isOver() { return lives == 0; }
}
