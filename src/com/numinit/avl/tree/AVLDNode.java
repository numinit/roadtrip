package com.numinit.avl.tree;


/**
 * A data AVL node.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public class AVLDNode<T extends Comparable<T>> extends AVLANode<T> {
	/**
	 * This Node's value
	 */
	private T _val;

	/**
	 * Left and right children
	 */
	private IAVLNode<T> _left, _right;
	
	/**
	 * The cached height for this subtree
	 */
	private int _height;

	/**
	 * Initializes this data node
	 * @param tree The factory to make AVL nodes
	 * @param val  The value to insert
	 */
	public AVLDNode(AVL<T> tree, T val) {
		super(tree);
		this._val = val;
		this._left = this.getTree().getNodeFactory().getNewEmptyNode();
		this._right = this.getTree().getNodeFactory().getNewEmptyNode();
		this._height = 0;
	}
	
	@Override
	public IAVLNode<T> stringify(StringBuilder builder) {
		// Walk the left
		builder.append("#<").append(this.getClass().getSimpleName()).append(":(");
		this.getLeft().stringify(builder);
		
		// Just include the value and height
		builder.append(" [val=").append(this._val.toString().replace('(', '[').replace(')', ']')).append(",height=").append(this.getHeight()).append("] ");
		
		// Walk the right
		this.getRight().stringify(builder);
		builder.append(")>");
		
		// Outta here
		return this;
	}

	@Override
	public IAVLNode<T> insert(T value) {
		// Insert!
		// < 0: value < val
		// = 0: value = val
		// > 0: value > val
		int comparison = value.compareTo(this.getValue());
		if (comparison <= 0) {
			// Go down the left
			this.setLeft(this.getLeft().insert(value));
		} else {
			// Go down the right
			this.setRight(this.getRight().insert(value));
		}
		
		// Handle unbalanced cases
		int diff = this.getLeft().getHeight() - this.getRight().getHeight();
		if (diff > 1) {
			// unbalanced to the left
			this.rotateL();
		} else if (diff < -1) {
			// unbalanced to the right
			this.rotateR();
		}
		
		// Update the height
		return this.setHeight();
	}
	
	@Override
	public IAVLNode<T> removeMax() {
		if (this.getRight().isEmpty()) {
			// This is the maximum, replace it with the left subtree
			return this.getLeft();
		} else {
			// Otherwise, just walk down the right
			this.setRight(this.getRight().removeMax());
		}
		
		// Rebalance
		int diff = this.getLeft().getHeight() - this.getRight().getHeight();
		if (diff > 1) {
			this.rotateAR();
		} else if (diff < -1) {
			throw new RuntimeException("logic failed us, the unbalance is facing the wrong way on removal");
		}
		
		// Update the height
		return this.setHeight();
	}
	
	@Override
	public T max() {
		// If right is empty, return this, otherwise go right
		return this.getRight().isEmpty() ? this.getValue() : this.getRight().max();
	}
	
	@Override
	public IAVLNode<T> traverse(IAVLNodeCallback<T> traversal) {
		// Walk the left subtree
		this.getLeft().traverse(traversal);
		
		// Call with ourself
		traversal.operation(this);
		
		// Walk the right subtree
		this.getRight().traverse(traversal);
		
		// Outta here
		return this;
	}
	
	@Override
	public int getHeight() {
		return this._height;
	}
	
	@Override
	public IAVLNode<T> setHeight() {
		this._height = Math.max(this.getLeft().getHeight(), this.getRight().getHeight()) + 1;
		return this;
	}
	
	@Override
	public T getValue() {
		return this._val;
	}

	@Override
	public IAVLNode<T> setValue(T value) {
		if (value == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		this._val = value;
		return this;
	}
	
	@Override
	public IAVLNode<T> getLeft() {
		return this._left;
	}

	@Override
	public IAVLNode<T> getRight() {
		return this._right;
	}
	
	@Override
	public IAVLNode<T> setLeft(IAVLNode<T> left) {
		if (left == null) {
			throw new IllegalArgumentException("left child must not be null");
		}
		this._left = left;
		return this;
	}

	@Override
	public IAVLNode<T> setRight(IAVLNode<T> right) {
		if (right == null) {
			throw new IllegalArgumentException("right child must not be null");
		}
		this._right = right;
		return this;
	}
	
	@Override
	public boolean isEmpty() {
		// This is a data node
		return false;
	}
	
	/**
	 * Performs a left rotation on this node
	 * @return the result of the rotation
	 */
	private IAVLNode<T> rotateL() {
		if (this.getLeft().isLeftDeep()) {
			return this.rotateLL();
		} else if (this.getLeft().isRightDeep()) {
			return this.rotateLR();
		} else {
			throw new RuntimeException("left unbalance is neither left nor right deep");
		}
	}
	
	/**
	 * Performs a right rotation on this node
	 * @return the result of the rotation
	 */
	private IAVLNode<T> rotateR() {
		if (this.getRight().isLeftDeep()) {
			return this.rotateRL();
		} else if (this.getRight().isRightDeep()) {
			return this.rotateRR();
		} else {
			throw new RuntimeException("right unbalance is neither left nor right deep");
		}
	}
	
	/**
	 * Performs a left/left-deep rotation on this subtree.
	 * @return This subtree
	 */
	private IAVLNode<T> rotateLL() {
		this.getTree().incrementStat(AVL.AVL_ROTATE_LL);
		return makeRightDeep(this);
	}
	
	/**
	 * Performs a right/right-deep rotation on this subtree.
	 * @return This subtree
	 */
	private IAVLNode<T> rotateRR() {
		this.getTree().incrementStat(AVL.AVL_ROTATE_RR);
		return makeLeftDeep(this);
	}
	
	/**
	 * Performs a left/right-deep rotation on this subtree.
	 * @return This subtree
	 */
	private IAVLNode<T> rotateLR() {
		this.getTree().incrementStat(AVL.AVL_ROTATE_LR);
		makeLeftDeep(this.getLeft());
		return makeRightDeep(this);
	}

	/**
	 * Performs a right/left-deep rotation on this subtree.
	 * @return This subtree
	 */
	private IAVLNode<T> rotateRL() {
		this.getTree().incrementStat(AVL.AVL_ROTATE_RL);
		makeRightDeep(this.getRight());
		return makeLeftDeep(this);
	}
	
	/**
	 * Performs an after-removal rotation
	 * @return This subtree
	 */
	private IAVLNode<T> rotateAR() {
		this.getTree().incrementStat(AVL.AVL_ROTATE_AR);

		// Make the left left deep if the left is right deep
		if (this.getLeft().isRightDeep()) {
			makeLeftDeep(this.getLeft());
		}
		
		return makeRightDeep(this);
	}
	
	/**
	 * Makes root left deep, returning root
	 * @param root The root
	 * @return The root
	 */
	private IAVLNode<T> makeLeftDeep(IAVLNode<T> root) {
		// Grab references
		IAVLNode<T> newLeft = root.getRight(), newRight = newLeft.getRight();
		
		// Move the left to the right on the new left child, and update the left
		newLeft.setRight(newLeft.getLeft()).setLeft(root.getLeft());
		
		// Swap values, and hang the new left and right values off the root
		return swap(root, newLeft).setLeft(newLeft.setHeight()).setRight(newRight.setHeight()).setHeight();
	}
	
	/**
	 * Makes root right deep, returning root
	 * @param root The root
	 * @return The root
	 */
	private IAVLNode<T> makeRightDeep(IAVLNode<T> root) {
		// Grab references
		IAVLNode<T> newRight = root.getLeft(), newLeft = newRight.getLeft();
		
		// Move the right to the left on the new right child, and update the right
		newRight.setLeft(newRight.getRight()).setRight(root.getRight());
		
		// Swap values, and hang the new left and right values off the root
		return swap(root, newRight).setLeft(newLeft.setHeight()).setRight(newRight.setHeight()).setHeight();
	}
	
	/**
	 * Swaps the values of two nodes (n1 and n2)
	 * @param n1 The first node
	 * @param n2 The second node
	 * @return   The first node
	 */
	private IAVLNode<T> swap(IAVLNode<T> n1, IAVLNode<T> n2) {
		T tmp = n1.getValue();
		n1.setValue(n2.getValue());
		n2.setValue(tmp);
		return n1;
	}
}