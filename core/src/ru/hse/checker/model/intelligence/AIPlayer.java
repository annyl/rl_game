package ru.hse.checker.model.intelligence;

import java.util.List;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.utils.Pair;

public class AIPlayer extends Player {
    private static Player ANDROID_AI = null; //implementation is only in android project
    private ActionListener iModelListener;

    public AIPlayer(NumPlayer numPlayer, Board board, ActionListener iModelListener) {
        super(numPlayer, board);
        this.iModelListener = iModelListener;
    }

    @Override
    public void step(Board board) {
        iModelListener.onStepBegin(this);
        super.step(board);
        iModelListener.onStepDone(this);
    }

    @Override
    public Pair<Cell, Cell> getMove(Board board, List<Pair<Cell, Cell>> legalActions, boolean mustHit) {
        return ANDROID_AI.getMove(board, legalActions, mustHit);
    }

    public static void setAndroidAi(Player player) {
        ANDROID_AI = player;
    }
}
