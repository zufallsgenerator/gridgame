package gamelib;

import static gamelib.LibConstants.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Frame of game
 * 
 * @author Christer Byström
 * 
 */
public class GameFrame extends JFrame implements SpriteHolder {
	static final long serialVersionUID = 23123252323L;

	private final static String DEFWINTITLE = "[Default Game]";

	private String txtTitle = "[DEFAULT GAME TITLE]";

	private String txtInstructions = "[Default Instructions]";

	private String txtToStart = "[Default How to start]";

	private String txtLoading = "[Default Loading text]";

	private enum STATES {
		LOADING, RUNNING, TITLE, WAITING, FINISHED
	}

	private String txtFinished = "";

	private String txtFinishedSub = "";

	private String[] txtArrayCredits = null;

	private Font bigFont = new Font("Arial", Font.BOLD, 20);

	private Font smallFont = new Font("Arial", Font.BOLD, 15);
	private Font smallestFont = new Font("Arial", 0, 12);

	private Object state = STATES.LOADING;

	private SpriteHolder spriteHolder = null;

	private Timer timer = null;

	private int scrollx = 0;

	private int scrolly = 0;

	private Map map;

	private GameCanvas canvas;

	private LinkedList<Sprite> mysprites; // Sprite canvases

	private ReentrantReadWriteLock spriteLock;// Lock for sprites

	private int tilesizex = 16;

	private int tilesizey = 16;

	/**
	 * @param tileGfx
	 */
	public GameFrame(TileSet tileGfx) {
		super();

		// List for keeping sprite canvases
		this.spriteLock = new ReentrantReadWriteLock();
		this.mysprites = new LinkedList<Sprite>();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(DEFWINTITLE);
		this.setSize(500, 500);

		canvas = new GameCanvas(tileGfx);
		this.getContentPane().add(canvas, SwingConstants.CENTER);
		canvas.setFocusable(true);
		canvas.transferFocus();

		setVisible(true);

		initTimer();

	}

	/**
	 * Show title screen
	 */
	public void titleScreen() {
		state = STATES.TITLE;
	}

	/**
	 * Show game over/finished screen
	 * 
	 * @param text
	 *            to show on game over/finished screen
	 */

	public void gameover(String text, String subtext) {
		txtFinished = text;
		txtFinishedSub = subtext;
		state = STATES.FINISHED;
	}

	/**
	 * Start drawing
	 */
	public void start() {
		if (!timer.isRunning()) {
			timer.start();
		}
		state = STATES.RUNNING;
	}

	/**
	 * Initialize timer
	 */
	public void initTimer() {
		// For redrawing
		timer = new Timer(1000 / TICKS_PER_SECOND, new ActionListener() {
			private long lasttick = 0;

			private long lastrepainttick = 0;

			private long sectick = 0;

			private int fps = 0;

			public void actionPerformed(ActionEvent e) {
				long tick = Calendar.getInstance().getTimeInMillis();
				// If we did last repaint in time, then we can afford another
				// on. Or if it was 100 ms since last repaint, then we really
				// need to repaint.
				// System.out.println("TICKS: " + (tick-lasttick));
				if (tick - lasttick < (2000 / TICKS_PER_SECOND)
						|| tick - lastrepainttick > 100) {
					canvas.repaint();
					fps++;
					lastrepainttick = tick;
				} else {
					// System.out.println("Time diff: " + (tick-lasttick));
				}

				lasttick = tick;
				sectick++;
				if (tick - sectick >= 1000) {
					// System.out.println("FPS: " + fps);
					sectick = tick;
					fps = 0;

				}
			}
		});

		timer.start();
		state = STATES.LOADING;

	}

	/**
	 * Stop drawing
	 */
	public void stop() {
		timer.stop();
	}

	/**
	 * Add sprite to frame
	 * 
	 * @param sprite
	 */
	public void addSprite(Sprite sprite) {
		// Use locks to avoid race conditions
		spriteLock.writeLock().lock();
		mysprites.add(sprite);
		spriteLock.writeLock().unlock();
	}

	/**
	 * Remove sprite from frame
	 * 
	 * @param sprite
	 */
	public void removeSprite(Sprite sprite) {
		// Use locks to avoid race conditions
		spriteLock.writeLock().lock();
		mysprites.remove(sprite);
		spriteLock.writeLock().unlock();
	}

	/**
	 * Set map of frame
	 * 
	 * @param map
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * @param keyAdapter
	 */
	public void setKeyAdapter(KeyAdapter keyAdapter) {
		// Skapa tangentlyssnare för spelplanen.
		canvas.setFocusable(true);
		canvas.transferFocus();
		canvas.addKeyListener(keyAdapter);
	}

	/**
	 * Add mouse listener to canvas where game is painted
	 * 
	 * @param listener
	 */
	public void addCanvasMouseListener(MouseListener listener) {
		canvas.addMouseListener(listener);
	}

