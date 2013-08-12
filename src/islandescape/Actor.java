package islandescape;

import gamelib.AnimFrames;
import gamelib.Sprite;

import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;

import static islandescape.GameConstants.*;

/*
 * Jag tänkte implementera det som abstrakt klass, men eftersom jag skulle antingen
 * behöva overloada tusen metoder som inte används eller bygga X antal interface
 * så blev det här enklaste lösningen. Polymorfism utan typecasting
 */

/**
 * Base class of all actors, that is figure or objects Does not draw anything,
 * but HAS a sprite
 * 
 * @author Christer Byström
 * 
 */
public class Actor {
	private Sprite sprite;

	private AnimFrames frames;

	private Boolean onDoorWait = false;

	private Boolean onEndOfGameWait = false;

	private MyTile currentTile = null;

	private Room room;

	private int x = 0, y = 0;

	// Counter for when to switch frame
	private int animStep = 0;

	// Number of steps between frame switches
	private int stepSize = 2;

	private int xTile;

	private int yTile;

	private int xTileNew;

	private int yTileNew;

	private int transition = 0;

	private int transitionMax = TILESIZEX;

	// Point of transition where we start buffering future movements
	private int transitionPendLimit = TILESIZEX / 2;

	// Speed when moving normally
	private int standardTransitionSpeed = TILESIZEX / 10;

	// Speed when pushed
	private int pushTransitionSpeed = TILESIZEX / 5;

	private int transitionSpeed = standardTransitionSpeed;

	private Boolean transitionGoing = false;

	private Boolean transMoved = false;

	private int pendingMove = NO_MOVE;

	private int currentMove = NO_MOVE;

	private int lastMove = NO_MOVE;

	/**
	 * @param room
	 *            that player belongs to
	 */
	public Actor(Room room) {
		this.room = room;
		this.sprite = new Sprite(); // Must create sprite before creating room
		room.addActor(this);
		onEnterTile();
		frames = new AnimFrames();
		// update();
	}

	/**
	 * Called upon entrance to a new room
	 */
	public void resetMovement() {
		transition = 0;
		transitionGoing = false;
		currentMove = pendingMove = NO_MOVE;
	}

	/**
	 * @return
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * @param room
	 */
	public void setRoom(Room room) {
		this.room = room;
	}

	/**
	 * @return coordinate in pixels
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @return coordinate in pixels
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @return - x coordinate of current tile position
	 */
	public int getTileX() {
		return this.xTile;
	}

	/**
	 * @return - y coordinate of current tile position
	 */
	public int getTileY() {
		return this.yTile;
	}

	private void setPosition(int x, int y) {
		sprite.setPosition(x, y);
		this.x = x;
		this.y = y;
	}

	/**
	 * @param path
	 * @param up
	 * @param down
	 * @param left
	 * @param right
	 * @return
	 */
	public Boolean loadMove(String path, String up, String down, String left,
			String right) {
		// Loads are cached in frames
		return frames.loadMove(path, up, down, left, right);
	}

	/**
	 * Load still frame of actors standing still
	 * 
	 * @param path
	 * @param file
	 * @return true if successful
	 */
	public Boolean loadStill(String path, String file) {
		Boolean retval = frames.loadStill(path, file);

		if (retval) {
			sprite.setIcon(frames.getStill());
		}

		return retval;
	}

	/**
	 * Check if a movement is in progress
	 * 
	 * @return - true if no movement is in progress
	 */
	public Boolean movementDone() {
		return !transitionGoing;
	}

	/**
	 * @param isVisible
	 *            true if visible
	 */
	public void setVisible(Boolean isVisible) {
		sprite.setVisible(isVisible);
	}

	/**
	 * @return true if successful
	 */
	public Boolean setRandomTilePos() {
		int loop = 0;

		// Loop counter just to avoid eternal looping
		while (loop++ < 100) {
			xTile = (int) (Math.random() * (room.getWidth() - 2)) + 1;
			yTile = (int) (Math.random() * (room.getHeight() - 2)) + 1;

			/*
			 * System.out.println("Pos: " + this + " " + xTile + " " + yTile +
			 * "?" + world.getWidth());
			 */

			if (posOK(xTile, yTile) && !room.getTile(xTile, yTile).isDoor()) {
				setTilePos(xTile, yTile);
				// Pop tile
				if (currentTile != null) {
					currentTile.removeActor(this);
				}

				// Set new tile to be "home"
				currentTile = room.getTile(xTile, yTile);
				currentTile.addActor(this);
				onEnterTile();

				transitionGoing = false;

				return true;
			}
		}

		return false;
	}

