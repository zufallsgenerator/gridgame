package gamelib;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Static class to load resources as a byte array
 * 
 * @author Christer Byström
 * 
 */
public class ResourceLoader {

	private static ClassLoader classLoader = null;

	/**
	 * Load resource
	 * 
	 * @param path
	 * @return array of bytes with resource, or null
	 */
	public static byte[] load(String path) {
		InputStream inputStream = null;
		byte buf[] = null;

		if (classLoader == null) {
			System.out.println("Creating classloader!");
			ImageLoader imageLoader;
			imageLoader = new ImageLoader();
			classLoader = (imageLoader.getClass().getClassLoader());
		}

		inputStream = classLoader.getResourceAsStream(path);
		if (inputStream != null) {
		} else {
			return null;
		}

		int len;
		try {
			len = inputStream.available();
		} catch (IOException e) {
//			System.out.println("InputStream.available() failed: "
//					+ e.getMessage());
			try {
				inputStream.close();
			} catch (IOException e2) {
			}

			return null;
		}

		DataInputStream dis = new DataInputStream(inputStream);

		if (dis == null) {
//			System.out.println("Failed creating DataInputStream!");
			try {
				inputStream.close();
			} catch (IOException e) {
				// Hey, what do do if you fail closing?
			}
			return null;
		}

		buf = new byte[len];

		try {
			dis.readFully(buf);
		} catch (IOException e) {
			try {
	//			System.out.println("readFully() failed: " + e.getMessage());
				dis.close();
				inputStream.close();
			} catch (IOException e2) {
			}
			return null;
		}

		try {
			dis.close();
			inputStream.close();
		} catch (IOException e2) {
		//	System.out.println("Failed closing inputstream " + path + ":"
		//			+ e2.getMessage());
		}

		return buf;
	}

	/**
	 * Check if resource exists
	 * 
	 * @param filename
	 * @return true if resource exists
	 */
	static public Boolean exists(String filename) {
		if (classLoader == null) {
			//System.out.println("Creating classloader!");
			ImageLoader imageLoader;
			imageLoader = new ImageLoader();
			classLoader = (imageLoader.getClass().getClassLoader());
		}

		InputStream inputStream = classLoader.getResourceAsStream(filename);

		if (inputStream == null) {
			return false;
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			//System.out.println("InputStream.close() failed: " + e.getMessage());
		}

		return true;
	}

}
