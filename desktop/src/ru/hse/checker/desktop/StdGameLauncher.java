package ru.hse.checker.desktop;

import java.util.List;

import ru.hse.checker.controller.io.StdIO;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Config;
import ru.hse.checker.model.GameModel;
import ru.hse.checker.model.IModelListener;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.utils.Pair;

public class StdGameLauncher {
    public static void main(String[] args) {
        GameModel gameModel = new GameModel(new Config());
        StdIO stdIO = new StdIO(gameModel);
        gameModel.startGame(stdIO, new IModelListener() {
            @Override
            public void onPosChanged(Cell oldCell, Cell newCell) {
                System.out.println("Moved: " + oldCell.toString() + "->" + newCell.toString());
            }

            @Override
            public void onUpToQueen(Cell cell) {

            }

            @Override
            public void onCreated(Cell cell) {

            }

            @Override
            public void onRemoved(Cell cell) {

            }

            @Override
            public void onSelectedFrom(Cell cell) {
                System.out.println("Selected from: " + cell.toString());
            }

            @Override
            public void onSelectedTo(Cell cell) {
                System.out.println("Selected to: " + cell.toString());
            }

            @Override
            public void onAvailablePaths(List<Cell> paths) {
                System.out.println("Input number where you want to go");
                for (int i = 1; i <= paths.size(); i++) {
                    System.out.print(i + ":" + paths.get(i-1).toString());
                }
                System.out.println();
            }

            @Override
            public void incorrectSelection() {

            }

            @Override
            public void onStepDone(Player player) {

            }

            @Override
            public void onStepBegin(Player player) {

            }

            @Override
            public void playerWin(Player player) {
                System.out.println(player.num() + " is winner!");
            }

            @Override
            public void mustHitPathsHandler(List<Pair<Cell, Cell>> paths) {
                System.out.println("Must hit!");
                for (Pair<Cell, Cell> pair : paths) {
                    System.out.print("[" + pair.first.toString() + "->" + pair.second.toString() + "]");
                }
            }
        });
    }
}
