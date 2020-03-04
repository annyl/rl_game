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

            if (!checker.isQueen()) {
                int dx1 = x + 1; //forward
                int dy1 = y + 1; //right
                int dy2 = y - 1; //left
                int dx2 = x - 1; //back
                if (board.existsOppositeChecker(dx1, dy1, checker) && !board.existsChecker(dx1+1, dy1+1) && board.withinBoard(dx1+1,dy1+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy1+1)));

                if (board.existsOppositeChecker(dx1, dy2, checker) && !board.existsChecker(dx1+1, dy2-1) && board.withinBoard(dx1+1,dy2-1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy2-1)));

                //checker must hit back also
                /*if (board.existsChecker(dx2, dy1) && !board.existsChecker(dx2-1, dy1+1) && board.withinBoard(dx2-1,dy1+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy1+1)));

                if (board.existsChecker(dx2, dy2) && !board.existsChecker(dx2-1, dy2-1) && board.withinBoard(dx2-1,dy2-1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy2-1)));*/
            } else {
                boolean leftForwardAvailable = true;
                boolean rightForwardAvailable = true;
                boolean leftBackAvailable = true;
                boolean rightBackAvailable = true;
                for (int dc = 1; dc < Board.ROW; dc++) {
                    int dx1 = x + dc; //forward
                    int dy1 = y + dc; //right
                    int dy2 = y - dc; //left
                    int dx2 = x - dc; //back

                    if (!board.withinBoard(dx1, dy1) || !board.withinBoard(dx1+1, dy1+1) || (board.existsChecker(dx1, dy1) && board.existsChecker(dx1+1, dy1+1)))
                        rightForwardAvailable = false;
                    if (rightForwardAvailable && board.existsChecker(dx1, dy1) && !board.existsOppositeChecker(dx1+1, dy1+1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy1+1)));
                        rightForwardAvailable = false;
                    }

                    if (!board.withinBoard(dx1, dy2) || !board.withinBoard(dx1+1, dy2-1) || (board.existsChecker(dx1, dy2) && board.existsChecker(dx1+1, dy2-1)))
                        leftForwardAvailable = false;
                    if (leftForwardAvailable && board.existsChecker(dx1, dy2) && !board.existsOppositeChecker(dx1+1, dy2-1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy2-1)));
                        leftForwardAvailable = false;
                    }

                    if (!board.withinBoard(dx2, dy1) || !board.withinBoard(dx2-1, dy1+1) || (board.existsChecker(dx2, dy1) && board.existsChecker(dx2-1, dy1+1)))
                        rightBackAvailable = false;
                    if (rightBackAvailable && board.existsChecker(dx2, dy1) && !board.existsOppositeChecker(dx2-1, dy1+1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy1+1)));
                        rightBackAvailable = false;
                    }

                    if (!board.withinBoard(dx2, dy2) || !board.withinBoard(dx2-1, dy2-1) || (board.existsChecker(dx2, dy2) && board.existsChecker(dx2-1, dy2-1)))
                        leftBackAvailable = false;
                    if (leftBackAvailable && board.existsChecker(dx2, dy1) && !board.existsOppositeChecker(dx2-1, dy2-1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx2-1, dy2-1)));
                        leftBackAvailable = false;
                    }
                }
            }
        }

        boolean isMustHit = paths.size() > 0;
        return new Pair<>(isMustHit, paths);
    }

    public ArrayList<Cell> getAvailablePath(Player player, Cell cell) {
        if (!(player instanceof UserPlayer))
            throw new RuntimeException("There's no rules for AI");

        ArrayList<Cell> paths = new ArrayList<>();
        if (!cell.hasChecker())
            return paths;

        Board.Relative board = player.relativeBoard();
        Pair<Integer, Integer> relCoords = board.indxChecker(cell.getChecker());
        int x = relCoords.first;
        int y = relCoords.second;



        if (!board.existsChecker(x, y))
            return paths;

        Checker checker = board.getChecker(x, y);

        boolean leftForwardAvailable = true;
        boolean rightForwardAvailable = true;
        boolean leftBackAvailable = true;
        boolean rightBackAvailable = true;
        for (int dc = 1; dc < Board.ROW; dc++) {
            int dx1 = x + dc; //forward
            int dy1 = y + dc; //right
            int dy2 = y - dc; //left

            if (rightForwardAvailable && !board.existsChecker(dx1, dy1) && board.withinBoard(dx1, dy1))
                paths.add(board.getCell(dx1, dy1));
            else rightForwardAvailable = false;

            if (leftForwardAvailable && !board.existsChecker(dx1, dy2) && board.withinBoard(dx1, dy2))
                paths.add(board.getCell(dx1, dy2));
            else leftForwardAvailable = false;

            if (!checker.isQueen()) {
                break;
            }

            int dx2 = x - dc; //back

            if (rightBackAvailable && !board.existsChecker(dx2, dy1) && board.withinBoard(dx2, dy1))
                paths.add(board.getCell(dx2, dy1));
            else rightBackAvailable = false;

            if (leftBackAvailable &&  !board.existsChecker(dx2, dy2) && board.withinBoard(dx2, dy2))
                paths.add(board.getCell(dx2, dy2));
            else leftBackAvailable = false;
        }

        return paths;
    }

    public ArrayList<Cell> getAvailablePathFromMustHits(Player player, Cell cell, List<Pair<Cell, Cell>> hits) {
        if (!(player instanceof UserPlayer))
            throw new RuntimeException("There's no rules for AI");

        ArrayList<Cell> paths = new ArrayList<>();

        for (Pair<Cell, Cell> pair : hits) {
            if (pair.first == cell)
                paths.add(pair.second);
        }

        return paths;
    }


    public boolean isGameOver(Player player) {
        return player.checkers().isEmpty();
    }

}
