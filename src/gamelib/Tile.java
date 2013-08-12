package gamelib;

/**
 * Interface for the tile in the tileset
 * 
 * @author Christer Byström
 * 
 */
public interface Tile {
	/**
	 * @return number of block
	 */
	public int getBlock();

	/**
	 * @param block
	 *            number of block
	 */
	public void setBlock(int block);
}
