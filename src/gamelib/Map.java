package gamelib;

/**
 * Interface for map
 * 
 * @author Christer Byström
 */
public interface Map {
	public Tile[][] getTiles();

	public int getWidth();

	public int getHeight();
}
