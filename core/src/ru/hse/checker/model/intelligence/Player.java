package ru.hse.checker.model.intelligence;

import java.util.LinkedList;
import java.util.List;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Checker;
import ru.hse.checker.model.Logic;
import ru.hse.checker.utils.Pair;

public abstract class Player {

    public enum NumPlayer {
        FIRST, SECOND
    }

    public final static Checker.Type FIRST_PLAYER_TYPE = Checker.Type.BLACK;
    public final static Checker.Type SECOND_PLAYER_TYPE = FIRST_PLAYER_TYPE == Checker.Type.BLACK ? Checker.Type.WHITE : Checker.Type.BLACK;

    private Checker.Type type;
    private NumPlayer numPlayer;
    protected Board absBoard;
    private Board.Relative relBoard;
    private Logic logic = new Logic();

    //interface bound with Model(Cell). it is not good, but и так пойдет. Нужны всего лишь координаты, можно Cell->Pair<Integer, Integer>
    public interface ActionListener {
        void onSelectedFrom(Cell cell);
        void onSelectedTo(Cell cell);
        void mustHitPathsHandler(List<Pair<Cell, Cell>> paths);
        void onAvailablePaths(List<Cell> paths);
        void incorrectSelection();
        void onStepDone(Player player);
        void onStepBegin(Player player);
        void playerWin(Player player);
    }

    public Player(NumPlayer numPlayer, Board board) {
        this.numPlayer = numPlayer;
        this.type = numPlayer == NumPlayer.FIRST ? FIRST_PLAYER_TYPE : SECOND_PLAYER_TYPE;
        this.absBoard = board;
    }

    public Checker.Type getType() {
        return type;
    }

    public void step(Board board) {
        List<Checker> checkers = checkers();

        Pair<Boolean, List<Pair<Cell, Cell>>> hits = logic.playerMustHit(this);
        boolean isMustHit = hits.first;

        if (isMustHit) {
            do {
                Pair<Cell, Cell> path = getMove(board, hits.second, true);
                Cell from = path.first;
                Cell to = path.second;
                Checker hitter = from.getChecker(); //what hit first, it must continue hit
                board.moveChecker(from.x, from.y, to.x, to.y);

                if (to.x - from.x > 0 && to.y - from.y > 0) //up-right hitting
                    board.removeChecker(to.x - 1, to.y - 1);
                if (to.x - from.x > 0 && to.y - from.y < 0) //up-left hitting
                    board.removeChecker(to.x - 1, to.y + 1);
                if (to.x - from.x < 0 && to.y - from.y > 0) //back-right hitting
                    board.removeChecker(to.x + 1, to.y - 1);
                if (to.x - from.x < 0 && to.y - from.y < 0) //back-left hitting
                    board.removeChecker(to.x + 1, to.y + 1);

                System.out.println("Hitted: " + from.toString() + "->" + to.toString());

                hits = logic.playerMustHit(this);
                isMustHit = hits.first;

                if (isMustHit) {
                    if (logic.getAvailablePathFromMustHits(this, hitter.getCell(), hits.second).size() == 0)
                        isMustHit = false;
                }
            } while (isMustHit);
        } else {
            List<Pair<Cell, Cell>> paths = new LinkedList<>();
            for (Checker checker : checkers) {
                List<Cell> tos = logic.getAvailablePath(this, checker.getCell());
                for (Cell cell : tos) {
                    paths.add(new Pair<>(checker.getCell(), cell));
                }
            }
            Pair<Cell, Cell> path = getMove(board, paths, false);
            Cell from = path.first;
            Cell to = path.second;
            board.moveChecker(from.x, from.y, to.x, to.y);
        }
    }

    public abstract Pair<Cell, Cell> getMove(Board board, List<Pair<Cell, Cell>> legalActions, boolean mustHit);

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
