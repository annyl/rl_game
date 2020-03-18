package ru.hse.checker.model;

public class Checker {

    public enum Type {
        WHITE, BLACK
    }

    public final Type type;
    private boolean isQueen;
    private Cell cell;

    public Checker(Type type, Cell cell) {
        this.type = type;
        this.cell = cell;
    }

    public void upToQueen() {
        isQueen = true;
    }

    public Cell getCell() { return cell; }
    public void setCell(Cell cell) { this.cell = cell; }

    public boolean isQueen() {
        return isQueen;
    }

    public boolean isOpposite(Checker checker) {
        return checker.type != this.type;
    }
}
