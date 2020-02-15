package ru.hse.checker.model;


public class Board  {
    public static final int ROW = 8;
    public static final int COLUMN = 8;
    public Cell[][] cells = new Cell[ROW][COLUMN];

    public Board(boolean isWhitesBeneath) {
        Checker.Type below = isWhitesBeneath ? Checker.Type.WHITE : Checker.Type.BLACK;
        Checker.Type above = isWhitesBeneath ? Checker.Type.BLACK : Checker.Type.WHITE;


        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if ((i + j) % 2 == 0) {
                    cells[i][j] = new Cell(Cell.Type.BLACK);
                    if (i < 3)
                        cells[i][j].setChecker(new Checker(below));
                    if (i > 4)
                        cells[i][j].setChecker(new Checker(above));
                } else {
                    cells[i][j] = new Cell(Cell.Type.WHITE);
                }
            }
        }
    }


    private void printCells() {
        for (int i = ROW - 1; i >= 0; i--) {
            for (int j = 0; j < COLUMN; j++) {
                if (!cells[i][j].hasChecker()) System.out.print(" 0");
                else if (cells[i][j].getChecker().type == Checker.Type.WHITE)
                    System.out.print(" 1");
                else System.out.print("-1");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Board board = new Board(false);
        board.printCells();
    }
}
