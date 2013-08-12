package gamelib;

import javax.swing.ImageIcon;

/**
 * Interface for the tileset so that the graphical layers need to know nothing
 * about the rest
 * 
 * @author Christer Byström
 * 
 */
public interface TileSet {
	/**
	 * @param index
	 *            of desired image
	 * @return image
	 */
	public ImageIcon getImage(int index);
}
