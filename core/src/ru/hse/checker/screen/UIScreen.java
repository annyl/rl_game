package ru.hse.checker.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import ru.hse.checker.model.Constants;
import ru.hse.checker.utils.CheckersAM;

public class UIScreen implements Screen {
    private Stage uiStage;
    private Table table;

    protected Stage getUIStage() {
        if (uiStage == null) {
            uiStage = new Stage();
            uiStage = new Stage(new StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT));
            Gdx.input.setInputProcessor(uiStage);
            setBackButton();
            uiStage.addActor(backGround());
        }
        return uiStage;
    }

    protected Table getTable() {
        if (table == null) {
            table = new Table(getSkin());
            table.setFillParent(true);
            getUIStage().addActor(table);
        }
        return table;
    }

    private Table backGround() {
        Table layer = new Table();
        Image bg = new Image(getSkin().getDrawable("window-c"));
        bg.setWidth(Constants.SCREEN_WIDTH);
        bg.setHeight(Constants.SCREEN_HEIGHT);
        bg.setColor(Color.DARK_GRAY);
        layer.add(bg).height(Constants.GAME_HEIGHT).width(Constants.GAME_WIDTH).expandX().expandY();
        layer.setFillParent(true);
        return layer;
    }

    protected Skin getSkin() {
        return CheckersAM.getInstance().getSkin();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getUIStage().act();
        getUIStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        getUIStage().getViewport().update(width, height, false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (uiStage != null)
            uiStage.dispose();
    }

    /**
     * Back button
     */
    private void setBackButton() {
        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    keyBackPressed();
                }
                return false;
            }
        });
    }

    /**
     * Override this method to do some function when back button pressed
     */
    public void keyBackPressed() {

    }
}
