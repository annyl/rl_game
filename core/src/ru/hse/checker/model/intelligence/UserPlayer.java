package ru.hse.checker.model.intelligence;

import java.util.ArrayList;
import java.util.List;

import ru.hse.checker.controller.io.IOController;
import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Logic;
import ru.hse.checker.utils.Pair;

public class UserPlayer extends Player {

    private IOController io;
    private ActionListener listener;
    private Logic logic = new Logic();

    public UserPlayer(NumPlayer player, Board board, IOController io, ActionListener actionListener) {
        super(player, board);
        this.io = io;
        listener = actionListener;
    }

    @Override
    public void step(Board board) {

        while (true) {
            Pair<Boolean, List<Pair<Cell, Cell>>> hits = logic.playerMustHit(this);
            boolean isMustHit = hits.first;
            if (!isMustHit)
                break;
            List<Pair<Cell, Cell>> paths = hits.second;

            listener.mustHitPathsHandler(paths);

            Cell from = io.selectSrcCell(paths);
            if (from == null) {
                listener.incorrectSelection();
                continue;
            }
            listener.onSelectedFrom(from);

            ArrayList<Cell> tos = logic.getAvailablePathFromMustHits(this, from, paths);
            if (tos.isEmpty()) {
                listener.incorrectSelection();
                continue;
            }

            Cell to = io.selectDstCell(tos);
            listener.onSelectedTo(to);

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
        }

        while (true) {
            Cell from = io.selectSrcCell();
            if (!from.hasChecker() || from.getChecker().type != getType()) {
                listener.incorrectSelection();
                continue;
            }

            listener.onSelectedFrom(from);

            ArrayList<Cell> tos = logic.getAvailablePath(this, from);

            if (tos.isEmpty()) {
                listener.incorrectSelection();
                continue;
            }

            listener.onAvailablePaths(tos);
            Cell to = io.selectDstCell(tos);
            listener.onSelectedTo(to);

            board.moveChecker(from.x, from.y, to.x, to.y);
            break;
        }
    }


}
