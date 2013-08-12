package islandescape;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static islandescape.GameConstants.*;
import gamelib.ImageLoader;
import gamelib.Sprite;
import gamelib.TileSet;

import javax.swing.SwingUtilities;

/**
 * Creates a gameworld
 * 
 * @author Christer Byström
 * 
 */

public class Game {
	private static final String DUMMYTEXT = "X";

	private int currentBlock = 1;

	private Sprite tilesprite; // Debug only

	private World world;

	public static void main(String[] args) {
		new Game();
	}

	/**
	 * Starts game
	 */
	public Game() {
		MyMouseListener mouseListener;
		world = new World();
		Input input = new Input(world, this);

		// Sprite to paint map with
		if (DEBUG) {
			tilesprite = new Sprite();
			tilesprite.setText(DUMMYTEXT);
			tilesprite.setPosition(40, 40);
			tilesprite.setOverlay(true);
			world.getFrame().addSprite(tilesprite);
		}

		// Set up listener
		world.setKeyAdapter(input);

		if (DEBUG) {
			mouseListener = new MyMouseListener(world);
		}

		input.setPlayer(world.getPlayer());

		// Hold it until it starts
		while (ImageLoader.totalImages() != ImageLoader.loadedImages()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
			}
		}

		world.titleScreen();
	}

	// For map making, debugging
	/**
	 * Increase current block
	 */
	public void blockUp() {
		currentBlock++;

		if (DEBUG) { // MapMaker
			// Really ugly
			TileSet tileSet;
			tileSet = (TileSet) world.getTileSet();
			tilesprite.setIcon(tileSet.getImage(currentBlock));
		}

	}

	/**
	 * Decrease current block
	 */
	public void blockDown() {
		currentBlock--;

		if (DEBUG) { // MapMaker
			// Really ugly
			TileSet tileSet;
			tileSet = (TileSet) world.getTileSet();
			tilesprite.setIcon(tileSet.getImage(currentBlock));
		}
	}

	// ----------

	// Mouse listener as inner class - used for map making

	/**
	 * @author Christer Byström
	 * 
	 */
	class MyMouseListener implements MouseListener {
		private World world;

		/**
		 * 
		 */
		public MyMouseListener(World world) {

			this.world = world;
			world.addMouseListener(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			// Mouse pressed?
			if (SwingUtilities.isLeftMouseButton(e)
					|| SwingUtilities.isRightMouseButton(e)) {
				// Get tile
				MyTile tile = world.getTileFromPoint(e.getPoint());
				// Got tile?
				if (tile != null) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						tile.setBlock(currentBlock);
					}
					if (SwingUtilities.isRightMouseButton(e)) {
						currentBlock = tile.getBlock();
						blockUp();
						blockDown();
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
