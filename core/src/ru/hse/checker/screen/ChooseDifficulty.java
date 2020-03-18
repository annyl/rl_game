package ru.hse.checker.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.hse.checker.Game;
import ru.hse.checker.model.Config;

public class ChooseDifficulty extends UIScreen {

    private Config config;

    ChooseDifficulty(Config config) {
        this.config = config;
    }

    @Override
    public void show() {
        Label welcomeLabel = new Label("Choose difficulty", getSkin());
        Label.LabelStyle halogen = new Label.LabelStyle(getSkin().getFont("halogen"), getSkin().getColor("color"));
        welcomeLabel.setStyle(halogen);
        getTable().center().add(welcomeLabel).center();
        getTable().row();


        TextButton easy = new TextButton("Easy", getSkin());
        easy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.setDifficulty(Config.Difficulty.EASY);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(config));
            }
        });
        getTable().add(easy).padTop(20);
        getTable().row();

        TextButton medium = new TextButton("Medium", getSkin());
        medium.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.setDifficulty(Config.Difficulty.MEDIUM);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(config));
            }
        });
        getTable().add(medium).padTop(20);
        getTable().row();

        TextButton hard = new TextButton("Hard", getSkin());
        hard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.setDifficulty(Config.Difficulty.HARD);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(config));
            }
        });
        getTable().add(hard).padTop(20);
        getTable().row();

        getTable().pack();
    }

    @Override
    public void keyBackPressed() {
        super.keyBackPressed();
        ((Game) Gdx.app.getApplicationListener()).setScreen(new ChooseEnemy(config));
    }
}
