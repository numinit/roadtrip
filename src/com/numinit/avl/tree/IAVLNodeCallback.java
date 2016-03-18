package com.numinit.avl.tree;
/**
 * A simple interface used to traverse an AVL tree.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public interface IAVLNodeCallback<T extends Comparable<T>> {
	/**
	 * The operation to perform
	 * @param obj The object
	 */
	void operation(IAVLNode<T> obj);
}