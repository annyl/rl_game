package ru.hse.checker.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ru.hse.checker.utils.Pair;

public class RawModel implements Board.ICheckerChangeListener {
    private int[] model = new int[Board.ROW * Board.COLUMN];

    private final int BLACK = 1;
    private final int WHITE = 3;
    private final int BLACK_QUEEN = 2;
    private final int WHITE_QUEEN = 4;
    private static Set<Integer> cellsToAddOne = new HashSet<>(Arrays.asList(0, 1, 2, 3, 8, 9, 10, 11, 16, 17, 18, 19, 24, 25, 26, 27));


    public int[] getFlatten() {
        return model;
    }

    public static int numCell(int x, int y) {
        return index(x, y) / 2;
    }

    public static Pair<Integer, Integer> coords(int numCell) {
        int index = 2*numCell + (cellsToAddOne.contains(numCell) ? 1 : 0);
        return new Pair<>(index/Board.ROW, 7 - index % Board.ROW);
    }

    public static int index(int x, int y) {
        return x * Board.ROW + 7-y;
    }


    @Override
    public void onPosChanged(Cell oldCell, Cell newCell) {
        model[index(oldCell.x, oldCell.y)] = 0;
        Checker.Type typeChecker = newCell.getChecker().type;
        int type;
        if (typeChecker == Checker.Type.WHITE) {
            if (newCell.getChecker().isQueen())
                type = WHITE_QUEEN;
            else
                type = WHITE;
        } else {
            if (newCell.getChecker().isQueen())
                type = BLACK_QUEEN;
            else
                type = BLACK;
        }
        model[index(newCell.x, newCell.y)] = type;
    }

    @Override
    public void onUpToQueen(Cell cell) {
        model[index(cell.x, cell.y)] = cell.getChecker().type == Checker.Type.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
    }

    @Override
    public void onCreated(Cell cell) {
        model[index(cell.x, cell.y)] = cell.getChecker().type == Checker.Type.WHITE ? WHITE : BLACK;
    }

    @Override
    public void onRemoved(Cell cell) {
        model[index(cell.x, cell.y)] = 0;
    }

    public void printModel() {
        for (int i = Board.ROW - 1; i >= 0; i--) {
            for (int j = 0; j < Board.COLUMN; j++) {
                System.out.print(model[index(i, j)]);
            }
            System.out.println();
        }
    }
}
