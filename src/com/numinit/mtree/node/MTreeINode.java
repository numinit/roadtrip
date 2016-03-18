package com.numinit.mtree.node;
import java.util.ArrayList;
import java.util.Collection;

import com.numinit.avl.ITopKMachine;
import com.numinit.mtree.MTree;
import com.numinit.mtree.node.result.MTreeResultCluster;
import com.numinit.mtree.node.result.MTreeResultDistance;
import com.numinit.mtree.node.result.MTreeResultMinFast;
import com.numinit.mtree.node.result.MTreeResultMinSlow;
import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;
import com.numinit.utils.DoubleUtils;
import com.numinit.utils.IIndexedData;

/**
 * An internal MTreeNode.
 * @author Morgan Jones
 *
 * @param <K> The key
 * @param <V> The value
 */
public class MTreeINode<K extends IPointInMetricSpace<K>, V> extends MTreeANode<K, V> {
	/**
	 * A simple private sphere class, so we don't have to say DataWrapper<K, IMTreeNode<K, V>> all the time
	 * @author Morgan Jones
	 *
	 */
	private class Sphere extends DataWrapper<K, IMTreeNode<K, V>> {
		/**
		 * Initializes this Sphere
		 * @param key  The key
		 * @param data The data
		 */
		public Sphere(K key, IMTreeNode<K, V> data) {
			super(key, data);
		}
	}

	/**
	 * Our subtree array
	 */
	private ArrayList<Sphere> _subtrees;

	/**
	 * Initializes this inode
	 * @param tree The tree that we're a part of
	 */
	public MTreeINode(MTree<K, V> tree) {
		super(tree);
		
		// Allocate enough space to hold our subtrees
		this._subtrees = this.getNewSphereArray();
	}
	
	@Override
	public IMTreeNode<K, V> stringifyValue(int idx, StringBuilder builder) {		
		this.get(idx).getData().stringify(builder);
		return this;
	}
	
	@Override
	public int getDepth() {
		int maxDepth = Integer.MIN_VALUE;
		for (Sphere subtree : this._subtrees) {
			int depth = subtree.getData().getDepth();
			if (depth > maxDepth) {
				maxDepth = depth;
			}
		}
		return maxDepth + 1;
	}

	@Override
	public int getLimit() {
		return this.getTree().getMaxInternalEntries();
	}
	
	@Override
	public int getCurrentSize() {
		return this._subtrees.size();
	}
	
	@Override
	public IMTreeNode<K, V> insert(K key, V value) {
		// Find the closest key
		MTreeResultMinFast<K, V> minResult = this.minFast(key);

		// Update the radius and recursively insert
		Sphere subsphere = this.get(minResult.getIndex());
		IMTreeNode<K, V> subtree = subsphere.getData().updateRadius(minResult.getRadius()).insert(key, value);
		if (subtree == null) {
			throw new RuntimeException("returned subtree is null");
		}
		
		// References aren't the same, we've got a new subtree
		if (subtree != subsphere.getData()) {
			// Append the subtree
			this.push(subtree.getCentroid(), subtree);
			
			if (this.isFull()) {
				// Cluster
				MTreeResultCluster<K, V> result = this.cluster();
				MTreeINode<K, V> createdInternal = this.updateRadius(result.getKeepRadius()).getTree().getNodeFactory().getNewINode();
				ArrayList<Sphere> oldSubtrees = this._subtrees;
				this._subtrees = this.getNewSphereArray();
				
				// Add the kept indices to the new mappings for this node
				for (int idx : result.getKeep()) {
					this.push(oldSubtrees.get(idx));
				}
				
				// Add the created indices to the new mappings for the new leaf
				for (int idx : result.getCreate()) {
					createdInternal.push(oldSubtrees.get(idx));
				}
				
				// Commit
				return createdInternal.updateRadius(result.getCreateRadius());
			} else {
				// This node didn't need a split
				return this;
			}
		} else {
			// This node didn't need a split
			return this;
		}
	}
	
	@Override
	public IMTreeNode<K, V> find(K query, double distance, Collection<DataWrapper<K, V>> output) {
		for (IIndexedData<K> key : this.keys()) {
			Sphere sphere = this.get(key.getIndex());
			if (DoubleUtils.compare(query.getDistance(sphere.getKey()), distance + sphere.getData().getRadius()) <= 0) {
				sphere.getData().find(query, distance, output);
			}
		}
		return this;
	}
	
	@Override
	public IMTreeNode<K, V> find(K query, ITopKMachine<DataWrapper<K, V>> topK) {
		MTreeResultMinSlow<K, V> minResult = this.minSlow(query);
		for (MTreeResultDistance<K, V> distance : minResult.getKeys()) {
			Sphere sphere = this.get(distance.getIndex());
			if (DoubleUtils.compare(query.getDistance(sphere.getKey()), distance.getDistance() + topK.getCurrentCutoff()) <= 0) {
				sphere.getData().find(query, topK);
			} else {
				break;
			}
		}
		return this;
	}
	
	@Override
	public IMTreeNode<K, V> traverse(IMTreeTraversalCallback<K, V> callback) {
		// Call the callback
		callback.operation(this);
		
		// Traverse children
		for (IIndexedData<K> key : this.keys()) {
			this.get(key.getIndex()).getData().traverse(callback);
		}
		
		return this;
	}
	
	@Override
	public double getRadiusFor(int idx) {
		return this.get(idx).getData().getRadius();
	}
	
	@Override
	public K getKey(int n) {
		return this.get(n).getKey();
	}
	
	/**
	 * Pushes (key, value) into this Node
	 * @param key The key
	 * @param ref The child reference
	 * @return this
	 */
	public MTreeINode<K, V> push(K key, IMTreeNode<K, V> ref) {
		return this.push(new Sphere(key, ref));
	}
	
	/**
	 * Pushes (key, value) into this Node
	 * @param val The sphere
	 * @return this
	 */
	protected MTreeINode<K, V> push(Sphere val) {
		this._subtrees.add(val);
		return this;
	}
	
	/**
	 * Returns the idx-th subtree
	 * @param idx The index
	 * @return The subtree
	 */
	protected Sphere get(int idx) {
		return this._subtrees.get(idx);
	}
	
	/**
	 * Returns a new Sphere array
	 * @return a new Sphere array
	 */
	private ArrayList<Sphere> getNewSphereArray() {
		return new ArrayList<Sphere>(this.getLimit());
	}
}