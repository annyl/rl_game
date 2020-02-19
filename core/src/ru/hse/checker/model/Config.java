package ru.hse.checker.model;

public class Config {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    private boolean vsAI = true;

    public Config(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    public Config(Difficulty difficulty, boolean twoUserPlayers) {
        this(difficulty);
        if (twoUserPlayers)
            this.vsAI = false;
    }

    public Difficulty getDifficulty() {return difficulty;}
}
