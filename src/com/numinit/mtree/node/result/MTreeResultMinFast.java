package com.numinit.mtree.node.result;

import com.numinit.mtree.point.IPointInMetricSpace;

/**
 * Defines the result of a fast M-Tree min operation.
 * @author Morgan Jones
 *
 * @param <K> The key
 * @param <V> The value
 */
public class MTreeResultMinFast<K extends IPointInMetricSpace<K>, V> {
	/**
	 * The radius
	 */
	private double _radius;
	
	/**
	 * The index
	 */
	private int _idx;
	
	/**
	 * Initializes a new MTreeResultMinFast
	 * @param radius The radius
	 * @param idx The index
	 */
	public MTreeResultMinFast(double radius, int idx) {
		this._radius = radius;
		this._idx = idx;
	}
	
	/**
	 * Returns the radius
	 * @return The radius
	 */
	public double getRadius() {
		return this._radius;
	}
	
	/**
	 * Returns the index
	 * @return The index
	 */
	public int getIndex() {
		return this._idx;
	}
}