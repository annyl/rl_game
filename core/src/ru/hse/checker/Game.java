package ru.hse.checker;


import ru.hse.checker.screen.IntroScreen;
import ru.hse.checker.utils.CheckersAM;

public class Game extends com.badlogic.gdx.Game {

	@Override
	public void create () {
		CheckersAM.getInstance().loadResources();
		this.setScreen(new IntroScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		CheckersAM.getInstance().dispose();
	}
}
