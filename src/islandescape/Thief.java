/**
 * 
 */
package islandescape;

import static islandescape.GameConstants.*;

import java.awt.Point;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Implements an actor that visits all available squares in the room, using a
 * deep-search
 * 
 * It picks up all objects that can be picked up, and "steals" them for the
 * player
 * 
 * @author Christer Byström
 * 
 */
public class Thief extends Actor {
	private static final String WALKING4 = "walking e%04d.bmp.gif";

	private static final String WALKING3 = "walking w%04d.bmp.gif";

	private static final String WALKING2 = "walking s%04d.bmp.gif";

	private static final String WALKING1 = "walking n%04d.bmp.gif";

	private static final String GFX_THIEF = "gfx/thief/";

	private Hashtable visited;

	private Boolean initDone = false;

	private LinkedList<Point> path;

	/**
	 * @param room
	 *            that thief dwells in
	 */
	public Thief(Room room) {
		super(room);
		init();
	}

	/**
	 * Called by constructor
	 */
	public void init() {
		loadMove(GFX_THIEF, WALKING1, WALKING2, WALKING3, WALKING4);

		// Create hashtable and path
		visited = new Hashtable();
		path = new LinkedList<Point>();
		initDone = true;
		setRandomTilePos();
	}

	/**
	 * Add a tile position to path and hash to be able to backtrace if we get
	 * stuck, and avoid entering the same tile again if not necessary, that is,
	 * avoid walking in small circles
	 * 
	 * @param x
	 *            tile position
	 * @param y
	 *            tile postion
	 */
	private void addToPath(int x, int y) {
		Point p = new Point(x, y);

		// Add to hash
		visited.put(p, p);

		// Add to path
		if (path.size() > 0) {
			Point last = path.getLast();
			if (last.x == p.x && last.y == p.y) {
				// System.out.println("Double!");
			} else {
				if (dirBetween(last, p) == NO_MOVE) {
					// System.out.println("----------\r\nADDING: NO WAY BETWEEN"
					// + p + "-" + last);

				}
				path.addLast(p);
			}
		} else {
			path.addLast(p);
		}

		// Create tracer - debug
		/*
		 * Marker marker = new Marker(getWorld()); marker.setTilePos(x, y);
		 */
	}

	/**
	 * @param x -
	 *            coord of pos to check
	 * @param y -
	 *            coord of pos to check
	 * @return - true if position was visited before
	 */
	private Boolean hasVisited(int x, int y) {
		Point p;
		p = (Point) visited.get(new Point(x, y));

		if (p == null) {
			return false;
		}

		return true;
	}

	/**
	 * @param from -
	 *            position to go from
	 * @param to -
	 *            position to go to
	 * @return - direction to move to get from "from" to "to"
	 */
	private int dirBetween(Point from, Point to) {
		if (from.x > to.x) {
			return MOVE_LEFT;
		}
		if (from.x < to.x) {
			return MOVE_RIGHT;
		}
		if (from.y > to.y) {
			return MOVE_UP;
		}
		if (from.y < to.y) {
			return MOVE_DOWN;
		}

		if (DEBUG) {
			System.out
					.println("Thief.dirBetween(): Movement between these to points could'n be resolved\r\n"
							+ from.x + "." + from.y + " - " + to.x + "." + to.y);
		}

		return NO_MOVE;
	}

	/**
	 * Try to move to position
	 * 
	 * @param x
	 * @param y
	 * @return - true on success
	 */
	private Boolean tryMove(int x, int y) {
		if (posOK(x, y) && !hasVisited(x, y)) {
			// Add to hash, and path
			addToPath(x, y);
			return true;

		}
		return false;
	}

	/*
	 * Arrays for trying the moves the right order - forward, right, left, back
	 */
	private final static int orderN[][] = { { 0, -1, MOVE_UP }, { 1, 0, MOVE_RIGHT },
			{ -1, 0, MOVE_LEFT }, { 0, 1, MOVE_DOWN } };

	private final static int orderE[][] = { { 1, 0, MOVE_RIGHT }, { 0, 1, MOVE_DOWN },
			{ 0, -1, MOVE_UP }, { -1, 0, MOVE_LEFT } };

	private final static int orderS[][] = { { 0, 1, MOVE_DOWN }, { -1, 0, MOVE_LEFT },
			{ 1, 0, MOVE_RIGHT }, { 0, -1, MOVE_UP }, };

	private final static int orderW[][] = { { -1, 0, MOVE_LEFT }, { 0, -1, MOVE_UP },
			{ 0, 1, MOVE_DOWN }, { 1, 0, MOVE_RIGHT } };

	/**
	 * Choose next position in deep-search fashion
	 * 
	 * @param x -
	 *            current x pos
	 * @param y -
	 *            current y pos
	 * @return
	 */
	private int chooseNextMove(int x, int y) {
		int order[][];

		switch (getLastMove()) {
		case MOVE_DOWN:
			order = orderS;
			break;
		case MOVE_LEFT:
			order = orderW;
			break;
		case MOVE_RIGHT:
			order = orderE;
			break;
		default:
			// case MOVE_UP:
			order = orderN;
			break;
		}

		// Point lastPoint;
		// if (path.size() > 0) {
		// lastPoint = path.getLast();
		// } else {
		// lastPoint = new Point(1000, 1000);
		// }
		// System.out.println("---- path lenght " + path.size() + " ---" + x +
		// "."
		// + y + " --- Last: " + lastPoint.x + "." + lastPoint.y);

		// Try all moves
		for (int i = 0; i < 4; i++) {
			// System.out.print("Trying x=" + order[i][0] + ", y=" +
			// order[i][1]);
			if (tryMove(order[i][0] + x, order[i][1] + y)) {
				// System.out.println("WORKED!");
				return order[i][2];
			} else {
				// System.out.println("FAILED!");
			}
		}
		// Go through all neighbouring tiles
		// Procedure: first try to move forward, then right, then left, back

		// Now we take one step back
		//
		if (path.size() == 0) {
			return NO_MOVE; // FAIL!
		}
		if (path.size() == 0) {
			return NO_MOVE;
		}

		// Remove last
		Point back = path.removeLast();

		if (path.size() == 0) {
			return NO_MOVE;
		}

		// Get, don't remove
		back = path.getLast();

		// Marker marker = new Marker(getWorld()); marker.setTilePos(x, y);
		// marker.setColor(Color.RED);

		return dirBetween(new Point(x, y), back);
	}

	/**
	 * Clear walked path and visited positions
	 */
	private void resetPath() {
		path.clear();
		visited.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#reset()
	 */
	public void reset() {
		resetPath();
		super.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#doIntelligence()
	 */
	public void doIntelligence() {
		if (initDone != true) {
			return;
		}
		// Ready to move?
		if (movementDone()) {
			// Yes, lets do it!
			int move = chooseNextMove(getTileX(), getTileY());

			switch (move) {
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
			case NO_MOVE: // Done? Well start all over again
				resetPath();
				break;
			}
		}

		checkCollisions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#isEnemy()
	 */
	public Boolean isEnemy() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tilegame.Actor#damage()
	 */
	public int damage() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Actor#collisionHandler(gamelib.Actor)
	 */
	public Boolean collisionHandler(Actor actor) {
		if (actor.isCollectable()) {
			actor.remove(); // STEAL!!!
		}

		return true;
	}
}