	/**
	 * Set coordinates of area we want to view
	 * 
	 * @param x
	 * @param y
	 */
	public void setScroll(int x, int y) {
		scrollx = x;
		scrolly = y;
	}

	/**
	 * Canvas paint game on, as inner class
	 * 
	 * @author Christer Byström
	 */
	class GameCanvas extends JComponent {

		private static final long serialVersionUID = 123912399L;

		private static final double CENTERFACTOR = 0.25;

		// Bitmasking, bit set or not, know what you are doing if you change
		private static final int ANIMFACTOR = 8;

		private int animcount = 0;

		private TileSet tileSet; // Only provides images, and dimensions

		// nothing more

		/**
		 * @param tileSet
		 *            to use
		 */
		GameCanvas(TileSet tileSet) {
			super();
			this.tileSet = tileSet;
			setDoubleBuffered(true);
		}

		/**
		 * Draw things like game title
		 * 
		 * @param g
		 * @param text
		 */
		private void drawTitle(Graphics g, String text) {
			Rectangle rect = this.getBounds();

			g.setFont(bigFont);
			g.drawString(text, (int) ((rect.width / 2) - (bigFont.getSize()
					* text.length() * CENTERFACTOR)), rect.height / 4);

		}

		/**
		 * @param g
		 * @param textArray
		 */
		private void drawCredits(Graphics g, String[] textArray) {
			Rectangle rect = this.getBounds();
			g.setFont(smallestFont);
			FontMetrics fontMetrics = g.getFontMetrics();

			if(textArray==null) {
				return;
			}
			
			for (int i = 0; i < textArray.length; i++) {
				g
						.drawString(textArray[i], 10, (int) ((fontMetrics
								.getHeight() * i) + (rect.height/3)+50));
			}

		}

		/**
		 * Draw blinking text in the bottom
		 * 
		 * @param g
		 * @param text
		 */
		private void drawFlasher(Graphics g, String text) {
			Rectangle rect = this.getBounds();

			g.setFont(smallFont);
			
			if (((animcount++) & ANIMFACTOR) == 0) {
				g.drawString(text, 10, rect.height
						- (int) (smallFont.getSize() * 2));
			}
		}

		/**
		 * Draw subtitle
		 * 
		 * @param g
		 * @param text
		 */

		private void drawSub(Graphics g, String text) {
			Rectangle rect = this.getBounds();
			g.setFont(smallFont);
			g.drawString(text, (int) ((rect.width / 2) - (smallFont.getSize()
					* text.length() * CENTERFACTOR)),
					(int) (bigFont.getSize() * 1.5) + (rect.height / 4));
		}

		/**
		 * Draw title screens and so on, depending on state
		 * 
		 * @param g
		 *            graphics object
		 */
		private void drawText(Graphics g) {
			// If loading, just draw a loading message
			Rectangle rect = this.getBounds();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, rect.width, rect.height);

			g.setColor(Color.white);

