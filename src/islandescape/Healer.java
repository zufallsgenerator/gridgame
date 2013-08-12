/**
 * 
 */
package islandescape;

import gamelib.AnimFrames;
import gamelib.ImageLoader;

import static islandescape.GameConstants.*;

/**
 * Healer actor that restores player HP
 * 
 * @author Christer Byström
 * 
 */
public class Healer extends Actor {
	private int regenerate = 0;

	private static final String HEALERSPELL = "healerspell";

	private static final String PATTERN = "gfx/healer/magic spelling s0000.bmp.gif";

	private static final String BASEDIR = "gfx/healer/";

	private static final String FIRSTFRAME = "magic spelling s%04d.bmp.gif";

	private Boolean animation = false;

	/**
	 * @param room
	 */
	public Healer(Room room) {
		super(room);
		setIcon(ImageLoader.loadImage(PATTERN));
		AnimFrames frames = getFrames();
		frames.loadCustom(HEALERSPELL, BASEDIR, FIRSTFRAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#isHealer()
	 */
	public Boolean isHealer() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#doIntelligence()
	 */
	public void doIntelligence() {
		if (regenerate > 0) {
			if ((regenerate & 3) == 0) {
				if (animation) {
					if (getFrames().customNextIsEnd(HEALERSPELL)) {
						animation = false;
					}
					setIcon(getFrames().customNext(HEALERSPELL));
				}
			}

			// setIcon(ImageLoader.loadImage("gfx/healer/magic spelling
			// s0001.bmp.gif"));
			regenerate--;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#isReady()
	 */
	public Boolean isReady() {
		if (regenerate == 0) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#regenerate()
	 */
	public void regenerate() {
		regenerate = TICKS_PER_SECOND * 10; // Ten seconds to regenerate
		// Animate
		getFrames().customStart(HEALERSPELL);
		animation = true;
	}
}
