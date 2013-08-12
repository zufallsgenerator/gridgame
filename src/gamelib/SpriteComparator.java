/**
 * 
 */
package gamelib;

import java.util.Comparator;

/**
 * 
 * @author Christer Byström
 * 
 */
public class SpriteComparator implements Comparator {

	/**
	 * Comparator for sorting sprites with respect to their vertical position
	 */
	public SpriteComparator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc) This one is sorting the one with lowest Y position first
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(Object arg0, Object arg1) {
		Sprite s1 = (Sprite) arg0;
		Sprite s2 = (Sprite) arg1;

		if (s1.getY() > s2.getY()) {
			return 1;
		}
		if (s1.getY() < s2.getY()) {
			return -1;
		}
		return 0;
	}

}
