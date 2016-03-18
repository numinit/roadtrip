package com.numinit.avl;
import java.util.ArrayList;

import com.numinit.avl.tree.AVL;

/**
 * AVL implementation of the TopKMachine
 * @author Morgan Jones
 *
 */
public class AVLTopKMachine<T> implements ITopKMachine<T> {
	/**
	 * The underlying AVL tree
	 */
	private AVL<AVLTopKMachineEntry<T>> _tree;

	/**
	 * Initializes this AVLTopKMachine.
	 * @param k The number of items
	 */
	public AVLTopKMachine(int k) {
		this._tree = new AVL<AVLTopKMachineEntry<T>>(k);
	}
	
	@Override
	public String toString() {
		return this._tree.toString();
	}
	
	@Override
	public void insert(double score, T value) {
		this._tree.insert(new AVLTopKMachineEntry<T>(score, value));
	}

	@Override
	public ArrayList<T> getTopK() {
		final ArrayList<T> ret = new ArrayList<T>(this._tree.getCount());
		this._tree.traverse((n) -> {
			ret.add(n.getValue().getData());
		});
		return ret;
	}

	@Override
	public double getCurrentCutoff() {
		AVLTopKMachineEntry<T> cutoff = this._tree.getCutoff();
		return cutoff == null ? Double.POSITIVE_INFINITY : cutoff.getScore();
	}
	
	/**
	 * Returns K for this AVLTopKMachine.
	 * @return K
	 */
	public int getK() {
		return this._tree.getK();
	}
}
