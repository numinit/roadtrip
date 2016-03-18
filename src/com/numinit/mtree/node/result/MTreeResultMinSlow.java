package com.numinit.mtree.node.result;
import java.util.List;

import com.numinit.mtree.point.IPointInMetricSpace;

/**
 * Defines the result of a slow M-Tree min operation.
 * @author Morgan Jones
 *
 * @param <K> The key
 * @param <V> The value
 */
public class MTreeResultMinSlow<K extends IPointInMetricSpace<K>, V> extends MTreeResultMinFast<K, V> {
	/**
	 * The keys
	 */
	private List<MTreeResultDistance<K, V>> _keys;
	
	/**
	 * Initializes this MTreeResultMinSlow
	 * @param radius The radius
	 * @param idx The index
	 * @param 
	 */
	public MTreeResultMinSlow(double radius, int idx, List<MTreeResultDistance<K, V>> keys) {
		super(radius, idx);
		this._keys = keys;
	}
	
	/**
	 * Returns the keys
	 * @return The keys
	 */
	public List<MTreeResultDistance<K, V>> getKeys() {
		return this._keys;
	}
}
