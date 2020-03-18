package ru.hse.checker.model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.hse.checker.controller.io.IOController;
import ru.hse.checker.model.intelligence.AIPlayer;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.model.intelligence.Players;
import ru.hse.checker.model.intelligence.UserPlayer;

public class GameModel {
    private Logic logic;
    private Board board;
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
        listeners.add(iModelListener);
        board = new Board(listeners);

        Player first = new UserPlayer(Player.NumPlayer.FIRST, board, ioController, iModelListener);

        Player second;
        if (!config.isVsAI())
            second  = new UserPlayer(Player.NumPlayer.SECOND, board, ioController, iModelListener);
        else {
            if (Gdx.app.getType() != Application.ApplicationType.Android)
                throw new RuntimeException("There's only AI for Android");
            second = new AIPlayer(Player.NumPlayer.SECOND, board, iModelListener);
        }

        Players players = new Players(first, second);

        Iterator<Player> it = players.iterator();

        while (true) {
            currentPlayer = it.next();
            if (logic.isGameOver(currentPlayer)) {
                isGameOver = true;
                Player winner = it.next();
                iModelListener.playerWin(winner);
                break;
            }
            currentPlayer.step(board);
        }
    }
    public boolean isGameOver() {return isGameOver;}
    public Board getBoard() {return board;}
    public Player getCurrentPlayer() {return currentPlayer;}
}