	/**
	 * @return what exit we are at
	 */
	public String whatExitIsThis() {
		if (xTileNew <= 1) {
			return TXT_WEST;
		}
		if (xTileNew >= room.getWidth() - 2) {
			return TXT_EAST;
		}
		if (yTileNew <= 1) {
			return TXT_NORTH;
		}
		if (yTileNew >= room.getHeight() - 2) {
			return TXT_SOUTH;
		}

		return TXT_MIDDLE;
	}

	/**
	 * Position player
	 * 
	 * @param pos
	 *            in tile coordinates
	 */
	public void setTilePos(Point pos) {
		setTilePos(pos.x, pos.y);
	}

	/**
	 * Position player
	 * 
	 * @param xTile
	 *            pos in tile coordinates
	 * @param yTile
	 *            pos in tile coordinates
	 */
	private void setTilePos(int xTile, int yTile) {
		if (posOK(xTile, yTile)) {
			this.xTileNew = this.xTile = xTile;
			this.yTileNew = this.yTile = yTile;

			this.x = xTile * TILESIZEX;
			this.y = yTile * TILESIZEY;
		}
	}

	/**
	 * @param pos -
	 *            forwards to setStartPos(int, int)
	 */
	public void setStartPos(Point pos) {
		setStartPos(pos.x, pos.y);
	}

	/**
	 * @param xTile
	 * @param yTile
	 */
	public void setStartPos(int xTile, int yTile) {
		// If never placed, the place now, otherwise wait

		this.xTile = this.xTileNew = xTile;
		this.yTile = this.yTileNew = yTile;

		if (currentTile == null) {
		} else {
			currentTile.removeActor(this);
		}

		currentTile = room.getTile(xTile, yTile);
		currentTile.addActor(this);

		transitionGoing = false;
	}

	// Really have this one?
	public void setIcon(ImageIcon icon) {
		sprite.setIcon(icon);
	}

	/**
	 * Update function for actors, this one should be overloaded
	 */
	public void doIntelligence() {
		// Void
	}

	/**
	 * Update function, called by owner (probably a Room) every tick Forwards
	 * update to children
	 */
	public void update() {
		doIntelligence(); // Children overloads this one
		doTransition();
		// Queued event
		if (onDoorWait == true) {
			onDoorWait = false;
			onDoor(whatExitIsThis());
		}

		if (onEndOfGameWait == true) {
			onEndOfGameWait = false;
			onEndOfGame();
		}
	}

	/**
	 * Called when a new transition from one tile to another is initiated
	 */
	public void initTransition() {
		transMoved = false;
		lastMove = currentMove;
	}

	/**
	 * Do transition between two tiles
	 */
	private void doTransition() {
		if (xTile != xTileNew) {
			if (xTile < xTileNew) {
				x += transitionSpeed;
				transition += transitionSpeed;
				if (x > xTileNew * TILESIZEX) {
					x = xTileNew * TILESIZEX;
					xTile = xTileNew;
					setTilePos(xTile, yTile);
					transitionGoing = false;

				}
			} else {
				x -= transitionSpeed;
				transition += transitionSpeed;
				if (x < xTileNew * TILESIZEX) {
					x = xTileNew * TILESIZEX;
					xTile = xTileNew;
					setTilePos(xTile, yTile);
					transitionGoing = false;
				}
			}
		}
		if (yTile != yTileNew) {
			if (yTile < yTileNew) {
				y += transitionSpeed;
				transition += transitionSpeed;
				if (y > yTileNew * TILESIZEY) {
					y = yTileNew * TILESIZEY;
					yTile = yTileNew;
					setTilePos(xTile, yTile);
					transitionGoing = false;
				}
			} else {
				y -= transitionSpeed;
				transition += transitionSpeed;
				if (y < yTileNew * TILESIZEY) {
					y = yTileNew * TILESIZEY;
					yTile = yTileNew;
					setTilePos(xTile, yTile);
					transitionGoing = false;
				}
			}
		}

		// Change CURRENT TILE when we're half over to it
		if (transitionGoing && transMoved == false) {
			if (transition < transitionMax / 2) {

				// Remove from old tile
				if (currentTile != null) {
					currentTile.removeActor(this);
				}

				// Put back to new tile
				currentTile = room.getTile(xTileNew, yTileNew);
				if (currentTile != null) {
					currentTile.addActor(this);
					onEnterTile();
				}

				transMoved = true;
			}

		}

		if (xTile == xTileNew && yTile == yTileNew) {
			x = xTile * TILESIZEX;
			y = yTile * TILESIZEY;

			int tmpMove = pendingMove;
			pendingMove = NO_MOVE;
			currentMove = NO_MOVE;

			switch (tmpMove) {
			case MOVE_UP:
				moveUp();
				break;
			case MOVE_DOWN:
				moveDown();
				break;
			case MOVE_LEFT:
				moveLeft();
				break;
			case MOVE_RIGHT:
				moveRight();
				break;
			}

		}
		setPosition(x, y);

		// Set sprite
		// MaybeTODO: speed
		if (transitionGoing && frames.isWalkLoaded()) {
			if (animStep-- <= 0) {
				switch (currentMove) {
				case MOVE_UP:
					setIcon(frames.nextUp());
					break;
				case MOVE_DOWN:
					setIcon(frames.nextDown());
					break;
				case MOVE_LEFT:
					setIcon(frames.nextLeft());
					break;
				case MOVE_RIGHT:
					setIcon(frames.nextRight());
					break;
				}

				animStep = stepSize;
			}
		}
	}

