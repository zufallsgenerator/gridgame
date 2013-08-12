package islandescape;

import gamelib.Map;
import gamelib.GameFrame;
import gamelib.ListReader;
import gamelib.Sprite;
import gamelib.SpriteHolder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Timer;

import static islandescape.GameConstants.*;

/**
 * Instantiates the game and all visible things
 * 
 * @author Christer Byström
 * 
 */
public class World implements ActionListener {
	private static final String YOU_FAILED = "You failed!";

	private static final String GAME_OVER = "Game Over";

	private static final String YOUR_SCORE = "Your score: ";

	private static final String CONGRATULATIONS = "Congratulations! Game finished!";

	private static final String LOADING_GRAPHICS = "Loading graphics, please wait...";

	private static final String TOSTARTGAME = "Press space key to start game!";

	private static final String INSTRUCTIONS = "Collect as many gems as possible, then escape from the island!";

	private static final String CREDITS[] = {
			"Game by Christer Byström (chrby001@student.liu.se),",
			"as a programming assignment in a java course,",
			"fall semester 2006 at Linköpings Universitet", "",
			"Sprites and tiles by Reiner Prokein",
			"(http://reinerstileset.4players.de/)", "",
			"Tiles by Hermann Hillmann",
			"(http://www.vbexplorer.com/VBExplorer/vb_game_downloads.asp)" };

	private static final String ISLAND_ESCAPE_UCASE = "ISLAND ESCAPE v0.5";

	private static final String ISLAND_ESCAPE_LCASE = "Island Escape";

	private static final String START = "start";

	private static final String ROOMS_TXT = "rooms.txt";

	private static final String SCORE = "Score: ";

	private static final String HEALTH = "Health";

	private static final String ENERGY_BAR = "EnergyBar";

	private static final String SCORE_0 = "Score: 0";

	private GameFrame frame;

	private Hashtable rooms;

	private Player player;

	private MyTileSet tileSet;

	private Sprite scoreSprite = null;

	private Boolean reset = false;

	private Boolean willStop = false;

	private Sprite hpSprite = null;

	private Sprite labelSprite = null;

	private int xcenter; // Center of viewport - x

	private int ycenter; // Center of viewport - y

	private int tick = 0;

	private int score = 0;

	private Room currentRoom = null;

	private Room lastRoom = null;

	private Timer timer = null;

	private Boolean waitingForSpaceKey = true;

	/**
	 * Instantiates the game and all visible things
	 */
	public World() {
		tileSet = new MyTileSet(); // MaybeTODO: PARAMETERS of tileset to load
		this.frame = new GameFrame(tileSet);

		// Set tilesizes
		frame.setTileSize(TILESIZEX, TILESIZEY);
		Sprite.setTileSize(TILESIZEX, TILESIZEY);

		// Set text
		frame.setTitle(ISLAND_ESCAPE_LCASE);
		frame.setTxtTitle(ISLAND_ESCAPE_UCASE);
		frame.setTxtInstructions(INSTRUCTIONS);
		frame.setTxtToStart(TOSTARTGAME);
		frame.setTxtLoading(LOADING_GRAPHICS);
		frame.setTxtCredits(CREDITS);

		timer = new Timer(1000 / TICKS_PER_SECOND, this);

		initRooms();

		// Kind of ugly, but I've already overdone it
		scoreSprite = new Sprite(10, 30);
		scoreSprite.setText(SCORE_0);
		scoreSprite.setColor(Color.WHITE);
		frame.addSprite(scoreSprite);
		hpSprite = new Sprite(10, 70);
		hpSprite.setText(ENERGY_BAR);
		hpSprite.setColor(Color.GREEN);
		frame.addSprite(hpSprite);
		labelSprite = new Sprite(10, 60);
		labelSprite.setText(HEALTH);
		frame.addSprite(labelSprite);
		// Create player
		player = new Player(currentRoom, this);
		player.setStartPos(currentRoom.getStartPos(null));
	}

	/**
	 * @param block
	 * @return true if solid
	 */
	public Boolean isBlockSolid(int block) {
		return tileSet.isSolid(block);
	}

	/**
	 * @param block
	 * @return true if door/exit
	 */
	public Boolean isBlockDoor(int block) {
		return tileSet.isDoor(block);
	}

	/**
	 * 
	 */
	public void titleScreen() {
		frame.titleScreen();
	}

	/**
	 * 
	 */
	public void start() {
		willStop = false;
		timer.start();
		frame.start();
	}

