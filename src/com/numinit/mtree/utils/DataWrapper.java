package com.numinit.mtree.utils;
/**
 * MatrixWrapper for a key/data pair
 * @author cmj4
 *
 * @param <K>  the key type
 * @param <V> the data type
 */
public class DataWrapper<K, V> {
	/**
	 * The key
	 */
	K _key;
	
	/**
	 * The data
	 */
	V _data;

	/**
	 * Initializes this DataWrapper
	 * @param key  The key
	 * @param data The data
	 */
	public DataWrapper(K key, V data) {
		this._key = key;
		this._data = data;
	}

	/**
	 * Returns the key
	 * @return The key
	 */
	public K getKey() {
		return _key;
	}

	/**
	 * Returns the data
	 * @return The data
	 */
	public V getData() {
		return _data;
	}
}