FireEscape - Assignment 4
=========================

A standalone Java/libGDX survival game. Control a flame, avoid pursuing water
enemies, fire automatically, and survive as long as possible.

RUN IN INTELLIJ IDEA
1. Download/clone this repository and open its root folder as a Gradle project.
2. Use JDK 17 or later for Gradle (tested with the installed IDEA JDK 25).
3. Wait for Gradle dependencies to download. Internet is needed on the first build.
4. Run Gradle > Tasks > application > run, or select the FireEscape configuration.
Windows terminal alternative: gradlew.bat run
macOS/Linux: sh gradlew run (desktop graphics required).
Verification: gradlew.bat check
Build a runnable jar: gradlew.bat fatJar
Run it: java -jar build/libs/FireEscape.jar
On macOS, add -XstartOnFirstThread before -jar.

HOW TO PLAY
Enter or click START to begin. Move with WASD or the arrow keys, including
diagonals, or hold the left mouse button to move toward the pointer.
The camera follows you across a 3840 x 2560 arena. The HUD stays fixed.
P pauses/resumes. Esc returns to the menu. Enter on Game Over starts a fresh run.

Water enemies spawn at random positions just outside the current camera view,
inside the arena. A short flashing warning shows where they will enter.
They continuously change direction to chase the player's current position.
Several enemies can pursue you together (up to 80 active enemies).

You start each run with THREE LIVES. Touching an active enemy removes one life.
After a hit, the flame flashes and is protected for 1.5 seconds of gameplay.
This prevents overlapping enemies from removing all lives immediately.
The top-right LIVES counter and three bars show remaining health.
At zero lives the Game Over screen shows your score, kills and level.
Replay restores three lives and resets the run. Pause and upgrade selection
freeze survival time, enemy movement, animation and the damage grace period.

Earn 10 points per second alive. A wave starts every 15 seconds; new waves
increase enemy speed and spawn frequency, with limits to keep them playable.
Best score lasts for the current app session and is not saved after closing.

The flame automatically shoots homing fireballs at the nearest enemy in range.
Enemies begin with 2 HP; their starting HP increases every fourth wave.
Defeated enemies drop 3 XP as green lights. Move near them to collect XP.
The first upgrade costs 9 XP; later costs increase by 6. Extra XP is retained.
Choose one of three random upgrades with 1/2/3 or by clicking a card:
- Hotter flames: +1 damage.
- Rapid fire: 18% shorter attack interval, down to 0.12 seconds.
- Long reach: +55 attack range.
- Ember magnet: +40 XP pickup radius.
- Light feet: +30 movement speed, up to 510.
Upgrades, XP and kills reset when you replay. Enemies left far behind retire
without awarding XP or kills; XP drops stay at their world positions.

ASSETS AND ANIMATION
The custom fire, water and background artwork was supplied for this project.
Original PNG files are preserved. Black backgrounds are hidden at drawing time.
assets/fire-animation/frame-1.png through frame-4.png are the four fire poses,
copied from bead-pattern (4).png, (3).png, (2).png and bead-pattern.png in order.
FlameAnimation uses libGDX Animation and TextureRegion to play 1,2,3,4,3,2
repeatedly, with 0.15 seconds per frame. A shared crop keeps the wood base steady.
The flame animates both while idle and while moving; it also animates on the menu.
The original background tiles at fixed positions so it scrolls with the camera.
Fireballs, XP lights, health bars and interface panels are drawn in code.

CODE GUIDE
DesktopLauncher: creates the desktop window and starts the game.
FireEscape: owns shared graphics and changes between screens.
MenuScreen: title, animated flame, instructions and start button.
GameScreen: input, random enemy spawning, gameplay updates, HUD and pause.
FirePlayer: movement and a smaller collision rectangle inside the artwork.
WaterDrop: warning period, enemy health, and normalized pursuit movement.
GameRules: survival score, difficulty, player lives and damage protection timer.
ResultScreen: Game Over, score, replay and return-to-menu buttons.
FlameAnimation: four-frame animation and texture disposal.
CombatSystem: target selection, fireballs, collisions, kills and XP collection.
FireBolt: homing projectile movement and swept collision checks.
ExperienceOrb: experience drops and pickup attraction.
Progression: XP thresholds, upgrade selection and player statistics.
Arena: world dimensions, visible area and coordinate conversion.
WorldView: following camera, separate from the fixed HUD camera.

In each active frame, delta time controls movement and timers. GameScreen moves
the player, follows with the camera, spawns enemies and updates CombatSystem.
CombatSystem reports player contact; GameRules.hit() removes a life only when
the protection timer has expired. At zero lives GameScreen opens ResultScreen.
Keyboard direction is normalized so diagonals do not move faster. Enemy pursuit
uses the vector from each water drop to the player and recalculates it every frame.

ASSIGNMENT 4 CHECKLIST
- Personalized assets: custom flame, water and background PNGs in assets/.
- Animations: four fire frames, ping-pong playback.
- Multiple classes: separate player, enemy, rules, combat and screen classes.
- Multiple random enemies: random edge positions and timed spawning.
- Game Over scenario: zero lives opens the result screen.
- Enemies chase the player: continuous pursuit and retargeting.
- Point and life systems: survival score, three lives, health display and grace period.
- README: controls, rules, running instructions and code explanation above.

BUILD AND CHECKS
Gradle wrapper and build.gradle are included. libGDX 1.14.2 is downloaded from
Maven Central. No absolute local paths or IDE installation are required.
gradlew.bat check runs GameChecks, CombatChecks and CameraChecks. They cover
life loss, contact protection, Game Over, replay reset, score, player movement,
pursuit, camera bounds, combat, XP and upgrades without opening a window.

References: libGDX framework; survival gameplay inspiration:
https://blazegamesuk.itch.io/slime-survivor
This project does not depend on the earlier Drop or Rainkeeper projects.
