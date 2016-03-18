package com.numinit.utils;

/**
 * Utility class for comparison of double-precision values
 * @author Morgan Jones
 *
 */
public class DoubleUtils {
	/**
	 * Tolerance for double comparisons
	 */
	private static final double TOLERANCE = 1.0e-10;
	
	/**
	 * Compares two double precision floats.
	 * @param d1 The first double
	 * @param d2 The second double
	 * @param tolerance The tolerance
	 * @return   0 if d1 and d2 are approximately equal, -1 if d1 < d2, 1 if d1 > d2
	 */
	public static int compare(double d1, double d2, double tolerance) {
		int comparison = Double.compare(d1, d2);
		return comparison == 0 || Math.abs(d2 - d1) < tolerance ? 0 : comparison;
	}
	
	/**
	 * Compares two double precision floats.
	 * @param d1 The first double
	 * @param d2 The second double
	 * @return 0 if d1 and d2 are approximately equal, -1 if d1 < d2, 1 if d1 > d2
	 */
	public static int compare(double d1, double d2) {
		return compare(d1, d2, TOLERANCE);
	}
	
	/**
	 * Approximate double precision comparison
	 * @param d1 The first double
	 * @param d2 The second double
	 * @return true if d1 and d2 are "close enough"
	 */
	public static boolean equal(double d1, double d2) {
		return compare(d1, d2) == 0;
	}
	
	/**
	 * Alias of equal
	 * @param d1 The first double
	 * @param d2 The second double
	 * @return true if d1 and d2 are "close enough"
	 */
	public static boolean equals(double d1, double d2) {
		return equal(d1, d2);
	}
}
