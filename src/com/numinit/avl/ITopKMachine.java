package com.numinit.avl;
import java.util.ArrayList;

/**
 * This simple interface allows us to get the top K objects out of a large set
 * (that is, the K objects that are inserted having the **lowest** score values)
 * The idea is that you would create an ITopKMachine using a specific K.  Then,
 * you insert the values one-by-one into the machine using the "insert" method.
 * Whenever you want to obtain the current top K, you call "getTopK", which puts
 * the top K into the array list.  In addition, the machine can be queried to
 * see what is the current worst score that will still put a value into the top K.
 * @author cmj4
 * 
 */
public interface ITopKMachine<T> {
	/**
	 * Insert a new value into the machine.  If its score is greater than the current
	 * cutoff, it is ignored.  If its score is smaller than the current cutoff, the
	 * insertion will evict the value with the worst score.
	 * @param score The score
	 * @param value The value
	 */
	void insert(double score, T value);

	/**
	 * Get the current top K in an array list
	 * @return The top K items
	 */
	ArrayList<T> getTopK();

	/**
	 * Let the caller know the current cutoff value that will get one into the top K list
	 * @return The cutoff
	 */
	double getCurrentCutoff();
}
