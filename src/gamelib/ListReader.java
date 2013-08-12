package gamelib;

import java.util.ArrayList;

/**
 * Class that loads a list of lines from a file
 * 
 * @author Christer Byström
 * 
 */
public class ListReader {
	private static final String EMPTYSTRING = "";

	private ArrayList<String> strings = null;

	/**
	 * Read a list of string from a file
	 * 
	 * @param filename
	 */

	public ListReader(String filename) {
		byte buf[];
		strings = new ArrayList<String>();

		buf = ResourceLoader.load(filename);

		if (buf != null) {
			String stringBuf;
			String stringsBuf[];

			stringBuf = new String(buf);

			stringBuf = stringBuf.replace("\r", "");

			stringsBuf = stringBuf.split("\n");

			for (int i = 0; i < stringsBuf.length; i++) {
//				System.out.println("Line: " + stringsBuf[i]);
				strings.add(stringsBuf[i]);
			}
		}

	}

	/*
	 * public ListReader(String filename) { BufferedReader inFil;
	 * 
	 * strings = new ArrayList<String>();
	 * 
	 * try {
	 * 
	 * inFil = new BufferedReader(new FileReader(filename)); } catch
	 * (FileNotFoundException e) { return; }
	 * 
	 * try { String line; do { line = inFil.readLine(); if (line != null && line !=
	 * EMPTYSTRING) { strings.add(line); } } while (line != null); } catch
	 * (IOException e) { try { inFil.close(); } catch (Exception weredoneanyway) { }
	 * strings = null; return; }
	 * 
	 * try { inFil.close(); } catch (Exception weredoneanyway) { }
	 *  }
	 */

	/**
	 * Get strings loaded
	 * 
	 * @return - ArrayList of strings
	 */
	public ArrayList<String> getStrings() {

		return strings;
	}

}
