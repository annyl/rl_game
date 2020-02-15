package ru.hse.checker.model;

public class Logic {

    public enum DirX {
        LEFT, RIGHT
    }
    public enum DirY {
        UP, DOWN
    }

    public static class Path {
        public int count = 1;
        public DirX x;
        public DirY y;
    }

    public interface ICheckerChangeListener {
        void onPosChanged(Checker checker, int oldX, int oldY, int newX, int newY);
        void onUpToQueen(Checker checker, int x, int y);
    }

    private ICheckerChangeListener listener;

    public Path[] getAvailablePath( int x, int y) {

    }

    public void move(int newX, int newY) {
        int oldX = x;
        int oldY = y;
        x = newX;
        y = newY;
        listener.onPosChanged(oldX, oldY, x, y);
    }

    public void move(Path dir) {
        int dY = dir.y == Checker.DirY.UP ? count : -count;
        int dX = dir.x == Checker.DirX.RIGHT ? count : -count;
        int newX = x + dX;
        int newY = y + dY;
        move(newX, newY);
    }


    public void move(Path dir) {
        move(dir, 1);
    }

    public void hit(Path dir) {
        move(dir, 2);
    }
}
