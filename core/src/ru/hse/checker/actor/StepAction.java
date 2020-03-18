package ru.hse.checker.actor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static ru.hse.checker.actor.BoardActor.trnsfrmCoordToPosX;
import static ru.hse.checker.actor.BoardActor.trnsfrmCoordToPosY;

public class StepAction extends ParallelAction {
    private float duration = 0.2f;
    private float scaleVal = 1.08f;

    public StepAction(int x, int y) {
        StepMove moveToAction = new StepMove();
        moveToAction.setDuration(duration);
        moveToAction.setCoords(x, y);
        ScaleToAction scale = scaleTo(scaleVal, scaleVal, duration/2);
        scale.setInterpolation(Interpolation.smoother);
        ScaleToAction shrink = scaleTo(1f, 1f, duration/2);
        shrink.setInterpolation(Interpolation.smoother);
        addAction(moveToAction);
        addAction(sequence(scale, shrink));
    }

    private class StepMove extends MoveToAction {

        void setCoords(int x, int y) {
            float py = trnsfrmCoordToPosY(x); //x - row, y - column
            float px = trnsfrmCoordToPosX(y);
            setInterpolation(Interpolation.smoother);
            setPosition(px, py);
        }
    }


}
