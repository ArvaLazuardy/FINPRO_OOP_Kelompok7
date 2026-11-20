package com.mrpororo.systems;

import com.mrpororo.levels.BossLevel;
import com.mrpororo.levels.Level;
import com.mrpororo.levels.RunGunLevel;
import com.mrpororo.levels.TestLevel;

public class LevelManager {
    public enum LevelType { TEST, RUN_GUN, BOSS }
    private Level currentLevel;
    private LevelType currentType;

    public void load(LevelType type) {
        if (currentLevel != null) currentLevel.dispose();
        currentType = type;
        switch (type) {
            case TEST:
                currentLevel = new TestLevel();
                break;
            case RUN_GUN:
                currentLevel = new RunGunLevel();
                break;
            case BOSS:
                currentLevel = new BossLevel();
                break;
            default:
                throw new IllegalArgumentException("Unknown level: " + type);
        }
        currentLevel.load();
    }
    public void nextLevel() {
        if (currentType == LevelType.RUN_GUN) load(LevelType.BOSS);
    }
    public Level getCurrentLevel() { return currentLevel; }
    public LevelType getCurrentType() { return currentType; }
    public void dispose() { if (currentLevel != null) currentLevel.dispose(); }
}
