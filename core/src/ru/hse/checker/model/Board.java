package ru.hse.checker.model;


import java.util.LinkedList;
import java.util.List;

import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.utils.Pair;

public class Board  {
    public static final int ROW = 8;
    public static final int COLUMN = 8;
    private final Cell[][] cells = new Cell[ROW][COLUMN];

    private List<Checker> whites = new LinkedList<>();
    private List<Checker> blacks = new LinkedList<>();

    private List<ICheckerChangeListener> listeners = new LinkedList<>();
    private Board flipped;
    private RawModel rawModel = new RawModel();

    private Board() {}

    public Board(List<ICheckerChangeListener> listeners) {
        this.listeners.addAll(listeners);
        this.listeners.add(rawModel);

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if ((i + j) % 2 == 0) {
                    cells[i][j] = new Cell(Cell.Type.BLACK, i, j);
                    if (i < 3) {
                        createCheckerInCell(Player.FIRST_PLAYER_TYPE, i, j);
                    }
                    if (i > 4) {
                        createCheckerInCell(Player.SECOND_PLAYER_TYPE, i, j);
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

    boolean withinBoard(int x, int y) {
        if (x < 0 || y < 0 || x >= ROW || y >= COLUMN)
            return false;
        return true;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    Checker getChecker(int x, int y) {
        return cells[x][y].getChecker();
    }

    boolean existsChecker(int x, int y) {
        if (!withinBoard(x, y))
            return false;
        return cells[x][y].hasChecker();
    }

    boolean existsOppositeChecker(int x, int y, Checker checker) {
        if (!withinBoard(x, y))
            return false;
        return cells[x][y].hasChecker() && cells[x][y].getChecker().isOpposite(checker);
    }

    private Board flip() {
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

    public RawModel getRaw() { return rawModel; }

    private void createCheckerInCell(Checker.Type type, int i, int j) {
        List<Checker> listCheckers = type == Checker.Type.WHITE ? whites : blacks;
        Checker checker = new Checker(type, cells[i][j]);
        listCheckers.add(checker);
        cells[i][j].setChecker(checker);
        for (ICheckerChangeListener listener : listeners)
            listener.onCreated(cells[i][j]);
    }

    public void moveChecker(int oldX, int oldY, int newX, int newY) {
        Cell from = getCell(oldX, oldY);
        Cell to = getCell(newX, newY);
        Checker checker = from.getChecker();
        from.removeChecker();
        to.setChecker(checker);
        for (ICheckerChangeListener listener : listeners)
            listener.onPosChanged(from, to);
        if (newX == ROW - 1 && !checker.isQueen() && checker.type == Player.FIRST_PLAYER_TYPE) //part of logic is here - пусть
            upToQueen(newX, newY);
        if (newX == 0 && !checker.isQueen() && checker.type == Player.SECOND_PLAYER_TYPE)
            upToQueen(newX, newY);
    }

    public void removeChecker(int x, int y) {
        Cell cell = getCell(x, y);
        Checker checker = cell.getChecker();
        List<Checker> listCheckers = checker.type == Checker.Type.WHITE ? whites : blacks;
        listCheckers.remove(checker);
        cell.removeChecker();
        for (ICheckerChangeListener listener : listeners)
            listener.onRemoved(cell);
    }

    void upToQueen(int x, int y) {
        Cell cell = getCell(x, y);
        cell.getChecker().upToQueen();
        for (ICheckerChangeListener listener : listeners)
            listener.onUpToQueen(cell);
    }

    void printCells() {
        for (int i = ROW - 1; i >= 0; i--) {
            System.out.print(i + " ");
            for (int j = 0; j < COLUMN; j++) {
                if (!cells[i][j].hasChecker()) System.out.print("\u25A1");
                else if (cells[i][j].getChecker().type == Checker.Type.WHITE)
                    if (!cells[i][j].getChecker().isQueen())
                        System.out.print("\u25CE");
                    else
                        System.out.print("\u25C7");
                else if (!cells[i][j].getChecker().isQueen())
                    System.out.print("\u25C9");
                else
                    System.out.print("\u25C8");
            }
            System.out.println();
        }
        System.out.print("  ");
        for (int i = 0; i < COLUMN; i++) {
            System.out.print(i);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        RawModel rawModel = new RawModel();
        List<ICheckerChangeListener> listeners = new LinkedList<>();
        listeners.add(rawModel);
        Board board = new Board(listeners);
        rawModel.printModel();
        board.printCells();
        System.out.println();
        board.flip().printCells();
    }

    public Relative forPlayer(Player player) {
        return new Relative(player, this);
    }

    public static class Relative {

        private Board relative;
        private boolean isFlipped;

        private Relative(Player player, Board board) {
            if (player.num() == Player.NumPlayer.FIRST) {
                isFlipped = false;
                this.relative = board;
            } else {
                this.relative = board.flip();
                isFlipped = true;
            }
        }

        Pair<Integer, Integer> indxChecker(Checker checker) {
            Cell cell = checker.getCell();
            return !isFlipped ? new Pair<>(cell.x, cell.y)  : new Pair<>(Board.ROW - cell.x - 1, Board.COLUMN - cell.y - 1) ;
        }

        boolean withinBoard(int x, int y) {
            return relative.withinBoard(x, y);
        }

        public Cell getCell(int x, int y) {
            return relative.getCell(x, y);
        }

        Checker getChecker(int x, int y) {
            return relative.getChecker(x, y);
        }

        boolean existsChecker(int x, int y) {
            return relative.existsChecker(x, y);
        }

        boolean existsOppositeChecker(int x, int y, Checker checker) {
            return relative.existsOppositeChecker(x, y, checker);
        }

        public void printCells() {
            relative.printCells();
        }

    }

}
