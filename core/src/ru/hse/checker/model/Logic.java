package ru.hse.checker.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.model.intelligence.UserPlayer;
import ru.hse.checker.utils.Pair;

public class Logic {

    public Pair<Boolean, List<Pair<Cell, Cell>>> playerMustHit(Player player) {
        Board.Relative board = player.relativeBoard();
        List<Checker> checkers = player.checkers();
        List<Pair<Cell, Cell>> paths = new LinkedList<>();
        for (Checker checker : checkers) {
            Pair<Integer, Integer> indices = board.indxChecker(checker);
            int x = indices.first;
            int y = indices.second;

            int dx1 = x + 1; //forward
            int dy1 = y + 1; //right
            int dy2 = y - 1; //left

            if (board.existsOppositeChecker(dx1, dy1, checker) && !board.existsChecker(dx1+1, dy1+1) && board.withinBoard(dx1+1,dy1+1))
                paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy1+1)));

            if (board.existsOppositeChecker(dx1, dy2, checker) && !board.existsChecker(dx1+1, dy2-1) && board.withinBoard(dx1+1,dy2-1))
                paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy2-1)));

            if (!checker.isQueen())
                continue;

            int dx2 = x - 1; //back

            if (board.existsOppositeChecker(dx2, dy1, checker) && !board.existsChecker(dx2-1, dy1+1) && board.withinBoard(dx2-1,dy1+1))
                paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy1+1)));

            if (board.existsOppositeChecker(dx2, dy2, checker) && !board.existsChecker(dx2-1, dy2-1) && board.withinBoard(dx2-1,dy2-1))
                paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy2-1)));
        }

        boolean isMustHit = paths.size() > 0;
        return new Pair<>(isMustHit, paths);
    }

    public ArrayList<Cell> getAvailablePath(Player player, Cell cell) {
        ArrayList<Cell> paths = new ArrayList<>();

        Board.Relative board = player.relativeBoard();
        Pair<Integer, Integer> relCoords = board.indxChecker(cell.getChecker());
        int x = relCoords.first;
        int y = relCoords.second;

        if (!board.existsChecker(x, y))
            return paths;

        Checker checker = board.getChecker(x, y);

        int dx1 = x + 1; //forward
        int dy1 = y + 1; //right
        int dy2 = y - 1; //left

        if (!board.existsChecker(dx1, dy1) && board.withinBoard(dx1, dy1))
            paths.add(board.getCell(dx1, dy1));
        if (!board.existsChecker(dx1, dy2) && board.withinBoard(dx1, dy2))
            paths.add(board.getCell(dx1, dy2));

        if (!checker.isQueen())
            return paths;

        int dx2 = x - 1; //back

        if (!board.existsChecker(dx2, dy1) && board.withinBoard(dx2, dy1))
            paths.add(board.getCell(dx2, dy1));
        if (!board.existsChecker(dx2, dy2) && board.withinBoard(dx2, dy2))
            paths.add(board.getCell(dx2, dy2));

        return paths;
    }

    public ArrayList<Cell> getAvailablePathFromMustHits(Player player, Cell cell, List<Pair<Cell, Cell>> hits) {
        ArrayList<Cell> paths = new ArrayList<>();

        for (Pair<Cell, Cell> pair : hits) {
            if (pair.first == cell)
                paths.add(pair.second);
        }

        return paths;
    }


    public boolean isGameOver(Player player) {
        List<Checker> checkers = player.checkers();
        int availablePaths = 0;
        for (Checker checker : checkers) {
            availablePaths += getAvailablePath(player, checker.getCell()).size();
        }
        return player.checkers().isEmpty() || availablePaths == 0;
    }

}
