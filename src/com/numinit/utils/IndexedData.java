package com.numinit.utils;

/**
 * IndexedData implementation
 * @author Morgan Jones
 *
 * @param <T> The type of data
 */
public class IndexedData<T> implements IIndexedData<T> {
	/**
	 * The index
	 */
	private int _idx;
	
	/**
	 * The value
	 */
	private T   _val;

	/**
	 * Constructs a new immutable IndexedData
	 * @param idx The index
	 * @param val The value
	 */
	public IndexedData(int idx, T val) {
		this._idx = idx;
		this._val = val;
	}
	
	@Override
	public String toString() {
		return String.format("#<%s@%#08x:[%d=%s]>", this.getClass().getSimpleName(), System.identityHashCode(this), this.getIndex(), this.getData().toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see IIndexedData#getIndex()
	 */
	@Override
	public int getIndex() {
		return this._idx;
	}
	
	/**
	 * Sets the index
	 * @param idx The new index
	 */
	public void setIndex(int idx) {
		this._idx = idx;
	}

	/*
	 * (non-Javadoc)
	 * @see IIndexedData#getData()
	 */
	@Override
	public T getData() {
		return this._val;
	}
	
	/**
	 * Sets the data
	 * @param data The new data
	 */
	public void setData(T data) {
		this._val = data;
	}
}