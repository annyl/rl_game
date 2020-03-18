package ru.hse.checker.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.hse.checker.Game;
import ru.hse.checker.model.Config;

public class IntroScreen extends UIScreen {

    private Config config = new Config();

    @Override
    public void show() {
        Label welcomeLabel = new Label("Welcome to", getSkin());
        Label.LabelStyle halogen = new Label.LabelStyle(getSkin().getFont("halogen"), getSkin().getColor("color"));
        welcomeLabel.setStyle(halogen);
        getTable().center().add(welcomeLabel).center();
        getTable().row();

        Label NeonCheckers = new Label("Neon Checkers", getSkin());
        Label.LabelStyle las = new Label.LabelStyle(getSkin().getFont("las"), getSkin().getColor("text"));
        NeonCheckers.setStyle(las);
        getTable().add(NeonCheckers);
        getTable().row();

        TextButton textButton = new TextButton("Play", getSkin());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ChooseEnemy(config));
            }
        });
        getTable().add(textButton).pad(50);

        getTable().pack();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void keyBackPressed() {
        super.keyBackPressed();
        Gdx.app.exit();
    }
}
