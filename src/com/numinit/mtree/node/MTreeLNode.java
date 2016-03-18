package com.numinit.mtree.node;
import java.util.ArrayList;
import java.util.Collection;

import com.numinit.avl.ITopKMachine;
import com.numinit.mtree.MTree;
import com.numinit.mtree.node.result.MTreeResultCluster;
import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;
import com.numinit.utils.DoubleUtils;
import com.numinit.utils.IIndexedData;

/**
 * A leaf MTreeNode.
 * @author Morgan Jones
 *
 * @param <K> The key
 * @param <V> The value
 */
public class MTreeLNode<K extends IPointInMetricSpace<K>, V> extends MTreeANode<K, V> {
	/**
	 * A simple private mapping class, so we don't have to say DataWrapper<K, V> every time
	 * @author Morgan Jones
	 *
	 */
	private class Mapping extends DataWrapper<K, V> {
		/**
		 * Initializes this Mapping
		 * @param key  The key
		 * @param data The data
		 */
		public Mapping(K key, V data) {
			super(key, data);
		}
	}
	/**
	 * Our value array
	 */
	private ArrayList<Mapping> _mappings;

	/**
	 * Initializes this leaf node
	 * @param tree The tree
	 */
	public MTreeLNode(MTree<K, V> tree) {
		super(tree);
		this._mappings = this.getNewMappingArray();
	}
	
	@Override
	public IMTreeNode<K, V> stringifyValue(int idx, StringBuilder builder) {		
		builder.append(this.get(idx).getData().toString());
		return this;
	}
	
	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public int getLimit() {
		return this.getTree().getMaxLeafEntries();
	}
	
	@Override
	public int getCurrentSize() {
		return this._mappings.size();
	}
	
	@Override
	public IMTreeNode<K, V> insert(K key, V value) {
		// Add a mapping
		this.push(key, value);
		
		if (this.isFull()) {
			// Cluster
			MTreeResultCluster<K, V> result = this.cluster();
			MTreeLNode<K, V> createdLeaf = this.updateRadius(result.getKeepRadius()).getTree().getNodeFactory().getNewLNode();
			ArrayList<Mapping> oldMappings = this._mappings;
			this._mappings = this.getNewMappingArray();
			
			// Add the kept indices to the new mappings for this node
			for (int idx : result.getKeep()) {
				this.push(oldMappings.get(idx));
			}
			
			// Add the created indices to the new mappings for the new leaf
			for (int idx : result.getCreate()) {
				createdLeaf.push(oldMappings.get(idx));
			}
			
			// Commit
			return createdLeaf.updateRadius(result.getCreateRadius());
		} else {
			// This node didn't need a split
			return this;
		}
	}
	
	@Override
	public IMTreeNode<K, V> find(K query, double distance, Collection<DataWrapper<K, V>> output) {
		for (IIndexedData<K> key : this.keys()) {
			if (DoubleUtils.compare(query.getDistance(key.getData()), distance) <= 0) {
				output.add(this.get(key.getIndex()));
			}
		}
		return this;
	}
	
	@Override
	public IMTreeNode<K, V> find(K query, ITopKMachine<DataWrapper<K, V>> topK) {
		// Add all of this node's children
		for (IIndexedData<K> key : this.keys()) {
			Mapping mapping = this.get(key.getIndex());
			topK.insert(query.getDistance(mapping.getKey()), mapping);
		}
		return this;
	}
	
	@Override
	public IMTreeNode<K, V> traverse(IMTreeTraversalCallback<K, V> callback) {
		// Call the callback
		callback.operation(this);
		
		return this;
	}
	
	@Override
	public K getKey(int n) {
		return this.get(n).getKey();
	}

	@Override
	public double getRadiusFor(int idx) {
		return 0.0d;
	}
	
	/**
	 * Pushes (key, value) into this Node
	 * @param key   The key
	 * @param value The value
	 * @return this
	 */
	public MTreeLNode<K, V> push(K key, V value) {
		return this.push(new Mapping(key, value));
	}
	
	/**
	 * Pushes (key, value) into this Node
	 * @param val The mapping
	 * @return this
	 */
	protected MTreeLNode<K, V> push(Mapping val) {
		this._mappings.add(val);
		return this;
	}
	
	/**
	 * Returns the idx-th DataWrapper object
	 * @param idx The index
	 * @return The wrapper
	 */
	protected Mapping get(int idx) {
		return this._mappings.get(idx);
	}
	
	/**
	 * Returns a new mapping array
	 * @return A new mapping array
	 */
	private ArrayList<Mapping> getNewMappingArray() {
		return new ArrayList<Mapping>(this.getLimit());
	}
}