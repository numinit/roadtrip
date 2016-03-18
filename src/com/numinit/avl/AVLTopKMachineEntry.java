package com.numinit.avl;

import com.numinit.utils.DoubleUtils;

/**
 * A single entry in an AVL tree. Implements Comparable.
 * @author Morgan Jones
 *
 * @param <T> The value type
 */
public class AVLTopKMachineEntry<T> implements Comparable<AVLTopKMachineEntry<T>> {
	/**
	 * The score
	 */
	private double _score;

	/**
	 * The data
	 */
	private T _data;

	/**
	 * Initializes a new Entry.
	 * @param score The score
	 * @param data  The data
	 */
	public AVLTopKMachineEntry(double score, T data) {
		this._score = score;
		this._data  = data;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("#<%s@%#08x:%+.2f:%s>", this.getClass().getSimpleName(), System.identityHashCode(this), this.getScore(), this.getData().toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AVLTopKMachineEntry<T> other) {
		return DoubleUtils.compare(this.getScore(), other.getScore());
	}
	
	/**
	 * Returns the score
	 * @return The score
	 */
	public double getScore() {
		return this._score;
	}

	/**
	 * Returns the data
	 * @return The data
	 */
	public T getData() {
		return this._data;
	}
}