	/**
	 * Stop world
	 */
	public void stop() {
		willStop = true;
		// frame.stop();
	}

	/**
	 * Reset world and all rooms
	 */
	private void doReset() {
		reset = false;
		currentRoom.removeActor(player); // Remove player from room, or else
		// player will be deleted
		initRooms();
		player.reset(); // Reset player
		currentRoom.addActor(player); // Add player
		player.setStartPos(currentRoom.getStartPos(null));
	}

	/**
	 * Change room
	 * 
	 * @param door -
	 *            name of door/exit
	 */
	public void changeRoom(String door) {
		Room newRoom;
		Room oldRoom;
		String roomName = currentRoom.nextRoom(door);

		if (roomName == null || roomName == "") {
			if (DEBUG) {
				System.out.println("No such connection " + door);
			}
			return;
		}

		if (DEBUG) {
			System.out.println("Room will be " + roomName);
		}

		newRoom = (Room) rooms.get(roomName);

		if (newRoom == null) {
			if (DEBUG) {
				System.out.println("Room not there!");
			}
			return;
		}

		lastRoom = oldRoom = currentRoom;
		currentRoom = newRoom;
		// Change player position
		oldRoom.removeActor(player);
		player.resetMovement();
		newRoom.addActor(player);

		player.setStartPos(newRoom.getStartPos(oldRoom.getName()));
		frame.setSpriteHolder((SpriteHolder) newRoom);

		frame.setMap((Map) newRoom);
	}

	/**
	 * Set start position of player in room for different situations
	 * 
	 * @param from
	 * @param tilex
	 * @param tiley
	 */
	public void setStartPos(String from, int tilex, int tiley) {
		if (DEBUG) {
			System.out.println("Set start pos: " + currentRoom.nextRoom(from));
		}
		currentRoom.setStartPos(currentRoom.nextRoom(from), tilex, tiley);
	}

	/**
	 * @return player
	 */
	public Actor getPlayer() {
		return (Actor) player;
	}

	/**
	 * @return current tileset
	 */
	public MyTileSet getTileSet() {
		return tileSet;
	}

	/**
	 * @param score
	 *            of player
	 */
	public void setScore(int score) {
		this.score = score;
		scoreSprite.setText(SCORE + score);
	}

	/**
	 * @param hp
	 *            current hp
	 * @param max
	 *            hp
	 */
	public void setHP(int hp, int max) {
		hpSprite.setMeter(hp, max);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// Update all actors
		currentRoom.updateActors();
		// Update last room aswell, but only at half the speed
		if (lastRoom != null) {
			if ((tick++ & 1) == 0) {
				lastRoom.updateActors();
			}
		}

		// Execute pending actions
		if (reset) {
			doReset();
		}

		if (willStop) {
			timer.stop();
			willStop = false;
		}
	}

	/**
	 * Initialize rooms, load them if they are not loaded yet
	 */
	public void initRooms() {
		Room newRoom;

		if (rooms == null) {
			// Create...
			rooms = new Hashtable();
			ArrayList<String> files = new ListReader(ROOMS_TXT).getStrings();

			Iterator i = files.iterator();

			while (i.hasNext()) {
				newRoom = new Room(1, 1, tileSet);
				// If debug, load from file, otherwise load from resource
				if (DEBUG) {
					newRoom.load((String) i.next());
				} else {
					newRoom.loadFromResource((String) i.next());
				}
				rooms.put(newRoom.getName(), newRoom);
			}

		} else {
			// ...or reset
			Enumeration e;

			e = rooms.elements();
			while (e.hasMoreElements()) {
				if (DEBUG) {
					System.out.println("RESET ROOM!");
				}
				// C/C++ -like wild 'n crazy typecasting
				((Room) e.nextElement()).reset();
			}

			if (currentRoom != null) {
				currentRoom.removeActor(player);
			}
		}

		lastRoom = null;
		currentRoom = (Room) rooms.get(START);
		if (currentRoom == null) {
			currentRoom = new Room(20, 20, tileSet); // In worst case, make
			// an empty room
		}

		frame.setMap((Map) currentRoom);
		frame.setSpriteHolder((SpriteHolder) currentRoom);
	}

	/**
	 * Save room/map - forwarder
	 */
	public void saveMap() {
		currentRoom.save();
	}

