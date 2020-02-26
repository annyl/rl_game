package ru.hse.checker.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Checker;
import ru.hse.checker.model.IModelListener;
import ru.hse.checker.utils.CheckersAM;
import ru.hse.checker.utils.Pair;

import static ru.hse.checker.model.Board.COLUMN;
import static ru.hse.checker.model.Board.ROW;

public class BoardStage extends Stage implements IModelListener {

    public interface IViewListener {
        void onSelectedCell(int x, int y);
    }

    private Sprite background;
    private CellActor[][] cellActors = new CellActor[ROW][COLUMN];
    private Map<Cell, CheckerActor> checkerActors = new HashMap<>();

    @Override
    public void onSelectedFrom(Cell cell) {
        cellActors[cell.x][cell.y].setCellType(CellActor.Type.MOVE);
    }

    @Override
    public void onSelectedTo(Cell cell) {

    }

    @Override
    public void mustHitPathsHandler(List<Pair<Cell, Cell>> paths) {

    }

    @Override
    public void onAvailablePaths(List<Cell> paths) {
        for (Cell cell : paths)
            cellActors[cell.x][cell.y].setCellType(CellActor.Type.MOVE);
    }

    @Override
    public void incorrectSelection() {
        for (CellActor[] cellActors : cellActors)
            for (CellActor cellActor : cellActors)
                cellActor.setToBaseType();
    }


    public BoardStage(IViewListener listener) {
        super(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        background = new Sprite(CheckersAM.getInstance().getTexture(CheckersAM.BACKGROUND));

        //todo: bind model and view for cells
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                CellActor cellActor;
                if ((i + j) % 2 == 0)
                    cellActor = new CellActor(CellActor.Type.BLACK);
                else
                    cellActor = new CellActor(CellActor.Type.WHITE);
                cellActor.setCoords(i, j);
                addActor(cellActor);
                cellActors[i][j] = cellActor;
                int finalI = i;
                int finalJ = j;
                cellActor.addListener(new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        listener.onSelectedCell(finalI, finalJ);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void draw() {
        getBatch().begin();
        getBatch().disableBlending();
        background.draw(getBatch());
        getBatch().enableBlending();
        getBatch().end();
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void onPosChanged(Cell oldCell, Cell newCell) {
        SequenceAction sequenceAction = new SequenceAction();
        StepAction stepAction = new StepAction();
        stepAction.setCoords(newCell.x, newCell.y);
        CheckerActor checkerSprite = checkerActors.get(oldCell);

        sequenceAction.addAction(stepAction);
        sequenceAction.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                checkerActors.remove(oldCell);
                checkerActors.put(newCell, checkerSprite);
                return false;
            }
        });

        checkerSprite.addAction(sequenceAction);
    }

    @Override
    public void onUpToQueen(Cell cell) {
        Checker checker = cell.getChecker();
        checkerActors.get(cell).setType(checker.type == Checker.Type.WHITE ? CheckerActor.Type.WHITE_QUEEN : CheckerActor.Type.BLACK_QUEEN);
    }

    @Override
    public void onCreated(Cell cell) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Checker checker = cell.getChecker();
                CheckerActor checkerActor;
                if (checker.type == Checker.Type.WHITE)
                    checkerActor = new CheckerActor(CheckerActor.Type.WHITE);
                else
                    checkerActor = new CheckerActor(CheckerActor.Type.BLACK);
                checkerActors.put(cell, checkerActor);
                checkerActor.setCoords(cell.x, cell.y);
                checkerActor.setTouchable(Touchable.disabled);
                addActor(checkerActor);
            }
        });

    }

    @Override
    public void onRemoved(Cell cell) {
        SequenceAction sequenceAction = new SequenceAction();
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setDuration(0.3f);
        alphaAction.setAlpha(1f);
        sequenceAction.addAction(alphaAction);
        CheckerActor checkerActor = checkerActors.remove(cell);

        sequenceAction.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                BoardStage.this.getActors().removeValue(checkerActor, false);
                return false;
            }
        });


        checkerActor.addAction(sequenceAction);
    }
}
