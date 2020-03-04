package ru.hse.checker.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.hse.checker.controller.io.IOController;
import ru.hse.checker.controller.io.StdIO;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.model.intelligence.Players;
import ru.hse.checker.model.intelligence.UserPlayer;
import ru.hse.checker.utils.Pair;

public class GameModel {
    private Logic logic;
    private Board board;
    private RawModel rawModel = new RawModel();
    private boolean isGameOver = false;
    private Config config;
    private Player currentPlayer;

    public GameModel(Config config) {
        this.config = config;
        logic = new Logic();
    }

    public void startGame(IOController ioController, IModelListener iModelListener) {
        isGameOver = false;

        List<Board.ICheckerChangeListener> listeners = new LinkedList<>();
        listeners.add(rawModel);
        listeners.add(iModelListener);
        board = new Board(listeners);

        Player first = new UserPlayer(Player.NumPlayer.FIRST, board, ioController, iModelListener);
        Player second = new UserPlayer(Player.NumPlayer.SECOND, board, ioController, iModelListener);
        Players players = new Players(first, second);

        Iterator<Player> it = players.iterator();

        while (true) {
            currentPlayer = it.next();
            if (logic.isGameOver(currentPlayer)) {
                isGameOver = true;
                Player winner = it.next();
                System.out.println(winner.num() + " is winner!");
                break;
            }
            currentPlayer.step(board);
        }
    }

    public Player getCurrentPlayer() {return currentPlayer;}

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
            public void mustHitPathsHandler(List<Pair<Cell, Cell>> paths) {
                System.out.println("Must hit!");
                for (Pair<Cell, Cell> pair : paths) {
                    System.out.print("[" + pair.first.toString() + "->" + pair.second.toString() + "]");
                }
            }
        });
    }

}
