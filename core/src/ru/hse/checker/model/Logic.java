package ru.hse.checker.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.model.intelligence.Players;
import ru.hse.checker.model.intelligence.UserPlayer;
import ru.hse.checker.utils.Pair;

public class Logic {

    private Board board;
    public Logic(Board board) {
        this.board = board;
    }

    public Pair<Boolean, List<Pair<Cell, Cell>>> playerMustHit(Player player) {
        Board board = player.relativeBoard(this.board);
        List<Checker> checkers = player.checkers(this.board);
        List<Pair<Cell, Cell>> paths = new LinkedList<>();
        for (Checker checker : checkers) {
            Pair<Integer, Integer> indices = player.indxChecker(checker);
            int x = indices.first;
            int y = indices.second;

            if (!checker.isQueen()) {
                int dx1 = x + 1; //forward
                int dy1 = y + 1; //right
                int dy2 = y - 1; //left
                int dx2 = x - 1; //back
                if (board.existsOppositeChecker(dx1, dy1, checker) && !board.existsChecker(dx1+1, dy1+1) && board.withinBoard(dx1+1,dy1+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy1+1)));

                if (board.existsOppositeChecker(dx1, dy2, checker) && !board.existsChecker(dx1+1, dy2+1) && board.withinBoard(dx1+1,dy2+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy2+1)));

                //checker must hit back also
                /*if (board.existsChecker(dx2, dy1) && !board.existsChecker(dx2+1, dy1+1) && board.withinBoard(dx2+1,dy1+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx2+1, dy1+1)));

                if (board.existsChecker(dx2, dy2) && !board.existsChecker(dx2+1, dy2+1) && board.withinBoard(dx2+1,dy2+1))
                    paths.add(new Pair<>(checker.getCell(), board.getCell(dx2+1, dy2+1)));*/
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

                    if (!board.withinBoard(dx1, dy2) || !board.withinBoard(dx1+1, dy2+1) || (board.existsChecker(dx1, dy2) && board.existsChecker(dx1+1, dy2+1)))
                        leftForwardAvailable = false;
                    if (leftForwardAvailable && board.existsChecker(dx1, dy2) && !board.existsOppositeChecker(dx1+1, dy2+1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx1+1, dy2+1)));
                        leftForwardAvailable = false;
                    }

                    if (!board.withinBoard(dx2, dy1) || !board.withinBoard(dx2+1, dy1+1) || (board.existsChecker(dx2, dy1) && board.existsChecker(dx2+1, dy1+1)))
                        rightBackAvailable = false;
                    if (rightBackAvailable && board.existsChecker(dx2, dy1) && !board.existsOppositeChecker(dx2+1, dy1+1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx2+1, dy1+1)));
                        rightBackAvailable = false;
                    }

                    if (!board.withinBoard(dx2, dy2) || !board.withinBoard(dx2+1, dy2+1) || (board.existsChecker(dx2, dy2) && board.existsChecker(dx2+1, dy2+1)))
                        leftBackAvailable = false;
                    if (leftBackAvailable && board.existsChecker(dx2, dy1) && !board.existsOppositeChecker(dx2+1, dy2+1, checker)) {
                        paths.add(new Pair<>(checker.getCell(), board.getCell(dx2+1, dy2+1)));
                        leftBackAvailable = false;
                    }
                }
            }
        }

        boolean isMustHit = paths.size() > 0;
        return new Pair<>(isMustHit, paths);
    }

    public ArrayList<Cell> getAvailablePath(Player player, int x, int y) {
        if (!(player instanceof UserPlayer))
            throw new RuntimeException("There's no rules for AI");

        Board board = player.relativeBoard(this.board);

        ArrayList<Cell> paths = new ArrayList<>();

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

    private static void testAvailablePath() {
        RawModel rawModel = new RawModel();
        Board board = new Board(rawModel);
        Logic logic = new Logic(board);

        UserPlayer first = new UserPlayer(Player.NumPlayer.FIRST);
        UserPlayer second = new UserPlayer(Player.NumPlayer.SECOND);

        Players players = new Players(first, second);

        Scanner in = new Scanner(System.in);
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {

            Player player = it.next();

            player.relativeBoard(board).printCells();

            int x = in.nextInt();
            int y = in.nextInt();
            ArrayList<Cell> paths = logic.getAvailablePath(player, x, y);
            for (Cell cell : paths) {
                System.out.print("(" + cell.x + "," + cell.y + "), ");
            }
            System.out.println();
        }
    }

    public boolean isGameOver(Player player) {
        return player.checkers(this.board).isEmpty();
    }

    public static void main(String[] args) {
        testAvailablePath();
    }
}
