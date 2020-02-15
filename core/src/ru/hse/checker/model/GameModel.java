package ru.hse.checker.model;

public class GameModel {
    private Logic logic = new Logic();
    private Board board;
    private RawModel rawModel;

    public GameModel(Config config) {
        board = new Board(config.isPlayerForWhites());
    }




}
