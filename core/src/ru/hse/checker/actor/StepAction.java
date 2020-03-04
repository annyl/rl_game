package ru.hse.checker.actor;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

import static ru.hse.checker.actor.BoardActor.trnsfrmCoordToPosX;
import static ru.hse.checker.actor.BoardActor.trnsfrmCoordToPosY;

public class StepAction extends MoveToAction {
    public StepAction() {
        setDuration(0.3f);
    }

    public void setCoords(int x, int y) {
        float py = trnsfrmCoordToPosY(x); //x - row, y - column
        float px = trnsfrmCoordToPosX(y);
        setPosition(px, py);
    }
}
