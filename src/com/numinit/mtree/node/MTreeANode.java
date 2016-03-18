package com.numinit.mtree.node;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

import com.numinit.avl.AVLTopKMachine;
import com.numinit.avl.ITopKMachine;
import com.numinit.mtree.MTree;
import com.numinit.mtree.node.result.MTreeResultCluster;
import com.numinit.mtree.node.result.MTreeResultDistance;
import com.numinit.mtree.node.result.MTreeResultMinFast;
import com.numinit.mtree.node.result.MTreeResultMinSlow;
import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.utils.DoubleUtils;
import com.numinit.utils.IIndexedData;
import com.numinit.utils.IndexedData;

/**
 * An abstract implementation of a MTreeNode.
 * @author Morgan Jones
 *
 * @param <K> The key
 * @param <V> The value
 */
public abstract class MTreeANode<K extends IPointInMetricSpace<K>, V> implements IMTreeNode<K, V> {
	/**
	 * The tree we're a member of
	 */
	private MTree<K, V> _tree;
	
	/**
	 * Our radius
	 */
	private double _radius;
	
	/**
	 * Initializes this MTreeANode.
	 * @param _tree The tree we're a member of
	 */
	protected MTreeANode(MTree<K, V> tree) {
		this._tree = tree;
		this._radius = 0.0d;
	}
	
	@Override
	public String toString() {
		// This toString uses a StringBuilder to avoid creating and throwing away too many String references.
		StringBuilder builder = new StringBuilder();
		this.stringify(builder);
		return builder.toString();
	}
	
	@Override
	public IMTreeNode<K, V> stringify(StringBuilder builder) {
		builder.append(this.getClass().getSimpleName()).append(String.format("#<[r=%.2f]@%#08x:(", this.getRadius(), System.identityHashCode(this)));
		
		for (IIndexedData<K> key : this.keys()) {
			builder.append(key.getData().toString().replace('(', '[').replace(')', ']')).append(" => ");
			this.stringifyValue(key.getIndex(), builder);
			if (key.getIndex() < this.getCurrentSize() - 1) {
				builder.append(", ");
			}
		}
		
		builder.append(")>");
		return this;
	}

	@Override
	public MTree<K, V> getTree() {
		return this._tree;
	}
	
	@Override
	public boolean isFull() {
		return this.getCurrentSize() == this.getLimit();
	}
	
	@Override
	public MTreeResultMinFast<K, V> minFast(K query) {
		double minRadius = Double.POSITIVE_INFINITY;
		int minIdx = 0;
		
		// Loop through our keys
		for (IIndexedData<K> subkey : this.keys()) {
			double distance = query.getDistance(subkey.getData());
			if (DoubleUtils.compare(distance, minRadius) <= 0) {
				minRadius = distance;
				minIdx = subkey.getIndex();
			}
		}
		
		return new MTreeResultMinFast<K, V>(minRadius, minIdx);
	}
	
	@Override
	public MTreeResultMinSlow<K, V> minSlow(K query) {
		double minRadius = Double.POSITIVE_INFINITY;
		int minIdx = 0;
		ITopKMachine<MTreeResultDistance<K, V>> distanceMachine = new AVLTopKMachine<MTreeResultDistance<K, V>>(this.getCurrentSize());
		
		// Pick the subtree that minimizes distance
		for (IIndexedData<K> subkey : this.keys()) {
			double distance = query.getDistance(subkey.getData());
			if (DoubleUtils.compare(distance, minRadius) <= 0) {
				minRadius = distance;
				minIdx = subkey.getIndex();
			}
			distanceMachine.insert(distance, new MTreeResultDistance<K, V>(distance, subkey));
		}
		
		// Get the sorted results
		return new MTreeResultMinSlow<K, V>(minRadius, minIdx, distanceMachine.getTopK());
	}
	
	@Override
	public Iterable<IIndexedData<K>> keys() {
		// Store a reference to the current node
		final IMTreeNode<K, V> ref = this;
		
		// Return an iterable, so we can use java's foreach
		return new Iterable<IIndexedData<K>>() {
			@Override
			public Iterator<IIndexedData<K>> iterator() {
				return new Iterator<IIndexedData<K>>() {
					/**
					 * The current index
					 */
					private int _idx = 0;
					
					@Override
					public boolean hasNext() {
						return this._idx < ref.getCurrentSize();
					}

					@Override
					public IIndexedData<K> next() {
						IIndexedData<K> ret = new IndexedData<K>(this._idx, ref.getKey(this._idx));
						this._idx++;
						return ret;
					}
				};
			}
		};
	}
	
