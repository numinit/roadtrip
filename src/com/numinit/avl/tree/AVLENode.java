package com.numinit.avl.tree;


/**
 * An empty AVL node.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public class AVLENode<T extends Comparable<T>> extends AVLANode<T> {
	/**
	 * String describing an unsupported operation
	 */
	private static final String UNSUPPORTED_OPERATION = "this operation is not supported for this node";
	
	/**
	 * Initializes an empty node
	 * @param factory The NodeFactory
	 */
	public AVLENode(AVL<T> tree) {
		super(tree);
	}

	@Override
	public IAVLNode<T> stringify(StringBuilder builder) {
		builder.append("#<").append(this.getClass().getSimpleName()).append(":()>");
		return this;
	}
	
	@Override
	public IAVLNode<T> insert(T value) {
		// Return a new data node
		return this.getTree().getNodeFactory().getNewDataNode(value);
	}
	
	@Override
	public IAVLNode<T> removeMax() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}
	
	@Override
	public T max() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}
	
	@Override
	public IAVLNode<T> traverse(IAVLNodeCallback<T> traversal) {
		// Call the function 0 times
		return this;
	}

	@Override
	public T getValue() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public IAVLNode<T> setValue(T value) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public IAVLNode<T> getLeft() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public IAVLNode<T> getRight() {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public IAVLNode<T> setLeft(IAVLNode<T> left) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public IAVLNode<T> setRight(IAVLNode<T> right) {
		throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
	}

	@Override
	public int getHeight() {
		// We're below subtrees of height 0
		return -1;
	}
	
	@Override
	public IAVLNode<T> setHeight() {
		// Do nothing
		return this;
	}
	
	@Override
	public boolean isEmpty() {
		// We're empty
		return true;
	}
}
