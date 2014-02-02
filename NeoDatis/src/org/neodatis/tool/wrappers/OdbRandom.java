package org.neodatis.tool.wrappers;

import java.util.Random;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbRandom {
	protected static Random random = new Random();
	public static int getRandomInteger(){
		return random.nextInt();
	}
	public static double getRandomDouble(){
		return random.nextDouble();
	}
	
}
