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
    private Board absBoard;
    private Board.Relative relBoard;

    //interface bound with Model(Cell). it is not good, but и так пойдет. Нужны всего лишь координаты, можно Cell->Pair<Integer, Integer>
    public interface ActionListener {
        void onSelectedFrom(Cell cell);
        void onSelectedTo(Cell cell);
        void mustHitPathsHandler(List<Pair<Cell, Cell>> paths);
        void onAvailablePaths(List<Cell> paths);
        void incorrectSelection();
    }

    public Player(NumPlayer numPlayer, Board board) {
        this.numPlayer = numPlayer;
        this.type = numPlayer == NumPlayer.FIRST ? Checker.Type.WHITE : Checker.Type.BLACK;
        this.absBoard = board;
    }

    public Checker.Type getType() {
        return type;
    }

    public abstract void step(Board board);

    public NumPlayer num() {
        return numPlayer;
    }

    public Board.Relative relativeBoard() {
        if (relBoard == null)
            relBoard = absBoard.forPlayer(this);
        return relBoard;
    }

    public List<Checker> checkers() { return type == Checker.Type.WHITE ?  absBoard.getWhites() : absBoard.getBlacks(); }


}
