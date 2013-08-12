package islandescape;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static islandescape.GameConstants.*;

/**
 * @author Christer Byström
 * 
 */
public class Input extends KeyAdapter {
	private Actor player;

	private World world;

	private Game game; // Ugly

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		if (world.waitingForKey()) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (DEBUG) {
					System.out.println("Waiting for SPACE, got SPACE!");
				}

				world.onSpaceKey();

			} else {
				if (DEBUG) {
					System.out.println("Waiting for SPACE, got another key!");
				}
			}
			return;
		}

		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			if (player != null) {
				player.keyPress(e.getKeyCode());
			}
			break;
		case KeyEvent.VK_ESCAPE:
			world.resetToTitle();
			// world.close();
			break;
		case KeyEvent.VK_S:
			if (DEBUG) {
				// Save map
				world.saveMap();
			}
			break;
		case KeyEvent.VK_PLUS:
			if (DEBUG) {
				game.blockUp();
			}
			break;
		case KeyEvent.VK_MINUS:
			if (DEBUG) {
				game.blockDown();
			}
			break;
		case KeyEvent.VK_P:
			if (DEBUG) {
				if (player != null) {
					world.setStartPos(player.whatExitIsThis(), player
							.getTileX(), player.getTileY());
				}
			}
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (world.waitingForKey()) {
			return;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			player.keyRelease(e.getKeyCode());
			break;

		}

	}

	/**
	 * @param player
	 *            that is controlled by this input
	 */
	public void setPlayer(Actor player) {
		this.player = player;
	}

	/**
	 * @param world
	 *            where I belong
	 * @param game
	 *            where I belong
	 */
	public Input(World world, Game game) {
		this.world = world;
		this.game = game;
	}
}
