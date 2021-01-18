package io.swapastack.gomoku;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class Gomoku extends Game {

	/**
	 * This function returns the dimensions of the client area
	 * in logical screen pixels.
	 * see: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Graphics.html
	 *
	 * @return {@link Tuple}
	 * @author Dennis Jehle
	 */
	public Tuple<Integer> get_window_dimensions() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		System.out.printf("client area (window) dimensions:\n\twidth: %d\n\theight: %d\n", width, height);
		return new Tuple<Integer>(width, height);
	}

	/**
	 * This function can be used to switch screens.
	 *
	 * @param screen {@link ScreenEnum}
	 * @author Dennis Jehle
	 */
	public void change_screen(ScreenEnum screen) {
		if (screen.equals(ScreenEnum.MENU)) {
			this.setScreen(new MainMenuScreen(this));
		}
		else if (screen.equals(ScreenEnum.GAME)) {
			this.setScreen(new GameScreen(this));
		}
		else if (screen.equals(ScreenEnum.RANKING)) {
			this.setScreen(new RankingScreen(this));
		}
	}

	@Override
	public void create () {
		// set window title
		Gdx.graphics.setTitle("Gomoku - 五目並べ - Sopra 2020 / 2021");
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}
}
