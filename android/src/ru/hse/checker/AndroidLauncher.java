package ru.hse.checker;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ru.hse.checker.model.intelligence.AIPlayer;
import ru.hse.checker.model.intelligence.Player;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		AIPlayer.setAndroidAi(new AndroidAI(this, Player.NumPlayer.SECOND)); //todo: rewrite model: board <- player <-logic
		initialize(new Game(), config);
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
	}
}