	/**
	 * @return height in tiles
	 */
	public int getHeight() {
		if (this.currentRoom != null) {
			return currentRoom.getHeight();
		}
		return 0;
	}

	/**
	 * @return width in tiles
	 */
	public int getWidth() {
		if (this.currentRoom != null) {
			return currentRoom.getWidth();
		}
		return 0;
	}

	/**
	 * Set center to always be in viewport. The viewport, or "camera" will
	 * follow slowly after
	 * 
	 * The scroll speed should be set so that the player can't outrun the
	 * camera.
	 * 
	 * 
	 * 
	 * @param x -
	 *            x coordinate of scroll center
	 * @param y -
	 *            y coordinate of scroll center
	 */
	public void setScrollCenter(int x, int y) {
		int xoffset;
		int yoffset;
		Rectangle rect;

		if (frame == null) {
			return;
		}

		rect = frame.getBounds();

		xoffset = rect.width / 2;
		yoffset = rect.height / 2;

		/*
		 * System.out.println("X: " + x + " offset: " + xoffset + " width: " +
		 * map.getWidth() * TILESIZE);
		 */

		// Dont show something outside map
		if (x > (currentRoom.getWidth() * TILESIZEX) - xoffset) {
			xoffset = rect.width + (x - (currentRoom.getWidth() * TILESIZEX));
		}

		if (y > (currentRoom.getHeight() * TILESIZEY) - yoffset) {
			yoffset = rect.height + (y - (currentRoom.getHeight() * TILESIZEY));
		}

		if (x < xoffset) {
			xoffset = x;
		}

		if (y < yoffset) {
			yoffset = y;
		}

		// Set position
		if (x > xcenter + xoffset) {
			xcenter = x - xoffset;
		}
		if (x < xcenter + xoffset) {
			xcenter = x - xoffset;
		}
		if (y > ycenter + yoffset) {
			ycenter = y - yoffset;
		}
		if (y < ycenter + yoffset) {
			ycenter = y - yoffset;
		}

		frame.setScroll(xcenter, ycenter);
	}

	/**
	 * @return frame that is drawn upon
	 */
	public GameFrame getFrame() {
		return this.frame;
	}

	/**
	 * @param xTile
	 *            tile coordinate
	 * @param yTile
	 *            tile coordinate
	 * @return tile at position
	 */
	public MyTile getTileFromPos(int xTile, int yTile) {
		if (this.currentRoom != null) {
			MyTile tile = currentRoom.getTile(xTile, yTile);

			return tile;
		}

		return null;
	}

	/**
	 * @param keyAdapter
	 */
	public void setKeyAdapter(KeyAdapter keyAdapter) {
		frame.setKeyAdapter(keyAdapter);
	}

	/**
	 * @param listener
	 */
	public void addMouseListener(MouseListener listener) {
		frame.addCanvasMouseListener(listener);
	}

	/**
	 * Add actor to current room
	 * 
	 * @param actor
	 */
	public void addActor(Actor actor) {
		currentRoom.addActor(actor);
	}

	/**
	 * Add actor to current room
	 * 
	 * @param actor
	 */
	public void removeActor(Actor actor) {
		currentRoom.removeActor(actor);
	}

	/**
	 * @param point -
	 *            coordinates to get tile from
	 * @return - MapTile or null if point not part of any tile
	 */
	public MyTile getTileFromPoint(Point point) {
		if (this.currentRoom != null) {
			Point p = (Point) point.clone();

			p.x = p.x + xcenter;
			p.y = p.y + ycenter;

			MyTile tile = currentRoom.getTileFromPoint(p);

			return tile;
		}

		return (MyTile) null;
	}

	/**
	 * Reset game
	 */
	private void reset() {
		waitingForSpaceKey = true;
		stop();
		reset = true;
	}

	public void resetToTitle() {
		reset();
		titleScreen();
	}

	/**
	 * Called when game is over
	 * 
	 * @param finished
	 *            true if we reached the goal
	 */
	public void gameover(Boolean finished) {
		reset();
		if (finished) {
			frame.gameover(CONGRATULATIONS, YOUR_SCORE + score);
		} else {
			frame.gameover(GAME_OVER, YOU_FAILED);
		}
	}

	/**
	 * @return true if waiting for key
	 */
	public Boolean waitingForKey() {
		return waitingForSpaceKey;
	}

	/**
	 * Called when waiting for space and space is pressed
	 */
	public void onSpaceKey() {
		waitingForSpaceKey = false;
		start();
	}
}
