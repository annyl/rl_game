package ru.hse.checker.model;

public class Checker {
    public enum Type {
        WHITE, BLACK
    }

    public final Type type;
    private boolean isQueen;

    public Checker(Type type) {
        this.type = type;
    }

    public void upToQueen() {
        isQueen = true;
    }

    public boolean isQueen() {
        return isQueen;
    }
}
