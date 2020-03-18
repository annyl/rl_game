package ru.hse.checker.model.intelligence;

import java.util.ArrayList;
import java.util.List;

import ru.hse.checker.controller.io.IOController;
import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Checker;
import ru.hse.checker.model.Logic;
import ru.hse.checker.model.RawModel;
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
        listener.onStepBegin(this);
        super.step(board);
        listener.onStepDone(this);
    }

    @Override
    public Pair<Cell, Cell> getMove(Board board, List<Pair<Cell, Cell>> legalActions, boolean mustHit) {
        if (mustHit) {
            while (true) {
                listener.mustHitPathsHandler(legalActions);
                Cell from = io.selectSrcCell(legalActions);
                if (from == null) {
                    listener.incorrectSelection();
                    continue;
                }
                listener.onSelectedFrom(from);
                ArrayList<Cell> tos = logic.getAvailablePathFromMustHits(this, from, legalActions);
                if (tos.isEmpty()) {
                    listener.incorrectSelection();
                    continue;
                }
                Cell to = io.selectDstCell(tos);
                listener.onSelectedTo(to);
                return new Pair<>(from, to);
            }
        } else {
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
                return new Pair<>(from, to);
            }
        }
    }


}
