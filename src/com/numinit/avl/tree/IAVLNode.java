package com.numinit.avl.tree;


/**
 * Interface for an AVL tree node.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public interface IAVLNode<T extends Comparable<T>> {
	/**
	 * Stringifies this node using the specified StringBuilder.
	 * Returns this node.
	 * @param builder The builder
	 * @return This node
	 */
	IAVLNode<T> stringify(StringBuilder builder);

	/**
	 * Inserts a value into this subtree, performing the correct rotations.
	 * @return The subtree resulting from insertion
	 */
	IAVLNode<T> insert(T value);
	
	/**
	 * Removes the maximum value from this subtree
	 * @return The subtree resulting from removal
	 */
	IAVLNode<T> removeMax();
	
	/**
	 * Returns the maximum value in this subtree.
	 * @return The maximum
	 */
	T max();
	
	/**
	 * Traverses this IAVLNode in sorted order, yielding the values in its subtrees to the specified IAVLTraversal function
	 * @param  traversal A function to be called for every node
	 * @return This node
	 */
	IAVLNode<T> traverse(IAVLNodeCallback<T> traversal);
	
	/**
	 * Returns the tree that this AVLNode is in.
	 * @return The tree
	 */
	AVL<T> getTree();
	
	/**
	 * Returns the height of the subtree rooted at this Node
	 * @return The subtree's height
	 */
	int getHeight();
	
	/**
	 * Updates this subtree's height
	 * @return This subtree
	 */
	IAVLNode<T> setHeight();
	
	/**
	 * Returns the value for this IAVLNode.
	 * @return The value
	 */
	T getValue();

	/**
	 * Sets the value of this IAVLNode.
	 * @param value The value
	 * @return This subtree
	 */
	IAVLNode<T> setValue(T value);
	
	/**
	 * Returns the left subtree
	 * @return The left subtree
	 */
	IAVLNode<T> getLeft();
	
	/**
	 * Returns the right subtree
	 * @return The right subtree
	 */
	IAVLNode<T> getRight();
	
	/**
	 * Sets the left subtree
	 * @param left The left subtree
	 * @return This subtree
	 */
	IAVLNode<T> setLeft(IAVLNode<T> left);

	/**
	 * Sets the right subtree
	 * @param right The right subtree
	 * @return This subtree
	 */
	IAVLNode<T> setRight(IAVLNode<T> right);
	
	/**
	 * Returns whether this tree is left deep
	 * @return True if left deep
	 */
	boolean isLeftDeep();
	
	/**
	 * Returns whether this tree is right deep
	 * @return True if right deep
	 */
	boolean isRightDeep();
	
	/**
	 * Returns whether this node is an empty node
	 * @return Whether it's empty
	 */
	boolean isEmpty();
}