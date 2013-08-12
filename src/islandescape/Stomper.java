package islandescape;

import gamelib.AnimFrames;

import static islandescape.GameConstants.*;

/**
 * Moves randomly by teleportation and hurts player
 * 
 * @author Christer Byström
 */
public class Stomper extends Actor {
	private static final String ANGRY1 = "angry%04d.bmp.gif";

	private static final String GFX_STOMPER = "gfx/stomper/";

	private int counter = 0;

	private int secondsbetween = 3;

	private int blinking = 0;

	/**
	 * @param room -
	 *            where I belong
	 */
	public Stomper(Room room) {
		super(room);
		loadMove(GFX_STOMPER, ANGRY1, ANGRY1, ANGRY1, ANGRY1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Actor#doIntelligence()
	 */
	public void doIntelligence() {
		if (counter <= 0) {
			setRandomTilePos();
			counter = secondsbetween * TICKS_PER_SECOND;
		}

		if (counter == (secondsbetween * TICKS_PER_SECOND) / 8) {
			blinking = (secondsbetween * TICKS_PER_SECOND) / 4;
		}

		// Do blinking
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

		if ((counter & 2) == 0) {
			AnimFrames frames = getFrames();
			if (frames != null) {
				setIcon(frames.nextDown());
			}
		}

		counter--;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#reset()
	 */
	public void reset() {
		counter = blinking = 0;
		super.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Actor#isEnemy()
	 */
	public Boolean isEnemy() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Actor#damage()
	 */
	public int damage() {
		if (blinking > 0) {
			return 0;
		}

		return 3; // Do MUCH damage, but only if not blinking
	}
}
