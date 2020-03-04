package ru.hse.checker.controller.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ru.hse.checker.Game;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.GameModel;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.utils.Pair;

public class StdIO implements IOController {

    private GameModel gameModel;

    public StdIO(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    public Cell selectSrcCell() {
        Scanner sc = new Scanner(System.in);
        Cell cell = null;
        while (cell == null) {
            gameModel.getCurrentPlayer().relativeBoard().printCells();
            System.out.println("Input coords to select checker");
            int x = sc.nextInt();
            int y = sc.nextInt();
            cell = selectSrcCell(x, y);
        }
        return cell;
    }

    private Cell selectSrcCell(int x, int y) {
        Cell cell = gameModel.getCurrentPlayer().relativeBoard().getCell(x, y);
        if (cell == null || !cell.hasChecker() || cell.getChecker().type != gameModel.getCurrentPlayer().getType())
            return null;
        return cell;
    }



    @Override
    public Cell selectDstCell(ArrayList<Cell> tos) {
        Scanner sc = new Scanner(System.in);
        Cell to = null;
        while (to == null) {
            int num = sc.nextInt();
            if (num < 0 || num > tos.size())
                continue;
            to = tos.get(num-1);
        }
        return to;
    }
}