			if (state == STATES.LOADING) {
				drawTitle(g, txtTitle);
				drawSub(g, txtLoading);
				drawCredits(g,txtArrayCredits);
			} else if (state == STATES.TITLE) {
				drawTitle(g, txtTitle);
				drawSub(g, txtInstructions);
				drawCredits(g,txtArrayCredits);
				drawFlasher(g, txtToStart);

			} else if (state == STATES.FINISHED) {
				drawTitle(g, txtFinished);
				drawSub(g, txtFinishedSub);
				drawFlasher(g, txtToStart);

			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			LinkedList<Sprite> providedsprites;

			// Use cause sprite holder might
			// get changed by another thread
			SpriteHolder tmpSpriteHolder = spriteHolder;

			if (state != STATES.RUNNING) {
				drawText(g);

				return;
			}

			Comparator spriteComparator = new SpriteComparator();

			if (tmpSpriteHolder != null) {
				spriteLock.writeLock().lock(); // Lock
				providedsprites = tmpSpriteHolder.lockSprites();
			} else {
				providedsprites = null;
			}

			if (providedsprites != null) {
				java.util.Collections.sort(providedsprites, spriteComparator);
			}

			// Fill background
			Rectangle rect = this.getBounds();
			g.setColor(Color.gray);
			g.fillRect(0, 0, rect.width, rect.height);

			// Draw background
			if (map != null) {
				Tile[][] tiles = map.getTiles();
				int width = map.getWidth();
				int height = map.getHeight();

				Iterator sprIter;

				if (providedsprites != null) {
					sprIter = providedsprites.iterator();
				} else {
					sprIter = mysprites.iterator();
				}

				Sprite curSprite = null;
				for (int y = 0; y < height; y++) {

					// First draw tiles that fit to tilesize, then draw
					// the ones that don't, so we can let them expand

					// First pass
					for (int x = 0; x < width; x++) {
						Tile tile = tiles[x][y];

						// Fill shape
						if (tile.getBlock() > 0) {
							ImageIcon image = tileSet.getImage(tile.getBlock());
							if (image != null) {
								int tileoffsetx;
								int tileoffsety;

								tileoffsetx = (tilesizex / 2)
										- (image.getIconWidth() / 2);
								tileoffsety = (tilesizey)
										- image.getIconHeight();

								// First pass, only draw if it fits to an icon
								if (image.getIconWidth() <= tilesizex) {

									// System.out.println("SOLID!");
									image.paintIcon(null, g, (x * tilesizex)
											- scrollx + tileoffsetx,
											(y * tilesizey) - scrolly
													+ tileoffsety);
								}
							}
						}
					}

					// Second pass
					for (int x = 0; x < width; x++) {
						Tile tile = tiles[x][y];

						if (tile.getBlock() > 0) {
							ImageIcon image = tileSet.getImage(tile.getBlock());
							if (image != null) {
								int tileoffsetx;
								int tileoffsety;

								tileoffsetx = (tilesizex / 2)
										- (image.getIconWidth() / 2);
								tileoffsety = (tilesizey)
										- image.getIconHeight();

								if (image.getIconWidth() > tilesizex) {
									image.paintIcon(null, g, (x * tilesizex)
											- scrollx + tileoffsetx,
											(y * tilesizey) - scrolly
													+ tileoffsety);
								}
							}
						}
					}
					// Draw sprites
					if (curSprite == null) {
						if (sprIter.hasNext()) {
							curSprite = (Sprite) sprIter.next();
						}
					}

					while (curSprite != null
							&& curSprite.getY() <= tilesizey * y) {
						if (curSprite.isVisible()) {
							curSprite.draw(g, scrollx, scrolly);
						}
						if (sprIter.hasNext()) {
							curSprite = (Sprite) sprIter.next();
						} else {
							curSprite = null;
						}
					}

				}
			}
			// TODO: Sort sprites on Y coordinate
			// Draw sprites
			// iterate over all sprites and draw them
			// Also, don't draw sprites that are outside of viewport

			/*
			 * Iterator i = sprites.iterator(); while (i.hasNext()) { Sprite s =
			 * (Sprite) i.next(); if (!s.isOverlay() && s.isVisible()) s.draw(g,
			 * scrollx, scrolly); }
			 */

			// TODO: More efficient than just looping over it two times
			// Perhaps separate lists
			Iterator i = mysprites.iterator();
			while (i.hasNext()) {
				Sprite s = (Sprite) i.next();
				if (s.isOverlay() && s.isVisible()) {
					s.draw(g, scrollx, scrolly);
				}
			}

			if (providedsprites != null) {
				i = providedsprites.iterator();
				while (i.hasNext()) {
					Sprite s = (Sprite) i.next();
					if (s.isOverlay() && s.isVisible()) {
						s.draw(g, scrollx, scrolly);
					}
				}
			}

			if (tmpSpriteHolder != null) {
				spriteLock.writeLock().unlock();
				tmpSpriteHolder.releaseSprites();
			}

		}
	}

	/**
	 * @param spriteHolder
	 */
	public void setSpriteHolder(SpriteHolder spriteHolder) {
		this.spriteHolder = spriteHolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.SpriteHolder#getAllSprites()
	 */
	public LinkedList<Sprite> lockSprites() {
		// Nobody will read, skip the lock
		return mysprites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.SpriteHolder#releaseSprites()
	 */
	public void releaseSprites() {
		// Nobody will read, skip the lock
	}

	/**
	 * Set text
	 * 
	 * @param txtToStart
	 */
	public void setTxtToStart(String txtToStart) {
		this.txtToStart = txtToStart;
	}

	/**
	 * @param txtArrayCredits
	 */
	public void setTxtCredits(String[] txtArrayCredits) {
		this.txtArrayCredits = txtArrayCredits;
	}

	/**
	 * Set text
	 * 
	 * @param txtLoading
	 */
	public void setTxtLoading(String txtLoading) {
		this.txtLoading = txtLoading;
	}

	/**
	 * Set text
	 * 
	 * @param txtTitle
	 */
	public void setTxtTitle(String txtTitle) {
		this.txtTitle = txtTitle;
	}

	/**
	 * Set text
	 * 
	 * @param txtInstructions
	 */
	public void setTxtInstructions(String txtInstructions) {
		this.txtInstructions = txtInstructions;
	}

	/**
	 * Set size of tiles
	 * 
	 * @param tilesizex
	 * @param tilesizey
	 */
	public void setTileSize(int tilesizex, int tilesizey) {
		this.tilesizex = tilesizex;
		this.tilesizey = tilesizey;
	}
}
