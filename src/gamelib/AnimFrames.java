package gamelib;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

/**
 * Basically just a specialised image list with iterators to support animations
 * 
 * @author Christer Byström
 * 
 */
public class AnimFrames {

	private Boolean loaded = false;

	private Hashtable customAnimations;

	private Hashtable customIterators;

	private ArrayList<ImageIcon> up;

	private ArrayList<ImageIcon> down;

	private ArrayList<ImageIcon> left;

	private ArrayList<ImageIcon> right;

	private ImageIcon still = null;

	private int upIndex, downIndex, leftIndex, rightIndex;

	/**
	 * 
	 */
	public AnimFrames() {
		up = new ArrayList<ImageIcon>();
		down = new ArrayList<ImageIcon>();
		left = new ArrayList<ImageIcon>();
		right = new ArrayList<ImageIcon>();
		customAnimations = new Hashtable();
		customIterators = new Hashtable();
	}

	/**
	 * Load static frame
	 * 
	 * @param basePath -
	 *            base for images
	 * @param file -
	 *            image
	 * @return
	 */
	public Boolean loadStill(String basePath, String file) {
		try {
			still = ImageLoader.loadImage(basePath + file);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @return - static image
	 */
	public ImageIcon getStill() {
		return still;
	}

	/**
	 * Reset custom animation
	 * 
	 * @param id
	 */
	public void customStart(String id) {
		if (customIterators.containsKey(id)) {
			customIterators.put(id, 0);
		}
	}

	/**
	 * @param id
	 *            of animation
	 * @return true if next frame is end of animation
	 */
	public Boolean customNextIsEnd(String id) {
		if (!customIterators.containsKey(id)
				|| !customAnimations.containsKey(id)) {
			return true;
		}

		Integer myInt = (Integer) customIterators.get(id);
		int index = myInt.intValue();
		ArrayList<ImageIcon> frames = (ArrayList<ImageIcon>) customAnimations
				.get(id);
		// System.out.println("" + index + " - " + frames.size());
		if (index >= frames.size() - 1) {
			return true;
		}

		return false;
	}

	/**
	 * @param id
	 *            of custom animation
	 * @return next frame in animation
	 */
	public ImageIcon customNext(String id) {
		if (!customIterators.containsKey(id)
				|| !customAnimations.containsKey(id)) {
			return null;
		}

		Integer myInt = (Integer) customIterators.get(id);
		int index = myInt.intValue();
		ArrayList<ImageIcon> frames = (ArrayList<ImageIcon>) customAnimations
				.get(id);

		if (frames != null && frames.size() > 0) {
			// Get
			if (index < 0) {
				index = 0;
			} else {
				index++;
				if (index >= frames.size()) {
					index = 0;
				}
			}

			// Case we haven't loaded all
			if (index > frames.size()) {
				return null;
			}

			customIterators.put(id, index);

			return frames.get(index);
		}
		return null;
	}

	/**
	 * @param id
	 *            of custom animation
	 * @param basePath
	 * @param pattern
	 * @return
	 */
	public Boolean loadCustom(String id, String basePath, String pattern) {
		Boolean loop = true;
		ImageIcon icon = null;
		int i = 0;
		ArrayList<ImageIcon> dir = new ArrayList<ImageIcon>();

		while (loop) {
			// Make a path from the path, say that base path is
			// "player/" and pattern is "moven%4d.gif" then result
			// is "player/moven0000.gif","player/moven0001.gif" etc
			String filename = basePath + String.format(pattern, i);
			// System.out.println(filename);
			if (ImageLoader.exists(filename)) {

				try {
					icon = ImageLoader.loadImage(filename);
				} catch (Exception e) {
					loop = false;
				}
				if (loop) {
					dir.add(icon);
				}
				i++;
			} else {
				loop = false;
			}
		}

		customAnimations.put(id, dir);
		customIterators.put(id, 0);

		return true;
	}

	/**
	 * Load animations for movement
	 * 
	 * Provide a base path and formatting strings for loading images with names
	 * containing increasing numbers
	 * 
	 * @param basePath
	 *            of animations
	 * @param strup
	 *            formatting for up
	 * @param strdown
	 *            formatting for down
	 * @param strleft
	 *            formatting for left
	 * @param strright
	 *            formatting for right
	 * @return
	 */
	public Boolean loadMove(String basePath, String strup, String strdown,
			String strleft, String strright) {

		String filename;
		Boolean loop;
		int i;
		ImageIcon icon = null;
		LinkedList<String> patterns = new LinkedList<String>();
		LinkedList<ArrayList<ImageIcon>> dirs = new LinkedList<ArrayList<ImageIcon>>();
		Iterator<ArrayList<ImageIcon>> dirIter;
		Iterator<String> patternIter;

		// Set flag as loaded
		this.loaded = true;

		// Make two lists: one with paths/patterns and one with frame containers
		patterns.add(strup);
		patterns.add(strdown);
		patterns.add(strleft);
		patterns.add(strright);

		dirs.add(up);
		dirs.add(down);
		dirs.add(left);
		dirs.add(right);

		dirIter = dirs.iterator();
		patternIter = patterns.iterator();

		// Match patterns/directions
		while (dirIter.hasNext() && patternIter.hasNext()) {
			String pattern = patternIter.next();
			ArrayList<ImageIcon> dir = dirIter.next();
			loop = true;
			i = 0;
			while (loop) {
				// Make a path from the path, say that base path is
				// "player/" and pattern is "moven%4d.gif" then result
				// is "player/moven0000.gif","player/moven0001.gif" etc
				filename = basePath + String.format(pattern, i);
				// System.out.println(filename);
				if (ImageLoader.exists(filename)) {

					try {
						icon = ImageLoader.loadImage(filename);
					} catch (Exception e) {
						loop = false;
					}
					if (loop) {
						// TODO: How do you parameterize this?
						dir.add(icon);
					}
					i++;
				} else {
					loop = false;
				}
			}

		}

		return true;
	}

	/**
	 * @return true if walking frames are loaded
	 */
	public Boolean isWalkLoaded() {
		return loaded;
	}

	/**
	 * @return next frame for upwards movement
	 */
	public ImageIcon nextUp() {
		if (up != null && up.size() > 0) {
			// Get
			if (upIndex < 0) {
				upIndex = 0;
			} else {
				upIndex++;
				if (upIndex >= up.size()) {
					upIndex = 0;
				}
			}

			// Case we haven't loaded all
			if (upIndex > up.size()) {
				return null;
			}

			return up.get(upIndex);
		}
		return null;
	}

	/**
	 * @return next frame for downwards movement
	 */
	public ImageIcon nextDown() {
		if (down != null && down.size() > 0) {
			// Get
			if (downIndex < 0) {
				downIndex = 0;
			} else {
				downIndex++;
				if (downIndex >= down.size()) {
					downIndex = 0;
				}
			}

			// Case we haven't loaded all
			if (downIndex > down.size()) {
				return null;
			}

			return down.get(downIndex);
		}
		return null;
	}

	/**
	 * @return next frame for left movement
	 */
	public ImageIcon nextLeft() {
		if (left != null && left.size() > 0) {
			// Get
			if (leftIndex < 0) {
				leftIndex = 0;
			} else {
				leftIndex++;
				if (leftIndex >= left.size()) {
					leftIndex = 0;
				}
			}

			// Case we haven't loaded all
			if (leftIndex > left.size()) {
				return null;
			}

			return left.get(leftIndex);
		}
		return null;
	}

	/**
	 * @return next frame for right movement
	 */
	public ImageIcon nextRight() {
		if (right != null && right.size() > 0) {
			// Get
			if (rightIndex < 0) {
				rightIndex = 0;
			} else {
				rightIndex++;
				if (rightIndex >= right.size()) {
					rightIndex = 0;
				}
			}

			// Case we haven't loaded all
			if (rightIndex > right.size()) {
				return null;
			}

			return right.get(rightIndex);
		}
		return null;
	}
}
