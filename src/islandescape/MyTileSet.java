package islandescape;

import gamelib.ListReader;
import gamelib.TileSet;
import gamelib.ImageLoader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.ImageIcon;

import static islandescape.GameConstants.*;

/**
 * Tileset that holds imagetiles and info about tiles, such as if they are solid
 * or if they are exits/doors
 * 
 * @author Christer Byström
 * 
 */
public class MyTileSet implements TileSet {
	private static final String STRINGSEMICOLON = ";";

	private static final String STRINGEMPTY = "";

	private static final String TILES_TXT = "tiles.txt";

	private Hashtable tiles;

	private String basedir = "gfx/tiles/";

	/**
	 * Load file with tiles
	 */
	public MyTileSet() {
		tiles = new Hashtable();
		ArrayList<String> files = new ListReader(TILES_TXT).getStrings();

		Iterator<String> i = files.iterator();

		while (i.hasNext()) {
			makeTile(i.next());
		}

	}

	/**
	 * @param index
	 * @param filename
	 * @param solid
	 */
	private void makeTile(int index, String filename, Boolean solid) {
		tiles.put(index, new PrivTile(basedir + filename, solid, false, false));
	}

	/**
	 * @param index
	 * @param filename
	 * @param solid
	 * @param door
	 */
	private void makeTile(int index, String filename, Boolean solid,
			Boolean door) {
		tiles.put(index, new PrivTile(basedir + filename, solid, door, false));
	}

	/**
	 * @param index
	 * @param filename
	 * @param solid
	 * @param door
	 * @param endofgame
	 */
	private void makeTile(int index, String filename, Boolean solid,
			Boolean door, Boolean endofgame) {
		tiles.put(index, new PrivTile(basedir + filename, solid, door,
				endofgame));
	}

	/**
	 * Make tile from ini entry
	 * 
	 * @param entry
	 */
	private void makeTile(String entry) {
		String index = STRINGEMPTY;
		String filename = STRINGEMPTY;
		String solid = STRINGEMPTY;
		String door = STRINGEMPTY;
		String endofgame = STRINGEMPTY;

		String strings[] = entry.split(STRINGSEMICOLON);

		if (strings.length > 0) {
			index = strings[0].trim();
		}

		if (strings.length > 1) {
			filename = strings[1].trim();
		}
		if (strings.length > 2) {
			solid = strings[2].trim();
		}
		if (strings.length > 3) {
			door = strings[3].trim();
		}

		if (strings.length > 4) {
			endofgame = strings[4].trim();
		}

		makeTile(new Integer(index), filename, toBoolean(solid),
				toBoolean(door), toBoolean(endofgame));
	}

	/**
	 * @param text
	 * @return
	 */
	private Boolean toBoolean(String text) {
		text = text.trim();
		if (text.length() > 0) {
			int i;
			try {
				i = new Integer(text).intValue();
			} catch (Exception e) {
				i = 0;
			}
			if (i > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param index
	 * @return
	 */
	public ImageIcon getImage(int index) {
		PrivTile tile = (PrivTile) tiles.get(index);
		if (tile != null) {
			return tile.getImage();
		}

		return null;
	}

	/**
	 * Check if tile is solid
	 * 
	 * @param index
	 *            of tile
	 * @return true if tile is solid
	 */
	public Boolean isSolid(int index) {
		PrivTile tile = (PrivTile) tiles.get(index);
		if (tile != null) {
			return tile.isSolid();
		}

		return false;

	}

	/**
	 * Check if tile is end of game
	 * 
	 * @param index
	 *            of tile
	 * @return true if end of game tile
	 */
	public Boolean isEndOfGame(int index) {
		PrivTile tile = (PrivTile) tiles.get(index);
		if (tile != null) {
			return tile.isEndOfGame();
		}

		return false;
	}

	/**
	 * @param index
	 * @return
	 */
	public Boolean isDoor(int index) {
		PrivTile tile = (PrivTile) tiles.get(index);
		if (tile != null) {
			return tile.isDoor();
		}
		return false;
	}

	/**
	 * Hold the tile and information about the tile
	 * 
	 * @author Christer Byström
	 * 
	 */
	private class PrivTile {
		private ImageIcon image;

		private Boolean solid = false;

		private Boolean door = false;

		private Boolean endofgame = false;

		/**
		 * @param file
		 * @param solid
		 * @param door
		 * @param solid
		 */
		public PrivTile(String file, Boolean solid, Boolean door,
				Boolean endofgame) {
			image = ImageLoader.loadImage(file);
			this.solid = solid;
			this.door = door;
			this.endofgame = endofgame;
		}

		/**
		 * @return - image
		 */
		ImageIcon getImage() {
			return image;
		}

		/**
		 * @return - true if solid
		 */
		public Boolean isSolid() {
			return solid;
		}

		public Boolean isDoor() {
			return door;
		}

		public Boolean isEndOfGame() {
			return endofgame;
		}
	}
}
