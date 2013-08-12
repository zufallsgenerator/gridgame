/**
 * 
 */
package gamelib;

import java.awt.MediaTracker;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;

/**
 * A cache and a load finishing notifier for ImageIcons
 * 
 * A cheap and easy way to avoid multiple loads of the same files
 * 
 * @author Christer Byström
 * 
 */
public class ImageLoader {
	private static Hashtable images = null;

	private static Hashtable imagesfailed = null;


	/**
	 * 
	 */
	public ImageLoader() {
	}

	/**
	 * @param file
	 * @return
	 */
	static public ImageIcon loadImage(String file) {
		ImageIcon icon = null;

		// Create static hashtable
		if (images == null) {
			images = new Hashtable();
		}

		if (!images.containsKey(file)) {
			// System.out.println("Loading: " + file);
			// icon = new ImageIcon(file);
			byte buf[] = null;

			buf = ResourceLoader.load(file);
			if (buf == null) {
//				System.out.println("Failed loading resource " + file + "!");
				icon = new ImageIcon();
			} else {
				icon = new ImageIcon(buf); // Empty icon
			}
			images.put(file, icon);
		} else {
			// System.out.println("Found in cache: " + file);
			icon = (ImageIcon) images.get(file);
		}

		return icon;
	}

	/**
	 * @return - total number of images
	 */
	static public int totalImages() {
		if (images != null) {
			return images.size();
		}

		return 0;
	}

	/**
	 * @param filename
	 * @return
	 */
	static public Boolean exists(String filename) {
		if (imagesfailed == null) {
			imagesfailed = new Hashtable();
		} else {
			if (imagesfailed.containsKey(filename)) {
				// System.out.println("Caught unnecessary filesystem access " +
				// filename);
				return false;
			}
		}

		if (images != null) {
			if (images.containsKey(filename)) {
				return true;
			}
		} else {
			// Create static hashtable
			images = new Hashtable();
		}

		

		/*
		 * File f = new File(filename);
		 * 
		 * if (!f.canRead()) { imagesfailed.put(filename, filename); //
		 * System.out.println("File non-existant: " + filename); //
		 * System.out.println("SIZE: " + imagesfailed.size()); return false; }
		 */


		
		if (!ResourceLoader.exists(filename)) {
			imagesfailed.put(filename, filename);

			// System.out.println("File non-existant: " + filename);
			// System.out.println("SIZE: " + imagesfailed.size());
			return false;
		}

		return true;
	}

	/**
	 * @return - number of loaded images (images not loading, like aborted or
	 *         completed)
	 */
	static public int loadedImages() {
		if (images != null) {
			int c = 0;
			Enumeration e = images.elements();

			while (e.hasMoreElements()) {
				ImageIcon image = (ImageIcon) e.nextElement();
				if (image.getImageLoadStatus() != MediaTracker.LOADING) {
					c++;
				}
			}

			return c;
		}

		return 0;

	}

}
