package com.numinit.mtree;
import java.util.ArrayList;

import com.numinit.avl.AVLTopKMachine;
import com.numinit.avl.ITopKMachine;
import com.numinit.mtree.node.IMTreeNode;
import com.numinit.mtree.node.IMTreeTraversalCallback;
import com.numinit.mtree.node.MTreeNodeFactory;
import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;

/**
 * Implements a M-Tree.
 * @author Morgan Jones
 *
 */
public class MTree<K extends IPointInMetricSpace<K>, V> implements IMTree<K, V> {
	/**
	 * Limits on internal and leaf nodes
	 */
	private int _maxInternal, _maxLeaf;
	
	/**
	 * The root
	 */
	private IMTreeNode<K, V> _root;
	
	/**
	 * A factory for producing nodes
	 */
	private MTreeNodeFactory<K, V> _factory;

	/**
	 * Initializes this MTree
	 * @param maxInternal The maximum number of internal slots
	 * @param maxLeaf     The maximum number of leaf slots
	 */
	public MTree(int maxInternal, int maxLeaf) {
		if (maxInternal <= 1 || maxLeaf <= 0) {
			throw new IllegalArgumentException("MTree must have at least 2 internal slots and 1 leaf slot");
		}
		this._maxInternal = maxInternal;
		this._maxLeaf = maxLeaf;
		this._factory = new MTreeNodeFactory<K, V>(this);
		this._root = this.getNodeFactory().getNewLNode();
	}
	
	@Override
	public String toString() {
		return String.format("#<%s[internal=%d,leaf=%d]@%#08x:%s>", this.getClass().getSimpleName(),
				this.getMaxInternalEntries(), this.getMaxLeafEntries(), System.identityHashCode(this), this._root.toString());
	}
	
	@Override
	public void insert(K key, V value) {
		IMTreeNode<K, V> node = this._root.insert(key, value);
		
		// Re-root the tree if we ended up splitting the root
		if (node != this._root) {
			this._root = this.getNodeFactory().getNewINode().push(this._root.getCentroid(), this._root).push(node.getCentroid(), node);
		}
	}

	@Override
	public ArrayList<DataWrapper<K, V>> find(K query, double distance) {
		if (distance < 0.0d) {
			throw new IllegalArgumentException("distance must be >= 0");
		}
		
		ArrayList<DataWrapper<K, V>> ret = new ArrayList<DataWrapper<K, V>>();
		this._root.find(query, distance, ret);
		return ret;
	}

	@Override
	public ArrayList<DataWrapper<K, V>> findKClosest(K query, int k) {
		if (k < 0) {
			throw new IllegalArgumentException("k must be >= 0");
		}
		ITopKMachine<DataWrapper<K, V>> topKMachine = new AVLTopKMachine<DataWrapper<K, V>>(k);
		this._root.find(query, topKMachine);
		return topKMachine.getTopK();
	}

	@Override
	public int depth() {
		return this._root.getDepth();
	}
	
	/**
	 * Traverses this MTree.
	 * @param callback The callback
	 */
	public void traverse(IMTreeTraversalCallback<K, V> callback) {
		this._root.traverse(callback);
	}
	
	/**
	 * Returns the max number of internal entries in the nodes of this MTree
	 * @return The max number of internal entries in the nodes of this MTree
	 */
	public int getMaxInternalEntries() {
		return this._maxInternal;
	}
	
	/**
	 * Returns the max number of leaf entries in the nodes of this MTree
	 * @return The max number of leaf entries in the nodes of this MTree
	 */
	public int getMaxLeafEntries() {
		return this._maxLeaf;
	}
	
	/**
	 * Returns this tree's NodeFactory
	 * @return The NodeFactory
	 */
	public MTreeNodeFactory<K, V> getNodeFactory() {
		return this._factory;
	}
}
