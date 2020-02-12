package ru.hse.checker.model;

public class RawModel implements Logic.ICheckerChangeListener {
    private int[][] model = new int[Board.ROW][Board.COLUMN];

    public int[][] getModel() {
        return model;
    }

    @Override
    public void onPosChanged(Checker checker, int oldX, int oldY, int newX, int newY) {
        model[oldX][oldY] = 0;
        model[newX][newY] = checker.type == Checker.Type.WHITE ? 3 : 1;
    }

    @Override
    public void onUpToQueen(Checker checker, int x, int y) {
        model[x][y] = checker.type == Checker.Type.WHITE ? 4 : 2;
    }
}
