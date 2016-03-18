package com.numinit.mtree.node;
import java.util.Collection;

import com.numinit.avl.ITopKMachine;
import com.numinit.mtree.MTree;
import com.numinit.mtree.node.result.MTreeResultMinFast;
import com.numinit.mtree.node.result.MTreeResultMinSlow;
import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;
import com.numinit.utils.IIndexedData;

/**
 * Interface for a MTreeNode.
 * @author Morgan Jones
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public interface IMTreeNode<K extends IPointInMetricSpace<K>, V> {
	/**
	 * Stringifies this IMTreeNode with the given builder
	 * @param builder The StringBuilder to use
	 * @return This node
	 */
	IMTreeNode<K, V> stringify(StringBuilder builder);
	
	/**
	 * Stringifies an indexed value in this IMTreeNode
	 * @param idx The index
	 * @param builder The StringBuilder to use
	 * @return This node
	 */
	IMTreeNode<K, V> stringifyValue(int idx, StringBuilder builder);

	/**
	 * Returns the tree that this Node is a member of
	 * @return The tree
	 */
	MTree<K, V> getTree();
	
	/**
	 * Inserts into this M-Tree.
	 * @param key   The key
	 * @param value The value
	 * @return The new node resulting from insertion, or this if no new nodes resulted from insertion.
	 */
	IMTreeNode<K, V> insert(K key, V value);
	
	/**
	 * Finds all objects `distance' from `query' in this subtree.
	 * @param query    The query key
	 * @param distance The distance from the query key
	 * @param output   A collection to output to
	 * @return This node
	 */
	IMTreeNode<K, V> find(K query, double distance, Collection<DataWrapper<K, V>> output);
	
	/**
	 * Finds the closest objects to `query' in this subtree.
	 * @param query The query key
	 * @param topK  An ITopKMachine to output to
	 * @return This node
	 */
	IMTreeNode<K, V> find(K query, ITopKMachine<DataWrapper<K, V>> topK);
	
	/**
	 * Returns the index and radius of the key nearest query
	 * @param query The query
	 * @return A new MTreeMinFastResult
	 */
	MTreeResultMinFast<K, V> minFast(K query);
	
	/**
	 * Returns the same details as minFast, plus an ordered ranking of key distances
	 * @param query The query
	 * @return A new MTreeMinSlowResult
	 */
	MTreeResultMinSlow<K, V> minSlow(K query);
	
	/**
	 * Returns an iterable object over this node's keys
	 * @return an iterable object
	 */
	Iterable<IIndexedData<K>> keys();
	
	/**
	 * Traverses this subtree
	 * @param callback The callback
	 * @return This subtree
	 */
	IMTreeNode<K, V> traverse(IMTreeTraversalCallback<K, V> callback);
	
	/**
	 * Returns this node's radius
	 * @return This node's radius
	 */
	double getRadius();
	
	/**
	 * Returns the radius of a data element in this node
	 */
	double getRadiusFor(int idx);
	
	/**
	 * Sets this node's radius
	 * @param newRadius The new radius
	 * @return This node
	 */
	IMTreeNode<K, V> setRadius(double newRadius);
	
	/**
	 * Expands this node's radius, setting it to max(getRadius, newRadius).
	 * @param newRadius The new radius
	 * @return This node
	 */
	IMTreeNode<K, V> updateRadius(double newRadius);
	
	/**
	 * Returns the nth key
	 * @return the nth key
	 */
	K getKey(int n);
	
	/**
	 * Returns the centroid (the 0th key)
	 * @return the centroid
	 */
	K getCentroid();
	
	/**
	 * Returns the current depth of this subtree
	 * @return The current depth
	 */
	int getDepth();

	/**
	 * Returns the maximum number of items that can fit in this Node
	 * @return The maximum number of items
	 */
	int getLimit();
	
	/**
	 * Returns the current number of items in this Node
	 * @return The current number of items
	 */
	int getCurrentSize();
	
	/**
	 * Returns whether we're full
	 * @return true if full
	 */
	boolean isFull();
}