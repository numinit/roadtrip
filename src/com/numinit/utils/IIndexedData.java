package com.numinit.utils;
/**
 * An interface to an indexed element with an integer index into its
 * enclosing container and data of parameterized type T.
 */
public interface IIndexedData<T> {
    /**
     * Get index of this item.
     *
     * @return  index in enclosing container
     */
    int getIndex();

    /**
     * Get data.
     *
     * @return  data element
     */
    T   getData();
}