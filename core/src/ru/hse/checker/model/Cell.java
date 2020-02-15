package ru.hse.checker.model;



public class Cell {

    public enum Type {
        WHITE, BLACK
    }

    private Checker checker;

    public final Type type;

    public Cell(Type type) {
        this.type = type;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }
    public boolean hasChecker() {
        return checker != null;
    }
    public void removeChecker() {
        checker = null;
    }
    public Checker getChecker() { return checker; }
}
