package com.numinit.mtree.node.result;

import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.mtree.utils.DataWrapper;
import com.numinit.utils.DoubleUtils;
import com.numinit.utils.IIndexedData;

/**
 * A result of a distance operation
 * @author Morgan Jones
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class MTreeResultDistance<K extends IPointInMetricSpace<K>, V> extends DataWrapper<Integer, K> implements Comparable<MTreeResultDistance<K, V>> {
	/**
	 * This distance
	 */
	private double _distance;
	
	/**
	 * Initializes this MTreeResultDistance
	 * @param idx      The index
	 * @param data     The data
	 * @param distance The distance
	 */
	public MTreeResultDistance(int idx, K data, double distance) {
		super(idx, data);
		this._distance = distance;
	}

	/**
	 * Initializes this MTreeResultDistance
	 * @param distance  The distance
	 * @param data The data
	 */
	public MTreeResultDistance(double distance, IIndexedData<K> data) {
		this(data.getIndex(), data.getData(), distance);
	}
	
	/**
	 * Returns the index
	 * @return The index
	 */
	public int getIndex() {
		return this.getKey();
	}

	/**
	 * Returns the distance
	 * @return The distance
	 */
	public double getDistance() {
		return this._distance;
	}
	
	@Override
	public int compareTo(MTreeResultDistance<K, V> other) {
		return DoubleUtils.compare(this.getDistance(), other.getDistance());
	}
}