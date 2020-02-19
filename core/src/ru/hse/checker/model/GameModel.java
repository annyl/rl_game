package ru.hse.checker.model;

import com.badlogic.gdx.Game;

import java.util.Iterator;
import java.util.List;

import ru.hse.checker.model.intelligence.AIPlayer;
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
        board = new Board(rawModel);
        logic = new Logic(board);
    }

    public void startGame() {
        isGameOver = false;

        Player first = new UserPlayer(Player.NumPlayer.FIRST);
        Player second = new UserPlayer(Player.NumPlayer.SECOND);
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

    public static void main(String[] args) {
        GameModel gameModel = new GameModel(null);
        gameModel.startGame();
    }

}