	/**
	 * @return current movement in progress
	 */
	public int getMove() {
		return currentMove;
	}

	/**
	 * Repeat the previous/last transition
	 */
	public void repeatLastMove() {
		switch (lastMove) {
		case MOVE_UP:
			moveUp();
			break;
		case MOVE_DOWN:
			moveDown();
			break;
		case MOVE_LEFT:
			moveLeft();
			break;
		case MOVE_RIGHT:
			moveRight();
			break;
		}
	}

	/**
	 * @return - last movement
	 */
	public int getLastMove() {
		return lastMove;
	}

	/**
	 * @param xTile
	 * @param yTile
	 * @return - true if pos is OK to occupy
	 */
	public Boolean posOK(int xTile, int yTile) {
		MyTile tile = room.getTile(xTile, yTile);

		if (tile == null) {
			return false;
		}

		if (tile.isSolid()) {
			return false;
		}

		return true;
	}

	/**
	 * When pushed
	 */
	public void forceUp() {
		if (posOK(xTile, yTile - 1)) {
			yTileNew = yTile - 1;
			transition = 0;
			transitionGoing = true;
			currentMove = MOVE_UP;
			transitionSpeed = pushTransitionSpeed;
			initTransition();
		}
	}

	/**
	 * 
	 */
	public void moveUp() {
		if (transitionGoing == true) {
			if (transition > transitionPendLimit) {
				pendingMove = MOVE_UP;
			}
		} else {
			if (posOK(xTile, yTile - 1)) {
				yTileNew = yTile - 1;
				transition = 0;
				transitionGoing = true;
				transitionSpeed = standardTransitionSpeed;
				currentMove = MOVE_UP;
				initTransition();
			}
		}
	}

	/**
	 * When pushed
	 */
	public void forceDown() {
		if (posOK(xTile, yTile + 1)) {
			yTileNew = yTile + 1;
			transition = 0;
			transitionGoing = true;
			currentMove = MOVE_DOWN;
			transitionSpeed = pushTransitionSpeed;
			initTransition();
		}
	}

	/**
	 * 
	 */
	public void moveDown() {
		if (transitionGoing == true) {
			if (transition > transitionPendLimit) {
				pendingMove = MOVE_DOWN;
			}
		} else {
			if (posOK(xTile, yTile + 1)) {
				yTileNew = yTile + 1;
				transition = 0;
				transitionGoing = true;
				transitionSpeed = standardTransitionSpeed;
				currentMove = MOVE_DOWN;
				initTransition();
			}
		}
	}

	/**
	 * When pushed
	 */
	public void forceLeft() {
		if (posOK(xTile - 1, yTile)) {
			xTileNew = xTile - 1;
			transition = 0;
			transitionGoing = true;
			currentMove = MOVE_LEFT;
			transitionSpeed = pushTransitionSpeed;
			initTransition();
		}
	}