	@Override
	public K getCentroid() {
		return this.getKey(0);
	}
	
	@Override
	public double getRadius() {
		return this._radius;
	}
	
	@Override
	public IMTreeNode<K, V> setRadius(double newRadius) {
		this._radius = newRadius;
		return this;
	}
	
	@Override
	public IMTreeNode<K, V> updateRadius(double newRadius) {
		this._radius = Math.max(this.getRadius(), newRadius);
		return this;
	}
	
	/**
	 * Clusters the current node, returning a MTreeClusterResult
	 * @complexity O(n^2); n is the number of children of the current node
	 * @return a MTreeClusterResult
	 */
	protected MTreeResultCluster<K, V> cluster() {
		// Allocate a new cluster result
		MTreeResultCluster<K, V> ret = new MTreeResultCluster<K, V>(this.getCurrentSize());
		
		// Set up the seed picking loop
		IIndexedData<K> keepSeed = null, createSeed = null;
		double maxDistance = Double.NEGATIVE_INFINITY;
		
		// Use a naive O(n^2) loop to pick our seeds
		for (IIndexedData<K> k1 : this.keys()) {
			for (IIndexedData<K> k2 : this.keys()) {
				double distance = k1.getData().getDistance(k2.getData());
				if (DoubleUtils.compare(distance, maxDistance) >= 0) {
					keepSeed = k1;
					createSeed = k2;
					maxDistance = distance;
				}
			}
		}
		
		// Perform treesort by distances
		ITopKMachine<MTreeResultDistance<K, V>> keepMachine = new AVLTopKMachine<MTreeResultDistance<K, V>>(this.getCurrentSize()),
				                                createMachine = new AVLTopKMachine<MTreeResultDistance<K, V>>(this.getCurrentSize());
		for (IIndexedData<K> k : this.keys()) {
			MTreeResultDistance<K, V> keepDistance = new MTreeResultDistance<K, V>(k.getData().getDistance(keepSeed.getData()), k),
					                  createDistance = new MTreeResultDistance<K, V>(k.getData().getDistance(createSeed.getData()), k);
			keepMachine.insert(keepDistance.getDistance(), keepDistance);
			createMachine.insert(createDistance.getDistance(), createDistance);
		}

		// Perform clustering
		ArrayList<MTreeResultDistance<K, V>> keepKeys = keepMachine.getTopK(), createKeys = createMachine.getTopK();
		BitSet seen = new BitSet(this.getCurrentSize());

		for (int n = 0, i = 0, j = 0; i < keepKeys.size() && j < createKeys.size() && n < keepKeys.size(); i++, j++) {
			if (n++ < keepKeys.size()) {
				// Advance to the next valid key
				MTreeResultDistance<K, V> key = keepKeys.get(i);
				while (seen.get(key.getIndex()) && i < keepKeys.size()) {key = keepKeys.get(++i);}
				
				// Keep it
				ret.keep(key.getIndex()).setKeepRadius(key.getDistance() + this.getRadiusFor(key.getIndex()));
				seen.set(key.getIndex());
			}
			
			if (n++ < keepKeys.size()) {
				// Advance to the next valid key
				MTreeResultDistance<K, V> key = createKeys.get(j);
				while (seen.get(key.getIndex()) && j < createKeys.size()) {key = createKeys.get(++j);}
				
				// Create it
				ret.create(key.getIndex()).setCreateRadius(key.getDistance() + this.getRadiusFor(key.getIndex()));
				seen.set(key.getIndex());
			}
		}
		return ret;
	}
	
	/**
	 * Asserts that idx is valid
	 * @param idx The index
	 * @return this 
	 */
	protected MTreeANode<K, V> assertValidBounds(int idx) {
		if (idx < 0 || idx >= this.getLimit()) {
			throw new IndexOutOfBoundsException("key index is out of bounds");
		}
		return this;
	}
	
	/**
	 * Asserts that this node is not full
	 * @return this
	 */
	protected MTreeANode<K, V> assertNotFull() {
		if (this.isFull()) {
			throw new RuntimeException("node should not be full");
		}
		return this;
	}
	
	/**
	 * Assers that this node is full
	 * @return this
	 */
	protected MTreeANode<K, V> assertFull() {
		if (!this.isFull()) {
			throw new RuntimeException("node should be full");
		}
		return this;
	}
}
