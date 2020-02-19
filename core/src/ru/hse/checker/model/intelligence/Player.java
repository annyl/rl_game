package ru.hse.checker.model.intelligence;

import java.util.List;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Checker;
import ru.hse.checker.utils.Pair;

public abstract class Player {

    public enum NumPlayer {
        FIRST, SECOND
    }

    private Checker.Type type;
    private NumPlayer numPlayer;

    public Player(NumPlayer numPlayer) {
        this.numPlayer = numPlayer;
        this.type = numPlayer == NumPlayer.FIRST ? Checker.Type.WHITE : Checker.Type.BLACK;
    }

    public Checker.Type getType() {
        return type;
    }

    public abstract void step(Board board);

    public NumPlayer num() {
        return numPlayer;
    }

    public Pair<Integer, Integer> indxChecker(Checker checker) {
        Cell cell = checker.getCell();
        return numPlayer == NumPlayer.FIRST ? new Pair<>(cell.x, cell.y)  : new Pair<>(Board.ROW - cell.x - 1, Board.COLUMN - cell.y - 1) ;
    }

    public Board relativeBoard(Board board) {
        return numPlayer == NumPlayer.FIRST ? board : board.flipped();
    }
    public List<Checker> checkers(Board board) { return type == Checker.Type.WHITE ?  board.getWhites() : board.getBlacks(); }
}
