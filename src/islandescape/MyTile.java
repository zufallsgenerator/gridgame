package islandescape;

import gamelib.Tile;

import static islandescape.GameConstants.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * My tile class
 * 
 * @author Christer Byström
 * 
 */
public class MyTile implements Tile {
	private int block = 0;

	private MyTileSet tileSet;

	private Boolean done = false;

	private LinkedList<Actor> actors;

	/**
	 * @param tileSet
	 */
	private void init(MyTileSet tileSet) {
		this.tileSet = tileSet;
		this.actors = new LinkedList<Actor>();
	}

	/**
	 * @param tileSet
	 */
	public MyTile(MyTileSet tileSet) {
		init(tileSet);
	}

	/**
	 * @param block
	 * @param tileSet
	 */
	public MyTile(int block, MyTileSet tileSet) {
		init(tileSet);
		this.block = block;
	}

	/**
	 * @return
	 */
	public Boolean isSolid() {
		return tileSet.isSolid(block);
	}

	/**
	 * @return
	 */
	public Boolean isDoor() {
		return tileSet.isDoor(block);
	}

	/**
	 * @return
	 */
	public Boolean isEndOfGame() {
		return tileSet.isEndOfGame(block);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Tile#getBlock()
	 */
	public int getBlock() {
		return block;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gamelib.Tile#setBlock(int)
	 */
	public void setBlock(int block) {
		this.block = block;
	}

	/**
	 * @param actor
	 */
	public void addActor(Actor actor) {
		actors.add(actor);
	}

	/**
	 * @param mySelf
	 */
	public void checkCollisions(Actor mySelf) {
		Actor actor;
		Iterator iterator = actors.iterator();

		// Check if there are any other actors on the list
		while (iterator.hasNext()) {
			actor = (Actor) iterator.next();
			// Not me?
			if (actor != mySelf) {
				// Ok, handle collision
				if (mySelf.collisionHandler(actor) == false) {
					// False means don't give me any more collisions
					return;
				}
			}
		}

	}

	/**
	 * Print actors on tile
	 */
	public void whoThere() {
		if (DEBUG) {
			Actor actor;
			Iterator iterator = actors.iterator();

			System.out.print("(Block: " + getBlock() + ") Here is: ");
			// Check if there are any other actors on the list
			while (iterator.hasNext()) {
				actor = (Actor) iterator.next();
				// Not me?
				System.out.print(actor + ", ");
			}
			System.out.println(" ");
		}
	}

	/**
	 * @param actor
	 */
	public void removeActor(Actor actor) {
		actors.remove(actor);
	}
}
