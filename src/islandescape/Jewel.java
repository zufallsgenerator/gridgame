package islandescape;

import gamelib.AnimFrames;

/**
 * Collectable, gives score
 * 
 * @author Christer Byström
 * 
 */
public class Jewel extends Actor {
	private static final String JEWEL1 = "jewel lightblue%04d.bmp.gif";

	private static final String GFX_JEWEL = "gfx/jewel/";

	private int counter;

	/**
	 * @param room
	 */
	public Jewel(Room room) {
		super(room);
		loadMove(GFX_JEWEL, JEWEL1, JEWEL1, JEWEL1, JEWEL1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#doIntelligence()
	 */
	public void doIntelligence() {
		if ((counter & 2) == 0) {
			AnimFrames frames = getFrames();
			if (frames != null) {
				setIcon(frames.nextUp());
			}
		}

		counter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#isCollectable()
	 */
	public Boolean isCollectable() {
		return true;
	}

}
