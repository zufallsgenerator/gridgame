/**
 * 
 */
package islandescape;

import static islandescape.GameConstants.*;

import java.awt.event.KeyEvent;

/**
 * The player
 * 
 * @author Christer Byström
 */
public class Player extends Actor {
	private static final String PLAYER4 = "walking e%04d.bmp.gif";

	private static final String PLAYER3 = "walking w%04d.bmp.gif";

	private static final String PLAYER2 = "walking s%04d.bmp.gif";

	private static final String PLAYER1 = "walking n%04d.bmp.gif";

	private static final String GFX_PLAYER = "gfx/player/";

	private String onDoor = null;

	private int score;

	private final int HP_START = 7;

	private int hp;

	private World world;

	private int blinking = 0;

	private int lastKey = 0;

	/**
	 * @param room
	 *            where I belong
	 */
	public Player(Room room, World world) {
		super(room);
		this.world = world;
		// Now load frames
		loadMove(GFX_PLAYER, PLAYER1, PLAYER2, PLAYER3, PLAYER4);

		reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#doIntelligence()
	 */
	public void doIntelligence() {

		// if (DEBUG) {
		// System.out.println("Pos: " + getTileX() + "." + getTileY());
		// }

		checkCollisions();
		if (blinking > 0) {
			blinking--;

			if ((blinking & 2) == 0) {
				setVisible(false);
			} else {
				setVisible(true);
			}

			if (blinking == 0) {
				setVisible(true);
			}
		}

		// Check for movements
		if (lastKey != 0) {
			switch (lastKey) {
			case KeyEvent.VK_UP:
				moveUp();
				break;
			case KeyEvent.VK_DOWN:
				moveDown();
				break;
			case KeyEvent.VK_LEFT:
				moveLeft();
				break;
			case KeyEvent.VK_RIGHT:
				moveRight();
				break;
			}
		}

		// Scroll myself to center
		if (this.world != null) {
			world.setScrollCenter(getX(), getY());

		}

		if (DEBUG) {
			whoThere();
		}

		// Finally, see if we are to change rooms
		if (onDoor != null) {
			world.changeRoom(onDoor);
			onDoor = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#reset()
	 */
	public void reset() {
		score = 0;
		hp = HP_START;
		world.setHP(hp, HP_START);
		world.setScore(score);
		blinking = 30;
		setIcon(getFrames().nextDown());
		setVisible(true);
		resetMovement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#collisionHandler(tilegame.Actor)
	 */
	public Boolean collisionHandler(Actor actor) {
		// Am I hit, and no blinking?
		if (actor.isEnemy()) {
			if (blinking < 1) {
				hp -= actor.damage();
				world.setHP(hp, HP_START);
				// Do blink for a while if hurt
				if (actor.damage() > 0) {
					blinking = 30;
				}
			}

			// Am I dead???
			if (hp <= 0) {
				world.gameover(false);
				return false;
			}

			// And move away from enemy
			switch (actor.getMove()) {
			case MOVE_UP:
				forceUp();
				break;
			case MOVE_DOWN:
				forceDown();
				break;
			case MOVE_LEFT:
				forceLeft();
				break;
			case MOVE_RIGHT:
				forceRight();
				break;
			}

		}

		// Healer
		if (actor.isHealer() && actor.isReady()) {

			if (hp < HP_START) {
				hp = HP_START;
				actor.regenerate();
				world.setHP(hp, HP_START);
			}
		}

		if (actor.isCollectable()) {
			score += 100;
			world.setScore(score);
			actor.remove();
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#onDoor()
	 */
	public void onDoor(String door) {
		// Switch worlds!
		if (DEBUG) {
			System.out.println("DOOR: " + door);
		}
		onDoor = door;
	}

	/**
	 * called upon when game is ended, when reaching the end of game tile
	 */
	public void onEndOfGame() {
		world.gameover(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#resetMovement()
	 */
	public void resetMovement() {
		super.resetMovement();
		lastKey = 0;
	}

	/**
	 * @param key
	 */
	public void keyPress(int key) {
		lastKey = key;
	}

	/**
	 * @param key
	 */
	public void keyRelease(int key) {
		if (lastKey == key) {
			lastKey = 0;
		}
	}

}
