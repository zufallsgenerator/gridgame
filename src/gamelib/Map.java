package gamelib;

/**
 * Interface for map
 * 
 * @author Christer Bystr�m
 */
public interface Map {
	public Tile[][] getTiles();

	public int getWidth();

	public int getHeight();
}
