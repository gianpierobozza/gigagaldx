package com.gbozza.android.gigagal;

import com.badlogic.gdx.Game;

public class GigaGalGame extends Game {

	public final static String TAG = GigaGalGame.class.getName();

	@Override
	public void create() {
		setScreen(new GameplayScreen());
	}

}