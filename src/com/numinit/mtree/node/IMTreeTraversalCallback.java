package com.numinit.mtree.node;

import com.numinit.mtree.point.IPointInMetricSpace;

public interface IMTreeTraversalCallback<K extends IPointInMetricSpace<K>, V> {
	/**
	 * Calls the operation on an internal node
	 * @param internal The internal node
	 */
	void operation(MTreeINode<K, V> internal);
	
	/**
	 * Calls the operation on a leaf
	 * @param leaf The leaf node
	 */
	void operation(MTreeLNode<K, V> leaf);
}
