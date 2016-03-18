package com.numinit.mtree.node.result;
import java.util.ArrayList;

import com.numinit.mtree.point.IPointInMetricSpace;

/**
 * Abstracts a cluster result
 * @author Morgan Jones
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class MTreeResultCluster<K extends IPointInMetricSpace<K>, V> {
	/**
	 * Lists of indices to keep and create
	 */
	ArrayList<Integer> _keep, _create;
	
	/**
	 * Spheres around our indices
	 */
	double _keepRadius, _createRadius;
	
	/**
	 * Initializes this ClusterResult with a total size
	 * @param total The size
	 */
	public MTreeResultCluster(int total) {
		this._keep = new ArrayList<Integer>(total / 2);
		this._create = new ArrayList<Integer>(total / 2);
		this._keepRadius = this._createRadius = 0.0d;
	}
	
	/**
	 * Returns the keep radius
	 * @return The keep radius
	 */
	public final double getKeepRadius() {
		return this._keepRadius;
	}
	
	/**
	 * Expands the keep radius
	 * @param radius The new radius
	 * @return This
	 */
	public final MTreeResultCluster<K, V> setKeepRadius(double radius) {
		this._keepRadius = Math.max(this.getKeepRadius(), radius);
		return this;
	}
	
	/**
	 * Returns the create radius
	 * @return The create radius
	 */
	public final double getCreateRadius() {
		return this._createRadius;
	}
	
	/**
	 * Expands the create radius
	 * @param radius The new radius
	 * @return This
	 */
	public final MTreeResultCluster<K, V> setCreateRadius(double radius) {
		this._createRadius = Math.max(this.getCreateRadius(), radius);
		return this;
	}
	
	/**
	 * Adds idx to the keep array
	 * @param idx The index
	 * @return this
	 */
	public final MTreeResultCluster<K, V> keep(int idx) {
		this.getKeep().add(idx);
		return this;
	}
	
	/**
	 * Returns the keep array
	 * @return The keep array
	 */
	public final ArrayList<Integer> getKeep() {
		return this._keep;
	}
	
	/**
	 * Adds idx to the create array
	 * @param idx The index
	 * @return this
	 */
	public final MTreeResultCluster<K, V> create(int idx) {
		this.getCreate().add(idx);
		return this;
	}
	
	/**
	 * Returns the create array
	 * @return The create array
	 */
	public final ArrayList<Integer> getCreate() {
		return this._create;
	}
}