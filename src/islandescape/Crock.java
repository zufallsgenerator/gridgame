package islandescape;

/**
 * 
 * @author Christer Byström
 * 
 */
public class Crock extends Actor {

	private static final String CROCK4 = "walking e%04d.bmp.gif";

	private static final String CROCK3 = "walking w%04d.bmp.gif";

	private static final String CROCK2 = "walking s%04d.bmp.gif";

	private static final String CROCK1 = "walking n%04d.bmp.gif";

	private static final String GFX_ENEMY1 = "gfx/enemy1/";

	/**
	 * @param world
	 */
	public Crock(Room room) {
		super(room);
		loadMove(GFX_ENEMY1, CROCK1, CROCK2, CROCK3, CROCK4);

	}

	public void doIntelligence() {
		if (movementDone()) {

			double choice = Math.random() * 12;

			switch ((int) choice) {
			case 0:
				moveUp();
				break;
			case 1:
				moveDown();
				break;
			case 2:
				moveLeft();
				break;
			case 3:
				moveRight();
				break;
			default: // Prefer to keep moving
				// Will also give an effect of enemy deliberating when
				// hitting a wall (might try same move several times)
				repeatLastMove();
				break;
			}
		}
	}

	public Boolean isEnemy() {
		return true;
	}
}