	/**
	 * 
	 */
	public void moveLeft() {
		if (transitionGoing == true) {
			if (transition > transitionPendLimit) {
				pendingMove = MOVE_LEFT;
			}
		} else {
			if (posOK(xTile - 1, yTile)) {
				xTileNew = xTile - 1;
				transition = 0;
				transitionGoing = true;
				transitionSpeed = standardTransitionSpeed;
				currentMove = MOVE_LEFT;
				initTransition();
			}
		}
	}

	/**
	 * When pushed
	 */
	public void forceRight() {
		if (posOK(xTile + 1, yTile)) {
			xTileNew = xTile + 1;
			transition = 0;
			transitionGoing = true;
			currentMove = MOVE_RIGHT;
			transitionSpeed = pushTransitionSpeed;
			initTransition();
		}
	}

	/**
	 * 
	 */
	public void moveRight() {
		if (transitionGoing == true) {
			if (transition > transitionPendLimit) {
				pendingMove = MOVE_RIGHT;
			}
		} else {
			if (posOK(xTile + 1, yTile)) {
				xTileNew = xTile + 1;
				transition = 0;
				transitionGoing = true;
				transitionSpeed = standardTransitionSpeed;
				currentMove = MOVE_RIGHT;
				initTransition();
			}
		}
	}

	/**
	 * Set color of sprite - used by Marker
	 * 
	 * @param color
	 *            of sprite
	 */
	public void setColor(Color color) {
		sprite.setColor(color);
	}

	/**
	 * Called by children of actor if they want to check collisions
	 */
	public void checkCollisions() {
		if (currentTile != null) {
			currentTile.checkCollisions(this);
		}
	}

	/**
	 * Overload this one, this is the callback function for handling collisions
	 * However, collision checking is invoked by calling the method
	 * checkCollisions()
	 * 
	 * @param actor
	 * @return
	 */
	public Boolean collisionHandler(Actor actor) {
		return true;
	}

	/**
	 * Remove all references to self, then sit and wait for gc
	 */
	public void remove() {
		if (currentTile != null) {
			currentTile.removeActor(this);
		}
		room.removeActor(this);
		sprite = null;
	}

	/**
	 * Used for player, but CALLED by player
	 */
	private void onEnterTile() {
		// Check if we're on a door
		if (currentTile != null) {
			if (currentTile.isDoor()) {
				onDoorWait = true;
			}
			if (currentTile.isEndOfGame()) {
				System.out.println("ONENDOFGAME!");
				onEndOfGameWait = true;
			}

		}
	}

	/**
	 * Reset actor Overload if you wish
	 */
	public void reset() {
		setRandomTilePos();
	}

	/**
	 * Implemented by player, handler for entering doors
	 * 
	 * @param door
	 *            that player entered
	 */
	public void onDoor(String door) {

	}

	/**
	 * Implemented by player, handler for entering end of game tile
	 */
	public void onEndOfGame() {

	}

	/**
	 * @return - animation frames
	 */
	public AnimFrames getFrames() {
		return frames;
	}

	/**
	 * For healer, overload
	 * 
	 * @return true if ready
	 */
	public Boolean isReady() {
		return true;
	}

	/**
	 * For healer, overload
	 */
	public void regenerate() {
	}

	/**
	 * Overload this one if enemy
	 * 
	 * @return true if enemy
	 */
	public Boolean isEnemy() {
		return false;
	}

	/**
	 * Overload this one if you want other damage
	 * 
	 * @return damage to do to player
	 */
	public int damage() {
		return 1;
	}

	/**
	 * Overload this one if collectable
	 * 
	 * @return true if collectable
	 */
	public Boolean isCollectable() {
		return false;
	}

	/**
	 * Overload this one if healer
	 * 
	 * @return true if actor is healer
	 */
	public Boolean isHealer() {
		return false;
	}

	/**
	 * Overload this one if player
	 * 
	 * @return true if actor is player
	 */
	public Boolean isPlayer() {
		return false;
	}

	/**
	 * @return
	 */
	public Sprite getSprite() {
		return this.sprite;
	}

	// DEBUG
	public void whoThere() {
		/*
		 * if (currentTile != null) { currentTile.whoThere(); }
		 */
	}

	/**
	 * @param key
	 */
	public void keyPress(int key) {
	}

	/**
	 * @param key
	 */
	public void keyRelease(int key) {
	}

}
