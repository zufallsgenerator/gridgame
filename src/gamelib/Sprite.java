package gamelib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;


/**
 * Implements a sprite
 * 
 * @author Christer Byström
 * 
 */

public class Sprite {
	private static final String FONTNAME = "Arial";

	private Point position;

	private ImageIcon icon;
	
	private static int tilesizex=16;
	private static int tilesizey=16;

	private final static int METERSIZE = 10;

	private Boolean isMeter = false;

	private int max = 0;

	private int current = 0;

	private Boolean isVisible = true;

	private Font font;

	private String text;

	private Color color;

	private Boolean isOverlay = false;

	/**
	 * 
	 */
	public Sprite() {
		constructor(50, 50);
	}

	/**
	 * Create sprite
	 * 
	 * @param x
	 *            dimension
	 * @param y
	 *            dimension
	 */
	public Sprite(int x, int y) {
		constructor(x, y);
	}

	/**
	 * Set size of tiles
	 * 
	 * @param tilesizex
	 * @param tilesizey
	 */
	public static void setTileSize(int tilesizex, int tilesizey) {
		Sprite.tilesizex = tilesizex;
		Sprite.tilesizey = tilesizey;
	}
	
	/**
	 * @param current
	 * @param max
	 */
	public void setMeter(int current, int max) {
		if (max == 0) {
			this.isMeter = false;
		} else {
			this.isMeter = true;
		}

		if (max < 1) {
			max = 1;
		}

		if (current < 0) {
			current = 0;
		}

		this.max = max;
		this.current = current;
	}

	/**
	 * @param isVisible
	 *            true if visible
	 */
	public void setVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @return true if visible
	 */
	public Boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param text
	 *            to show
	 */
	public void setText(String text) {
		if (text != null) {
			if (this.font == null) {
				this.font = new Font(FONTNAME, Font.BOLD, 20);
			}
			isOverlay = true;
			this.text = text;
		} else {
			isOverlay = false;
			this.text = null;
		}
	}

	/**
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @param x
	 * @param y
	 */
	private void constructor(int x, int y) {
		position = new Point();

		position.x = x;
		position.y = y;
		color = Color.white;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	/**
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	/**
	 * @param g
	 * @param scrollx
	 * @param scrolly
	 */
	public void draw(Graphics g, int scrollx, int scrolly) {
		// Is meter
		if (this.isMeter) {
			int shade = (int) (((float) current / (float) max) * 255);
			g.setColor(Color.GRAY);
			g.fillRect(position.x, position.y, 1 + max * METERSIZE,
					1 + METERSIZE);
			g.setColor(new Color(255 - shade, shade, 0));
			g.fillRect(position.x, position.y, current * METERSIZE, METERSIZE);
			g.draw3DRect(position.x, position.y, current * METERSIZE,
					METERSIZE, true);
			g.setColor(Color.BLACK);
			g.drawRect(position.x - 1, position.y - 1, 2 + max * METERSIZE,
					2 + METERSIZE);
		} else
		// Is graphical sprite
		if (this.icon != null) {
			int xoffset = 0;
			int yoffset = 0;
			int xsize, ysize;
			// If image is bigger or smaller than tilesize,
			// try to "center" it on the tile
			xsize = icon.getIconWidth();
			ysize = icon.getIconWidth();

			xoffset = (xsize - tilesizex) / 2;
			yoffset = (ysize - tilesizey) / 2;

			// Also, try to put it a bit up, to get perspective effects
			yoffset += tilesizey / 2;

			// No offset if we're overlay
			if (isOverlay()) {
				scrolly = scrollx = 0;
			}

			icon.paintIcon(null, g, position.x - xoffset - scrollx, position.y
					- yoffset - scrolly);
		} else if (this.text != null) {
			if (this.font != null) {
				g.setFont(this.font);
			}
			// Draw on absolut position, don't care about scrollx and scrolly
			g.setColor(Color.BLACK);
			g.drawString(this.text, position.x + 1, position.y + 1);
			g.setColor(color);
			g.drawString(this.text, position.x, position.y);
		} else {
			// Do draw, just a ball for now
			g.setColor(color);
			g.fillOval(position.x - scrollx + (tilesizex / 2), position.y
					- scrolly + (tilesizey / 2), 10, 10);
		}
	}

	/**
	 * Set sprite to be overlayed, having static coordinates (not following
	 * scroll)
	 * 
	 * @param overlay
	 *            true if overlayed
	 */
	public void setOverlay(Boolean overlay) {
		this.isOverlay = overlay;
	}

	/**
	 * @return true if overlayed
	 */
	public Boolean isOverlay() {
		return isOverlay;
	}

	/**
	 * @return pixel position
	 */
	public int getX() {
		return position.x;
	}

	/**
	 * @return pixel position
	 */
	public int getY() {
		return position.y;
	}

}
