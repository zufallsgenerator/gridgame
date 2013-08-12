package islandescape;

import gamelib.ImageLoader;
import gamelib.Map;
import gamelib.Sprite;
import gamelib.SpriteHolder;
import gamelib.Tile;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static islandescape.GameConstants.*;

public class Room implements Map, SpriteHolder {
	private static final String STOMPERS = "stompers";

	private static final String HEALERS = "healers";

	private static final String THIEFS = "thiefs";

	private static final String JEWELS = "jewels";

	private static final String CROCKS = "crocks";

	private int height;

	private Properties props;

	private MyTileSet tileSet;

	private ReentrantReadWriteLock spriteLock;// Lock for sprites

	private LinkedList<Sprite> sprites;

	private int width;

	private LinkedList<Actor> actors; // Actors in room

	private String name = "";

	private String filename = "";

	private MyTile[][] tiles;

	/**
	 * @param height
	 *            of room
	 * @param width
	 *            of room
	 * @param tileSet
	 *            to use
	 */
	public Room(int height, int width, MyTileSet tileSet) {
		this.height = height;
		this.width = width;
		this.tileSet = tileSet;
		this.spriteLock = new ReentrantReadWriteLock();
		this.sprites = new LinkedList<Sprite>();

		actors = new LinkedList<Actor>();

		// Create an array of tiles
		tiles = new MyTile[height][width];
		// Populate array
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new MyTile(tileSet);
			}
		}
	}

	/**
	 * Create enemies and objects
	 */
	private void initActors() {
		
		// Create enemies
		for (int i = getIntProp(CROCKS, 5); i > 0; i--) {
			new Crock(this).setRandomTilePos();
		}

		for (int i = getIntProp(JEWELS, 3); i > 0; i--) {
			new Jewel(this).setRandomTilePos();
		}

		for (int i = getIntProp(THIEFS, 0); i > 0; i--) {
			new Thief(this).setRandomTilePos();
		}

		for (int i = getIntProp(HEALERS, 0); i > 0; i--) {
			new Healer(this).setRandomTilePos();
		}

		for (int i = getIntProp(STOMPERS, 1); i > 0; i--) {
			new Stomper(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.SpriteHolder#getAllSprites()
	 */
	public LinkedList<Sprite> lockSprites() {
		spriteLock.readLock().lock();
		return sprites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.SpriteHolder#releaseSprites()
	 */
	public void releaseSprites() {
		spriteLock.readLock().unlock();
	}

	/**
	 * Reset room, reset all actors
	 */
	public void reset() {
		while (actors.size() > 0) {
			// The actors asks the current room (this) to be removed
			actors.getFirst().remove();
		}

		initActors(); // Create new
	}

	/**
	 * @return name of room
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param door
	 * @return name of room connected with door
	 */
	public String nextRoom(String door) {
		return props.getProperty(door);
	}

	// Interface methods

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.FrameMap#getTiles()
	 */
	public Tile[][] getTiles() {
		return (Tile[][]) tiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.FrameMap#getWidth()
	 */
	public int getWidth() {
		return this.width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.FrameMap#getHeight()
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Send update to all actors in room
	 */
	public void updateActors() {
		Iterator i = actors.iterator();
		while (i.hasNext()) {
			try {
				Actor a = (Actor) i.next();
				a.update();
			} catch (ConcurrentModificationException exception) {
				// What to do? Well start over, cause actors might wanna remove
				// themselves
				// MaybeTODO: Do it nicer, don't remove actors, just mark them
				// as
				// dead
				i = actors.iterator();
			}
		}
	}

	/**
	 * @return all actors in room
	 */
	public LinkedList getActors() {
		return actors;
	}

	/**
	 * @param actor
	 *            to add to room
	 */
	public void addActor(Actor actor) {
		Room oldRoom;

		oldRoom = actor.getRoom();

		if (oldRoom != null) {
			oldRoom.removeActor(actor);
		}

		actor.setRoom(this);

		actors.add(actor);
		if (actor.getSprite() != null) {
			addSprite(actor.getSprite());
		} else {
			if (DEBUG) {
				System.out.println("Room.addActor(): NO SPRITE!!!");
			}
		}
	}

	/**
	 * @param actor
	 *            to remove from room
	 */
	public void removeActor(Actor actor) {
		if (actor.getSprite() != null) {
			removeSprite(actor.getSprite());
		}
		actors.remove(actor);
	}

	/**
	 * @param xPos
	 * @param yPos
	 * @return tile at position
	 */
	public MyTile getTile(int xPos, int yPos) {
		if (xPos >= 0 && yPos >= 0 && xPos < width && yPos < height) {
			return tiles[xPos][yPos];
		} else {
			return null;
		}
	}

	/**
	 * @param prop
	 * @return property as integer
	 */
	private int getIntProp(String prop) {
		return getIntProp(prop, -1);
	}

	/**
	 * @param prop
	 * @param defval
	 *            default value
	 * @return
	 */
	private int getIntProp(String prop, int defval) {
		int i;
		try {
			i = new Integer(props.getProperty(prop));
		} catch (Exception e) {
			i = defval;
		}

		return i;
	}

	/**
	 * @param prop
	 *            string of value to set
	 * @param value -
	 *            integer
	 */
	private void setIntProp(String prop, int value) {
		props.setProperty(prop, "" + value);
	}

	/**
	 * @param fromroom -
	 *            room where we are coming from
	 * @return - start position
	 */
	public Point getStartPos(String fromroom) {
		int x = -1;
		int y = -1;

		if (fromroom != null) {
			x = getIntProp("start-x-" + fromroom);
			y = getIntProp("start-y-" + fromroom);
		} else {
			x = getIntProp("start-x");
			y = getIntProp("start-y");
		}

		if (x == -1 || y == -1) {
			x = width / 2;
			y = height / 2;
		}

		return new Point(x, y);
	}

	/**
	 * Set start position of player in room
	 * 
	 * @param from
	 *            where player is coming
	 * @param tilex
	 * @param tiley
	 */
	public void setStartPos(String from, int tilex, int tiley) {
		setIntProp("start-x-" + from, tilex);
		setIntProp("start-y-" + from, tiley);
	}

	/**
	 * @param point
	 *            in pixel coordinates
	 * @return tile at point
	 */
	public MyTile getTileFromPoint(Point point) {
		int xTile;
		int yTile;

		xTile = point.x / TILESIZEX;
		yTile = point.y / TILESIZEY;

		return getTile(xTile, yTile);
	}

	/**
	 * Save map to file
	 */
	public void save() {
		OutputStream output;

		props.setProperty("width", Integer.toString(width));
		props.setProperty("height", Integer.toString(height));
		props.setProperty("name", this.name);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				props.setProperty(x + "x" + y, "" + tiles[x][y].getBlock());
			}
		}

		try {
			output = new FileOutputStream(filename);
		} catch (Exception e) {
			return;
		}

		try {
			props.store(output, "Mapfile");
		} catch (Exception e) {
		}

		try {
			output.close();
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * @param filename
	 *            map/room
	 * @return true if successful
	 */
	public Boolean load(String filename) {
		this.filename = filename;
		InputStream input;
		props = new Properties();

		try {
			input = new FileInputStream(filename);
		} catch (Exception e) {
			return false;
		}

		try {
			props.load(input);
		} catch (Exception e) {
			return false;
		}

		try {
			input.close();
		} catch (Exception e) {
		}

		try {
			this.width = new Integer(props.getProperty("width"));
		} catch (Exception e) {
			this.width = 20;
		}

		try {
			this.height = new Integer(props.getProperty("height"));
		} catch (Exception e) {
			this.height = 20;
		}

		try {
			this.name = props.getProperty("name");
		} catch (Exception e) {
			this.name = "";
		}

		tiles = new MyTile[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				try {
					tiles[x][y] = new MyTile(new Integer(props.getProperty(x
							+ "x" + y, "0")), tileSet);
				} catch (Exception e) {
					tiles[x][y] = new MyTile(0, tileSet);
				}
			}
		}

		initActors(); // Create actors... MaybeTODO: load actor parameters
		// from file

		return true;
	}

	private static ClassLoader classLoader = null;
	
	public Boolean loadFromResource(String filename) {
		this.filename = filename;
		InputStream inputStream = null;
		props = new Properties();


		if (classLoader == null) {
			System.out.println("Creating classloader!");
			ImageLoader imageLoader;
			imageLoader = new ImageLoader();
			classLoader = (imageLoader.getClass().getClassLoader());
		}

		inputStream = classLoader.getResourceAsStream(filename);
		if (inputStream != null) {
		} else {
			return false;
		}

		try {
			props.load(inputStream);
		} catch (Exception e) {
			return false;
		}

		try {
			inputStream.close();
		} catch (Exception e) {
		}

		try {
			this.width = new Integer(props.getProperty("width"));
		} catch (Exception e) {
			this.width = 20;
		}

		try {
			this.height = new Integer(props.getProperty("height"));
		} catch (Exception e) {
			this.height = 20;
		}

		try {
			this.name = props.getProperty("name");
		} catch (Exception e) {
			this.name = "";
		}

		tiles = new MyTile[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				try {
					tiles[x][y] = new MyTile(new Integer(props.getProperty(x
							+ "x" + y, "0")), tileSet);
				} catch (Exception e) {
					tiles[x][y] = new MyTile(0, tileSet);
				}
			}
		}

		initActors(); // Create actors... MaybeTODO: load actor parameters
		// from file

		return true;
	}	
	/**
	 * Add sprite to room
	 * 
	 * @param sprite
	 */
	public void addSprite(Sprite sprite) {
		// Use locks to avoid race conditions
		spriteLock.writeLock().lock();
		sprites.add(sprite);
		spriteLock.writeLock().unlock();
	}

	/**
	 * Remove sprite from room
	 * 
	 * @param sprite
	 */
	public void removeSprite(Sprite sprite) {
		// Use locks to avoid race conditions
		spriteLock.writeLock().lock();
		sprites.remove(sprite);
		spriteLock.writeLock().unlock();
	}
}
