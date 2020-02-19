package ru.hse.checker.model;



public class Cell {

    public enum Type {
        WHITE, BLACK
    }

    private Checker checker;

    //x - vertical axis, y - horizontal axis
    public final int x;
    public final int y;

    public final Type type;

    public Cell(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
        checker.setCell(this);
    }
    public boolean hasChecker() {
        return checker != null;
    }

    public void removeChecker() {
        checker.setCell(null);
        checker = null;
    }
    public Checker getChecker() { return checker; }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
