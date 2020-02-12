package ru.hse.checker.model;

public class Config {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    private boolean isPlayerForWhites;

    public Config(Difficulty difficulty, boolean isPlayerForWhites) {
        this.difficulty = difficulty;
        this.isPlayerForWhites = isPlayerForWhites;
    }

    public Difficulty getDifficulty() {return difficulty;}
    public boolean isPlayerForWhites() {return isPlayerForWhites;}
}
