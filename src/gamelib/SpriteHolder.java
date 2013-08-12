package gamelib;

import java.util.LinkedList;

/**
 * Interface for an object holding sprites
 * 
 * @author Christer Byström
 * 
 */
public interface SpriteHolder {
	/**
	 * Add sprite to holder
	 * 
	 * @param sprite
	 */
	public void addSprite(Sprite sprite);

	/**
	 * Remove sprite from holder
	 * 
	 * @param sprite
	 */
	public void removeSprite(Sprite sprite);

	/**
	 * Get list of all sprites, called when you want them
	 * 
	 * @return
	 */
	public LinkedList<Sprite> lockSprites();

	/**
	 * Release sprites
	 */
	void releaseSprites();
}
