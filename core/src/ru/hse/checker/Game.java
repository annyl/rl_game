package ru.hse.checker;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ru.hse.checker.screen.GameScreen;
import ru.hse.checker.utils.CheckersAM;

public class Game extends com.badlogic.gdx.Game {
	BitmapFont font;

	@Override
	public void create () {
		font = new BitmapFont();
		this.setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		font.dispose();
		CheckersAM.getInstance().dispose();
	}
}
