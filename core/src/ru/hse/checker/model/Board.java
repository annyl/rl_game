package ru.hse.checker.model;


import java.util.LinkedList;
import java.util.List;

public class Board  {
    public static final int ROW = 8;
    public static final int COLUMN = 8;
    private final Cell[][] cells = new Cell[ROW][COLUMN];

    private List<Checker> whites = new LinkedList<>();
    private List<Checker> blacks = new LinkedList<>();

    private List<ICheckerChangeListener> listeners = new LinkedList<>();
    private Board flipped;

    private Board() {}

    public Board(ICheckerChangeListener listener) {
        if (listener != null)
            this.listeners.add(listener);

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if ((i + j) % 2 == 0) {
                    cells[i][j] = new Cell(Cell.Type.BLACK, i, j);
                    if (i < 3) {
                        createCheckerInCell(Checker.Type.WHITE, i, j);
                    }
                    if (i > 4) {
                        createCheckerInCell(Checker.Type.BLACK, i, j);
                    }
                } else {
                    cells[i][j] = new Cell(Cell.Type.WHITE, i, j);
                }
            }
        }
    }

    public interface ICheckerChangeListener {
        void onPosChanged(Cell oldCell, Cell newCell);
        void onUpToQueen(Cell cell);
        void onCreated(Cell cell);
        void onRemoved(Cell cell);
    }


    public boolean withinBoard(int x, int y) {
        if (x < 0 || y < 0 || x >= ROW || y >= COLUMN)
            return false;
        return true;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public Checker getChecker(int x, int y) {
        return cells[x][y].getChecker();
    }

    public boolean existsChecker(int x, int y) {
        if (!withinBoard(x, y))
            return false;
        return cells[x][y].hasChecker();
    }

    public boolean existsOppositeChecker(int x, int y, Checker checker) {
        if (!withinBoard(x, y))
            return false;
        return cells[x][y].hasChecker() && cells[x][y].getChecker().isOpposite(checker);
    }

    public Board flipped() {
        if (flipped == null) {
            flipped = new Board();
            flipped.listeners = listeners;
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COLUMN; j++) {
                    flipped.cells[ROW - i - 1][COLUMN - j - 1] = cells[i][j];
                }
            }
        }
        return flipped;
    }

    public List<Checker> getWhites() { return whites; }
    public List<Checker> getBlacks() { return blacks; }

    private void createCheckerInCell(Checker.Type type, int i, int j) {
        List<Checker> listCheckers = type == Checker.Type.WHITE ? whites : blacks;
        Checker checker = new Checker(type, cells[i][j]);
        listCheckers.add(checker);
        cells[i][j].setChecker(checker);
        for (ICheckerChangeListener listener : listeners)
            listener.onCreated(cells[i][j]);
    }

    public void moveChecker(int oldX, int oldY, int newX, int newY) {
        Checker checker = getChecker(oldX, oldY);
        cells[oldX][oldY].removeChecker();
        cells[newX][newY].setChecker(checker);
        for (ICheckerChangeListener listener : listeners)
            listener.onPosChanged(cells[oldX][oldY], cells[newX][newY]);
    }

    public void removeChecker(int x, int y) {
        Checker checker = cells[x][y].getChecker();
        List<Checker> listCheckers = checker.type == Checker.Type.WHITE ? whites : blacks;
        listCheckers.remove(checker);
        cells[x][y].removeChecker();
        for (ICheckerChangeListener listener : listeners)
            listener.onRemoved(cells[x][y]);
    }

    public void upToQueen(int x, int y) {
        cells[x][y].getChecker().upToQueen();
        for (ICheckerChangeListener listener : listeners)
            listener.onUpToQueen(cells[x][y]);
    }

    public void printCells() {
        for (int i = ROW - 1; i >= 0; i--) {
            for (int j = 0; j < COLUMN; j++) {
                if (!cells[i][j].hasChecker()) System.out.print(" 0");
                else if (cells[i][j].getChecker().type == Checker.Type.WHITE)
                    System.out.print(" 1");
                else System.out.print("-1");
//                else {
//                    System.out.print(" " + cells[i][j].getChecker().id);
//                }
            }
            System.out.println();
        }
    }




    public static void main(String[] args) {
        RawModel rawModel = new RawModel();
        Board board = new Board(rawModel);
        rawModel.printModel();
        board.printCells();
        System.out.println();
        board.flipped().printCells();
    }




}
