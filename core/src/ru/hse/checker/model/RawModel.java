package ru.hse.checker.model;

public class RawModel implements Board.ICheckerChangeListener {
    private int[][] model = new int[Board.ROW][Board.COLUMN];

    public int[][] getModel() {
        return model;
    }

    @Override
    public void onPosChanged(Cell oldCell, Cell newCell) {
        model[oldCell.x][oldCell.y] = 0;
        model[newCell.x][newCell.y] = newCell.getChecker().type == Checker.Type.WHITE ? 3 : 1;
    }

    @Override
    public void onUpToQueen(Cell cell) {
        model[cell.x][cell.y] = cell.getChecker().type == Checker.Type.WHITE ? 4 : 2;
    }

    @Override
    public void onCreated(Cell cell) {
        model[cell.x][cell.y] = cell.getChecker().type == Checker.Type.WHITE ? 3 : 1;
    }

    @Override
    public void onRemoved(Cell cell) {
        model[cell.x][cell.y] = 0;
    }

    void printModel() {
        for (int i = Board.ROW - 1; i >= 0; i--) {
            for (int j = 0; j < Board.COLUMN; j++) {
                System.out.print(model[i][j]);
            }
            System.out.println();
        }
    }
}
