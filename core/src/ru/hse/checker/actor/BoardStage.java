package ru.hse.checker.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.hse.checker.Game;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.Checker;
import ru.hse.checker.model.Constants;
import ru.hse.checker.model.IModelListener;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.screen.GameDialog;
import ru.hse.checker.screen.GameScreen;
import ru.hse.checker.screen.IntroScreen;
import ru.hse.checker.utils.CheckersAM;
import ru.hse.checker.utils.Pair;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static ru.hse.checker.model.Board.COLUMN;
import static ru.hse.checker.model.Board.ROW;

public class BoardStage extends Stage implements IModelListener {

    public interface IViewListener {
        void onSelectedCell(int x, int y);
        void beginAnimation();
        void endAnimation();
    }

    private TextureRegion background;
    private CellActor[][] cellActors = new CellActor[ROW][COLUMN];
    private final Map<Cell, CheckerActor> checkerActors = new HashMap<>();
    private IViewListener listener;


    @Override
    public void onSelectedFrom(Cell cell) {
        cellActors[cell.x][cell.y].setCellType(CellActor.Type.MOVE);
    }

    @Override
    public void onSelectedTo(Cell cell) {

    }

    @Override
    public void mustHitPathsHandler(List<Pair<Cell, Cell>> paths) {
        Gdx.app.postRunnable(() -> {
            for (Pair<Cell, Cell> pair : paths) {
                cellActors[pair.first.x][pair.first.y].setCellType(CellActor.Type.HIT);
                cellActors[pair.second.x][pair.second.y].setCellType(CellActor.Type.HIT);
            }
        });
    }

    @Override
    public void onAvailablePaths(List<Cell> paths) {
        Gdx.app.postRunnable(() -> {
            for (Cell cell : paths)
                cellActors[cell.x][cell.y].setCellType(CellActor.Type.MOVE);
        });
    }

    private void deselectAllCells() {
        for (CellActor[] cellActors : cellActors)
            for (CellActor cellActor : cellActors)
                cellActor.setToBaseType();
    }

    @Override
    public void incorrectSelection() {
        Gdx.app.postRunnable(() -> {
            deselectAllCells();
        });
    }

    @Override
    public void onStepDone(Player player) {
        /*Gdx.app.postRunnable(() -> {
            addAction(sequence(rotateTo(getRoot().getRotation() + 180, 0.4f), run(()->{
                listener.endAnimation();
            })));
        });
        listener.beginAnimation();*/
    }

    @Override
    public void onStepBegin(Player player) {
        Gdx.app.postRunnable(() -> {
            synchronized (checkerActors) {
                checkerActors.forEach((cell, checkerActor) -> {
                    if (cell.hasChecker() && player.getType() == cell.getChecker().type) {
                        checkerActor.select();
                    } else
                        checkerActor.deselect();
                });
                listener.endAnimation();
            }
        });
        listener.beginAnimation();
    }

    @Override
    public void playerWin(Player player) {
        List<Cell> cellsToRemoveCheckers = new LinkedList<>();
        checkerActors.forEach((Cell cell, CheckerActor checkerActor) -> {
            cellsToRemoveCheckers.add(cell);
        });
        cellsToRemoveCheckers.forEach((Cell cell) -> {
            onRemoved(cell);
        });

        Gdx.app.postRunnable(() -> {
            final GameDialog gameDialog = new GameDialog("Game Over",  CheckersAM.getInstance().getSkin()); // this is the dialog title
            gameDialog.text(player.getType().name() + " won!");
            gameDialog.text("Do you want to play again?");
            gameDialog.button(" Yes ", new InputListener() { // button to exit app
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameDialog.hide();

                    ((GameScreen) ((Game) Gdx.app.getApplicationListener()).getScreen()).startGame();
                    return true;
                }
            });

            gameDialog.button(" No ", new InputListener() { // button to exit app
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameDialog.hide();

                    ((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen());
                    return false;
                }
            });

            gameDialog.show(this);
        });

    }


    public BoardStage(IViewListener listener) {
        super(new FitViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));

        this.listener = listener;
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
        getRoot().setOrigin(getWidth() / 2, getHeight() / 2); //to rotate by center

        addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    GameDialog gameDialog = new GameDialog("Quit", CheckersAM.getInstance().getSkin());
                    gameDialog.text("Are you sure to quit?");
                    gameDialog.button("Yes", new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            gameDialog.hide();
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen());
                            return true;
                        }
                    });
                    gameDialog.button("No", new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            gameDialog.hide();
                            return false;
                        }
                    });
                    gameDialog.show(BoardStage.this);
                }
                return false;
            }
        });
    }

    @Override
    public void draw() {
        getBatch().begin();
        getBatch().disableBlending();
        getBatch().draw(background, 0, 0, getWidth(), getHeight());
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
        Gdx.app.postRunnable(() -> {
            CheckerActor checkerActor = checkerActors.get(oldCell);
            checkerActor.addAction(sequence(new StepAction(newCell.x, newCell.y), run(() -> {
                synchronized (checkerActors) {
                    checkerActors.remove(oldCell);
                    checkerActors.put(newCell, checkerActor);
                }
                deselectAllCells();
                listener.endAnimation();
            })));
        });

        listener.beginAnimation();
    }

    @Override
    public void onUpToQueen(Cell cell) {
        Gdx.app.postRunnable(() -> {
            Checker checker = cell.getChecker();
            checkerActors.get(cell).setType(checker.type == Checker.Type.WHITE ? CheckerActor.Type.WHITE_QUEEN : CheckerActor.Type.BLACK_QUEEN);
        });
    }

    @Override
    public void onCreated(Cell cell) {
        Gdx.app.postRunnable(() -> {
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
        });

    }

    @Override
    public void onRemoved(Cell cell) {
        Gdx.app.postRunnable(() -> {
            CheckerActor checkerActor = checkerActors.remove(cell);
            checkerActor.addAction(sequence(fadeOut(0.3f), run(() -> {
                synchronized (checkerActors) {
                    BoardStage.this.getActors().removeValue(checkerActor, false);
                }
                listener.endAnimation();
            })));
        });
        listener.beginAnimation();
    }
}
