package com.numinit.avl.tree;


/**
 * A class encapsulating an AVL tree.
 * @author Morgan Jones
 *
 * @param <T> The type to stick in the tree's nodes. Must implement Comparable<T>.
 */
public class AVL<T extends Comparable<T>> {
	/**
	 * Constants for stat gets/sets
	 */
	public static final int AVL_ROTATE_RR = 0, AVL_ROTATE_RL = 1, AVL_ROTATE_LL = 2, AVL_ROTATE_LR = 3, AVL_ROTATE_AR = 4;
	public static final int AVL_STATS_LENGTH = 5;
	
	/**
	 * The maximum capacity of this tree and current element count
	 */
	private int _k, _count;
	
	/**
	 * The maximum item seen so far
	 */
	T _cutoff;
	
	/**
	 * A factory for node creation
	 */
	private AVLNodeFactory<T> _factory;
	
	/**
	 * Stats
	 */
	private int[] _stats;
	
	/**
	 * This tree's root
	 */
	private IAVLNode<T> _root;
	
	/**
	 * Initializes this AVL
	 * @param k The maximum number of items we should store
	 */
	public AVL(int k) {
		if (k <= 0) {
			throw new IllegalArgumentException(String.format("invalid k value %d, choose 0 or greater", k));
		}
		
		// Set initial constants
		this._k = k;
		this._count = 0;
		this._cutoff = null;
		
		// Create a node factory
		this._factory = new AVLNodeFactory<T>(this);
		
		// Generate the stats array
		this._stats = new int[AVL_STATS_LENGTH];
		
		// Allocate the root
		this._root = this._factory.getNewEmptyNode();
	}
	
	/**
	 * Initializes this AVL tree with no practical limit on storage size
	 */
	public AVL() {
		this(Integer.MAX_VALUE);
	}
	
	@Override
	public String toString() {
		return String.format("#<%s[k=%d,count=%d,cutoff=%s,stats=<rr=%d,rl=%d,ll=%d,lr=%d,ar=%d>]@%#08x:%s>", this.getClass().getSimpleName(),
				this.getK(), this.getCount(), this.getCutoff(),
				this.getStat(AVL_ROTATE_RR), this.getStat(AVL_ROTATE_RL), this.getStat(AVL_ROTATE_LL), this.getStat(AVL_ROTATE_LR), this.getStat(AVL_ROTATE_AR),
				System.identityHashCode(this), this._root.toString());
	}
	
	/**
	 * Inserts 'value' into this AVL.
	 * @param value The value
	 * @return This AVL
	 */
	public AVL<T> insert(T value) {
		// If we don't have a cutoff assigned yet or the value is lte the cutoff, insert
		if (this.getCount() < this.getK() || value.compareTo(this.getCutoff()) <= 0) {
			// Perform the insert, with rotations
			this._root = this._root.insert(value);
			
			if (this.getCount() == this.getK()) { 
				// Don't increment, and remove the maximum item so we stay at k total
				this._root.removeMax();
				this._cutoff = this._root.max();
			} else {
				// Increment the count
				this._count++;
				
				// Just update the cutoff if we're now full
				if (this.getCount() == this.getK()) {
					this._cutoff = this._root.max();
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Traverses this tree with the specified IAVLTraversal object
	 * @param traverse The traversal object
	 * @return This AVL
	 */
	public AVL<T> traverse(IAVLNodeCallback<T> traverse) {
		this._root.traverse(traverse);
		return this;
	}
	
	/**
	 * Returns this tree's NodeFactory.
	 * @return The NodeFactory
	 */
	public AVLNodeFactory<T> getNodeFactory() {
		return this._factory;
	}
	
	/**
	 * Returns the cutoff item. Any greater items than this item will not be added.
	 * @return The cutoff item
	 */
	public T getCutoff() {
		return this._cutoff;
	}
	
	/**
	 * Returns the maximum number of items we can store
	 * @return The maximum number of items
	 */
	public int getK() {
		return this._k;
	}
	
	/**
	 * Returns the count of items in this AVL
	 * @return The item count
	 */
	public int getCount() {
		return this._count;
	}
	
	/**
	 * Gets the idx-th stat from the stats array
	 * @param idx The index
	 * @return    The stat value
	 */
	public int getStat(int idx) {
		if (idx > this._stats.length) {
			throw new IllegalArgumentException(String.format("invalid stat idx %d", idx));
		}
		return this._stats[idx];
	}
	
	/**
	 * Increments the idx-th stat in the stats array
	 * @param idx The index
	 * @return    The new stat value
	 */
	public int incrementStat(int idx) {
		this._stats[idx] = this.getStat(idx) + 1;
		return this._stats[idx];
	}
}
