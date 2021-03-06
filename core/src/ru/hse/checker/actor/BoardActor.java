package ru.hse.checker.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Constants;

public class BoardActor extends Actor {

    private static int WIDTH = (int) Constants.GAME_WIDTH;
    private static int HEIGHT = (int) Constants.GAME_HEIGHT;

    private static int CELL_LEN = HEIGHT / Board.ROW; //HEIGHT < WIDTH -> HEIGHT
    private static int PADDING_H = (HEIGHT - Board.ROW*CELL_LEN)/2;
    private static int PADDING_W = (WIDTH - Board.COLUMN*CELL_LEN)/2;


    public BoardActor() {
        setWidth(CELL_LEN);
        setHeight(CELL_LEN);
    }

    public void setCoords(int x, int y) {
        float py = trnsfrmCoordToPosY(x); //x - row, y - column
        float px = trnsfrmCoordToPosX(y);
        setPosition(px, py);
    }

    public static float trnsfrmCoordToPosY(int x) {
        return PADDING_H + x*CELL_LEN;
    }

    public static float trnsfrmCoordToPosX(int y) {
        return PADDING_W + y*CELL_LEN;
    }
}
