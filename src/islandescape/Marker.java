package islandescape;

import java.awt.Color;

/*
 * Used for debugging purposes to show breadcrumb of thief
 * @author Christer Byström
 *
 */
public class Marker extends Actor {
	private static int c = 0;

	/**
	 * @param room
	 */
	public Marker(Room room) {
		super(room);
		setColor(new Color(c, c, c));

		c = (c + 1) % 255;
	}

}
