package com.numinit.mtree;
import java.util.ArrayList;

import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;

/**
 * This is a map from a set of keys of type PointInMetricSpace to a set of data of type DataType
 * @author cmj4
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public interface IMTree<K extends IPointInMetricSpace<K>, V> {
	/**
	 * Inserts a new key/data pair into the map
	 * @param keyToAdd  The key
	 * @param dataToAdd The data
	 */
	void insert(K key, V value);

	/**
	 * Find all of the key/data pairs in the map that fall within a particular distance of query point. If no results are found, then
	 * an empty array is returned (NOT a null!)
	 * @param query    The query
	 * @param distance The distance from the query
	 * @return All points that fall within that distance
	 */
	ArrayList<DataWrapper<K, V>> find(K query, double distance);

	/**
	 * Find the k closest key/data pairs in the map to a particular query point.
	 * If the number of points in the map is less than k, then the returned list will have less than k pairs in it
	 * if the number of points is zero, then an empty array is returned (NOT a null!)
	 * @param query The query
	 * @param k     The number of items to return
	 * @return The k closest points
	 */
	ArrayList<DataWrapper<K, V>> findKClosest(K query, int k);

	/**
	 * Returns the number of nodes that exist on a path from root to leaf in the tree...
	 * whatever the details of the implementation are, a tree with a single leaf node should return 1, and a tree
	 * with more than one leaf node should return at least two (since it must have at least one internal node).
	 * @return The depth
	 */
	public int depth();
}