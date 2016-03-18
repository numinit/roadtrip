package com.numinit.avl.tree;


/**
 * An abstract AVL node.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public abstract class AVLANode<T extends Comparable<T>> implements IAVLNode<T> {	
	/**
	 * The tree we're a member of
	 */
	private AVL<T> _tree;
	
	/**
	 * Initializes this Node
	 * @param factory The node factory
	 */
	protected AVLANode(AVL<T> tree) {
		this._tree = tree;
	}
	
	@Override
	public String toString() {
		// This toString uses a StringBuilder to avoid creating and throwing away too many String references.
		StringBuilder builder = new StringBuilder();
		this.stringify(builder);
		return builder.toString();
	}
	
	@Override
	public AVL<T> getTree() {
		return this._tree;
	}
	
	@Override
	public boolean isLeftDeep() {
		return this.getLeft().getHeight() > this.getRight().getHeight();
	}
	
	@Override
	public boolean isRightDeep() {
		return this.getRight().getHeight() > this.getLeft().getHeight();
	}
}