package com.numinit.avl.tree;


/**
 * A factory for building AVLNodes.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public class AVLNodeFactory<T extends Comparable<T>> {
	/**
	 * The containing tree
	 */
	private final AVL<T> _tree;

	/**
	 * The empty node
	 */
	private final AVLENode<T> _empty;
	
	/**
	 * Initializes an AVLNodeFactory
	 * @param tree The tree
	 */
	public AVLNodeFactory(AVL<T> tree) {
		this._tree = tree;
		this._empty = new AVLENode<T>(this._tree);
	}

	/**
	 * Returns a new data node.
	 * @param  data The data
	 * @return A new data node
	 */
	public final IAVLNode<T> getNewDataNode(T data) {
		return new AVLDNode<T>(this._tree, data);
	}
	
	/**
	 * Returns the empty node.
	 * @return The empty node
	 */
	public final IAVLNode<T> getNewEmptyNode() {
		return this._empty;
	}
}
