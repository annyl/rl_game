package ru.hse.checker.model.intelligence;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Logic;
import ru.hse.checker.utils.Pair;
import sun.rmi.runtime.Log;

public class UserPlayer extends Player {

    public UserPlayer(NumPlayer player) {
        super(player);
    }


    @Override
    public void step(Board board) {
        Logic logic = new Logic(board);

        Pair<Boolean, List<Pair<Cell, Cell>>> hits = logic.playerMustHit(this);
        boolean isMustHit = hits.first;
        if (isMustHit) {
            while (isMustHit) {
                List<Pair<Cell, Cell>> paths = hits.second;
                Cell from = selectFrom(board, paths);
                System.out.println("Selected from: " + from.toString());

                ArrayList<Cell> tos = logic.getAvailablePathFromMustHits(this, from, paths);
                Cell to = selectTo(tos);
                System.out.println("Selected to: " + to.toString());

                //move-hit
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
            }
        } else {
            while (true) {
                Cell from = selectFrom(board);
                System.out.println("Selected from: " + from.toString());

                Pair<Integer, Integer> relCoords = indxChecker(from.getChecker());

                ArrayList<Cell> tos = logic.getAvailablePath(this, relCoords.first, relCoords.second);
                if (tos.isEmpty())
                    continue;
                Cell to = selectTo(tos);
                System.out.println("Selected to: " + to.toString());
                board.moveChecker(from.x, from.y, to.x, to.y);
                System.out.println("Moved: " + from.toString() + "->" + to.toString());
                break;
            }
        }
    }

    private Cell selectFrom(Board board) {
        Scanner sc = new Scanner(System.in);
        Cell cell = null;
        while (cell == null) {
            Board relative = relativeBoard(board);
            relative.printCells();
            System.out.println("Input coords to select checker");
            int x = sc.nextInt();
            int y = sc.nextInt();
            cell = selectFrom(relative, x, y);
        }
        return cell;
    }

    private Cell selectFrom(Board board, int x, int y) {
        Cell cell = board.getCell(x, y);
        if (cell == null || !cell.hasChecker() || cell.getChecker().type != this.getType())
            return null;
        return cell;
    }

    private Cell selectFrom(Board board, List<Pair<Cell, Cell>> paths) {
        Cell cell = null;
        while (cell == null) {
            System.out.println("Must hit!");
            for (Pair<Cell, Cell> pair : paths) {
                System.out.print("[" + pair.first.toString() + "->" + pair.second.toString() + "]");
            }
            System.out.println();
            cell = selectFrom(board);
            for (Pair<Cell, Cell> pair : paths) {
                if (cell == pair.first)
                    return cell;
            }
        }
        return null;
    }

    private Cell selectTo(ArrayList<Cell> tos) {
        Scanner sc = new Scanner(System.in);
        Cell to = null;
        while (to == null) {
            System.out.println("Input number where you want to go");
            for (int i = 1; i <= tos.size(); i++) {
                System.out.print(i + ":" + tos.get(i-1).toString() + " ");
            }
            System.out.println();
            int num = sc.nextInt();
            if (num < 0 || num > tos.size())
                continue;
            to = tos.get(num-1);
        }
        return to;
    }

}
