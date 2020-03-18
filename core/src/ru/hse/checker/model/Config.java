package ru.hse.checker.model;

public class Config {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty = Difficulty.EASY;
    private boolean vsAI = true;

    public Config() { }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setVsAI(boolean vsAI) {
        this.vsAI = vsAI;
    }

    public boolean isVsAI() {return vsAI;}

    public Difficulty getDifficulty() {return difficulty;}
}
