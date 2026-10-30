# FireEscape — Assignment 4

A Java/libGDX survival game: move a four-frame animated flame, avoid pursuing
water enemies, and survive with three lives. Automatic fire, XP upgrades and
a following camera extend the gameplay.

## Play

- **WASD / arrows:** move. You can also hold the left mouse button.
- **Enter:** start or replay. **P:** pause. **Esc:** main menu.
- **1 / 2 / 3:** choose an upgrade when you level up.
- Enemy contact costs one life, followed by 1.5 seconds of protection.
- Survive to earn points. Zero lives means Game Over.

## Run

Open this folder in IntelliJ IDEA as a Gradle project with JDK 17 or later.
Run the **FireEscape** configuration or **Gradle → application → run**.
The first build needs internet access to download dependencies.

On Windows: `gradlew.bat run`. On macOS/Linux: `sh gradlew run`.
Run checks with `gradlew.bat check`; build a runnable jar with `gradlew.bat fatJar`.

See [README.txt](README.txt) for full gameplay instructions, a class-by-class
code guide, asset details and the Assignment 4 requirement checklist.
