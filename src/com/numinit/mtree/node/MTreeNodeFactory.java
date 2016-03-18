package com.numinit.mtree.node;

import com.numinit.mtree.MTree;
import com.numinit.mtree.point.IPointInMetricSpace;

/**
 * A factory for building MTreeNodes.
 * @author Morgan Jones
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class MTreeNodeFactory<K extends IPointInMetricSpace<K>, V> {
	/**
	 * The containing tree
	 */
	private final MTree<K, V> _tree;
	
	/**
	 * Initializes an MTreeNodeFactory
	 * @param tree The tree
	 */
	public MTreeNodeFactory(MTree<K, V> tree) {
		this._tree = tree;
	}

	/**
	 * Returns a new internal node
	 * @return A new internal node
	 */
	public final MTreeINode<K, V> getNewINode() {
		return new MTreeINode<K, V>(this._tree);
	}
	
	/**
	 * Returns a new leaf node
	 * @return A new leaf node
	 */
	public final MTreeLNode<K, V> getNewLNode() {
		return new MTreeLNode<K, V>(this._tree);
	}
}
