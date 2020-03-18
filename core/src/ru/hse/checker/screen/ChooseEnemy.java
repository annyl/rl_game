package ru.hse.checker.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.hse.checker.Game;
import ru.hse.checker.model.Config;

public class ChooseEnemy extends UIScreen {

    private Config config;

    ChooseEnemy(Config config) {
        this.config = config;
    }

    @Override
    public void show() {
        Label welcomeLabel = new Label("Choose mod", getSkin());
        Label.LabelStyle halogen = new Label.LabelStyle(getSkin().getFont("halogen"), getSkin().getColor("color"));
        welcomeLabel.setStyle(halogen);
        getTable().center().add(welcomeLabel).center();
        getTable().row();


        TextButton twoPlayers = new TextButton("Two players", getSkin());
        twoPlayers.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.setVsAI(false);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(config));
            }
        });
        getTable().add(twoPlayers).pad(20);
        getTable().row();

        TextButton vsAI = new TextButton("AI", getSkin());
        vsAI.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                config.setVsAI(true);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ChooseDifficulty(config));
            }
        });
        getTable().add(vsAI).pad(1);

        getTable().pack();
    }

    @Override
    public void keyBackPressed() {
        super.keyBackPressed();
        ((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen());
    }
}
